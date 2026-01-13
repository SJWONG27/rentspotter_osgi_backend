package leaseAgreement.service;

import authentication.persistence.repository.UserRepository;
import leaseAgreement.api.model.LeaseAgreement;
import leaseAgreement.api.service.LeaseAgreementService;
import leaseAgreement.persistence.LeaseAgreementRepository;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;


@Component(service = LeaseAgreementService.class)
public class LeaseServiceImp implements LeaseAgreementService {
    public LeaseServiceImp() {
    }

    @Reference
    private LeaseAgreementRepository leaseAgreementRepository;

    @Reference
    private UserRepository userRepository;

    @Override
    public LeaseAgreement getLeaseById(String leaseId) {
        return null;
    }

    @Override
    public List<LeaseAgreement> getLeasesByUserId(String userId) {
        return List.of();
    }

    @Override
    public String submitLandlordLease(String applicationId, LeaseAgreement leaseData) {
        return "";
    }

    @Override
    public void submitTenantLease(String leaseId, String lesseeIc, String lesseeDesignation, String lesseeSignature) {

    }

    @Override
    public void savePdf(String leaseId, String pdfBase64) {

    }

    @Override
    public String getPdf(String leaseId) {
        return "";
    }

//    @Override
//    public String submitLandlordLeaseAgreement(String )
}
