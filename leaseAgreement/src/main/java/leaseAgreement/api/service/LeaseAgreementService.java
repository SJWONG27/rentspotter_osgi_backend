package leaseAgreement.api.service;

import leaseAgreement.api.model.LeaseAgreementModel;

import java.util.List;
import java.util.Optional;

public interface LeaseAgreementService {
    Optional<LeaseAgreementModel> findById(String id);
    Optional<LeaseAgreementModel> findByApplicationId(String applicationId);
    List<LeaseAgreementModel> findByTenantId(String tenantId);
    LeaseAgreementModel save(LeaseAgreementModel lease);
    Optional<LeaseAgreementModel> updateLease(String applicationId, LeaseAgreementModel data);
    Optional<LeaseAgreementModel> submitLandlordLease(
            String applicationId,
            LeaseAgreementModel data
    );
    Optional<LeaseAgreementModel> submitTenantLease(
            String leaseId,
            String lesseeIc,
            String lesseeDesignation,
            String lesseeSignature
    );

    Optional<LeaseAgreementModel> savePdf(String leaseId, String pdfBase64);
}