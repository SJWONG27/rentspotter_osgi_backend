package reviewApplicant.service;

import reviewApplicant.model.ApplicantReview;
import tenantApplication.model.Application;

import java.util.List;

public interface ReviewApplicantService {
    List<Application> getApplicationsForLandlord(String landlordId);
    List<Application> getApplicationsForLandlordSorted(String landlordId, String sortOrder);
    ApplicantReview reviewApplication(String applicationId, String landlordId, ApplicantReview.ReviewDecision decision, String feedback);
    List<ApplicantReview> getReviewHistory(String landlordId);
}
