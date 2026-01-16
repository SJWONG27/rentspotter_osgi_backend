package leaseAgreement.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import leaseAgreement.persistence.LeaseAgreementRepository;
import org.bson.types.ObjectId;
import org.osgi.service.component.annotations.Component;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;

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
}