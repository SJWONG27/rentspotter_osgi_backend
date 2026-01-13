package leaseAgreement.api.service;

import leaseAgreement.api.model.LeaseAgreement;
import java.util.*;

public interface LeaseAgreementService {

    LeaseAgreement getLeaseById(String leaseId);
    List<LeaseAgreement> getLeasesByUserId(String userId);

    // Corresponds to submitLandlordLeaseAgreement
    String submitLandlordLease(String applicationId, LeaseAgreement leaseData);

    // Corresponds to submitTenantLeaseAgreement
    void submitTenantLease(String leaseId, String lesseeIc, String lesseeDesignation, String lesseeSignature);

    // Corresponds to savePDFToDB and getPDFFromDB
    void savePdf(String leaseId, String pdfBase64);
    String getPdf(String leaseId);

}
