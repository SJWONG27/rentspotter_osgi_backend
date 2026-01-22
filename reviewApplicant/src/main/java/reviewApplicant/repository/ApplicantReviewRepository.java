package reviewApplicant.repository;

import reviewApplicant.model.ApplicantReview;
import java.util.List;
import java.util.Optional;

public interface ApplicantReviewRepository {
    ApplicantReview save(ApplicantReview review);

    Optional<ApplicantReview> findById(String id);

    List<ApplicantReview> findByLandlordId(String landlordId);

    List<ApplicantReview> findByTenantId(String tenantId);

    Optional<ApplicantReview> findByApplicationId(String applicationId);

    Optional<ApplicantReview> findByApplicationIdAndLandlordId(String applicationId, String landlordId);

    List<ApplicantReview> findAll();

    void deleteById(String id);
}
