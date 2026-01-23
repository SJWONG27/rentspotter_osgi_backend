package reviewApplicant.service;

import reviewApplicant.model.ApplicantReview;
import java.util.List;
import java.util.Map;

public interface ReviewApplicantService {
    List<Map<String, Object>> getApplicationsForLandlord(String landlordId);

    List<Map<String, Object>> getApplicationsForLandlordSorted(String landlordId, String sortOrder);

    void acceptApplication(String applicationId, String landlordId, String feedback);

    void rejectApplication(String applicationId, String landlordId, String feedback);

    List<ApplicantReview> getReviewHistory(String landlordId);

    void contactApplicant(String applicationId, String message);

    Map<String, Object> getApplicantDetails(String tenantId);
}
