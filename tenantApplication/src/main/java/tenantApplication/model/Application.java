package tenantApplication.model;

import java.util.Date;

public class Application {
    private String id;
    private String tenantId;
    private String propertyId;
    private ApplicationStatus status;
    private Date applicationDate;
    
    private Double monthlyIncome;
    private String occupation;
    private String message;
    
    public enum ApplicationStatus {
        PENDING,
        APPROVED,
        REJECTED,
        CANCELLED
    }

    public Application() {}

    public Application(String tenantId, String propertyId, Double monthlyIncome, String occupation, String message) {
        this.tenantId = tenantId;
        this.propertyId = propertyId;
        this.monthlyIncome = monthlyIncome;
        this.occupation = occupation;
        this.message = message;
        this.status = ApplicationStatus.PENDING;
        this.applicationDate = new Date();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public String getPropertyId() { return propertyId; }
    public void setPropertyId(String propertyId) { this.propertyId = propertyId; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public Date getApplicationDate() { return applicationDate; }
    public void setApplicationDate(Date applicationDate) { this.applicationDate = applicationDate; }
    public Double getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(Double monthlyIncome) { this.monthlyIncome = monthlyIncome; }
    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
