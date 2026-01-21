package reviewApplicant.model;

import java.util.Date;

public class ApplicantReview {
    private String id;
    private String applicationId;
    private String landlordId;
    private String tenantId;
    private String propertyId;
    private ReviewDecision decision;
    private String feedback;
    private Date reviewDate;

    public enum ReviewDecision {
        APPROVED,
        REJECTED
    }

    public ApplicantReview() {}

    public ApplicantReview(String applicationId, String landlordId, String tenantId, String propertyId, ReviewDecision decision, String feedback) {
        this.applicationId = applicationId;
        this.landlordId = landlordId;
        this.tenantId = tenantId;
        this.propertyId = propertyId;
        this.decision = decision;
        this.feedback = feedback;
        this.reviewDate = new Date();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getApplicationId() { return applicationId; }
    public void setApplicationId(String applicationId) { this.applicationId = applicationId; }

    public String getLandlordId() { return landlordId; }
    public void setLandlordId(String landlordId) { this.landlordId = landlordId; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId = propertyId; }

    public ReviewDecision getDecision() { return decision; }
    public void setDecision(ReviewDecision decision) { this.decision = decision; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public Date getReviewDate() { return reviewDate; }
    public void setReviewDate(Date reviewDate) { this.reviewDate = reviewDate; }
}
