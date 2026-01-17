package leaseAgreement.service;


import java.util.List;
import java.util.Optional;

import leaseAgreement.api.model.LeaseAgreementStatus;
import leaseAgreement.persistence.LeaseAgreementRepository;
import org.bson.types.ObjectId;
import org.osgi.service.component.annotations.Component;

import leaseAgreement.api.model.LeaseAgreementModel;
import leaseAgreement.api.service.LeaseAgreementService;
import org.osgi.service.component.annotations.Reference;

@Component(service = LeaseAgreementService.class)
public class LeaseAgreementServiceImpl implements LeaseAgreementService {

    @Reference
    private LeaseAgreementRepository leaseAgreementRepository;

    public LeaseAgreementServiceImpl() {
    }

    @Override
    public Optional<LeaseAgreementModel> findById(String id) {
        return Optional.ofNullable(leaseAgreementRepository.findById(id));
    }

    @Override
    public Optional<LeaseAgreementModel> findByApplicationId(String applicationId) {
        return Optional.ofNullable(leaseAgreementRepository.findByApplicationId(applicationId));
    }

    @Override
    public List<LeaseAgreementModel> findByTenantId(String tenantId) {
        return leaseAgreementRepository.findByTenantIdOrderByDayDesc(tenantId);
    }


    @Override
    public LeaseAgreementModel save(LeaseAgreementModel lease) {
        if (lease.getId() == null) {
            lease.setId(new ObjectId());
            leaseAgreementRepository.save(lease);
        } else {
            leaseAgreementRepository.update(lease);
        }
        return lease;
    }

    public void updateLeaseFields(LeaseAgreementModel target, LeaseAgreementModel source) {
        target.setDay(source.getDay());
        target.setMonth(source.getMonth());
        target.setYear(source.getYear());
        target.setRentRmNum(source.getRentRmNum());
    }

    @Override
    public Optional<LeaseAgreementModel> updateLease(
            String applicationId,
            LeaseAgreementModel data
    ) {
        return findByApplicationId(applicationId).map(existing -> {
            updateLeaseFields(existing, data);
            existing.setLeaseStatus(LeaseAgreementStatus.UNDER_REVIEW_BY_TENANT);
            return save(existing);
        });
    }

    @Override
    public Optional<LeaseAgreementModel> submitLandlordLease(
            String applicationId,
            LeaseAgreementModel data
    ) {
        if(findByApplicationId(applicationId).isPresent()){
            return Optional.empty();
        }
        data.setApplicationId(applicationId);
        data.setLeaseStatus(LeaseAgreementStatus.UNDER_REVIEW_BY_TENANT);
        return Optional.of(save(data));
    }

    @Override
    public Optional<LeaseAgreementModel> submitTenantLease(
            String leaseId,
            String lesseeIc,
            String lesseeDesignation,
            String lesseeSignature
    ) {
        return findById(leaseId).map(lease -> {
            lease.setLesseeIc(lesseeIc);
            lease.setLesseeDesignation(lesseeDesignation);
            lease.setLesseeSignature(lesseeSignature);
            lease.setLeaseStatus(LeaseAgreementStatus.EFFECTIVE);
            return save(lease);
        });
    }

    @Override
    public Optional<LeaseAgreementModel> savePdf(String leaseId, String pdfBase64) {
        return findById(leaseId).map(lease -> {
            lease.setPdf(pdfBase64);
            return save(lease);
        });
    }
}