package leaseAgreement.persistence;

import leaseAgreement.api.model.LeaseAgreement;

public interface LeaseAgreementRepository {
    void save(LeaseAgreement leaseAgreement);

    LeaseAgreement findById(String id);

}
