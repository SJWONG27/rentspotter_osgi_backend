package leaseAgreement.persistence;

import leaseAgreement.api.model.LeaseAgreementModel;

import java.util.List;

public interface LeaseAgreementRepository {
    void save(LeaseAgreementModel lease);

    void update(LeaseAgreementModel lease);

    LeaseAgreementModel findById(String leaseId);

    LeaseAgreementModel findByApplicationId(String applicationId);

    List<LeaseAgreementModel> findByTenantIdOrderByDayDesc(String tenantId);
}
