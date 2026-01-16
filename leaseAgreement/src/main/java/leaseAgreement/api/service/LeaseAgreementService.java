package leaseAgreement.api.service;

import leaseAgreement.api.model.LeaseAgreementModel;

import java.util.List;
import java.util.Optional;

public interface LeaseAgreementService {
    Optional<LeaseAgreementModel> findById(String id);
    Optional<LeaseAgreementModel> findByApplicationId(String applicationId);
    List<LeaseAgreementModel> findByTenantId(String tenantId);
    LeaseAgreementModel save(LeaseAgreementModel lease);
}