package tenantApplication.service;

import tenantApplication.model.Application;
import landlordProperty.model.Property;
import java.util.List;

public interface TenantApplicationService {
    List<Property> getAvailableProperties(Double maxPrice, String type, String furnishing);
    Property getPropertyDetails(String propertyId);
    Application submitApplication(String tenantId, String propertyId, Double monthlyIncome, String occupation, String message);
    List<Application> getTenantApplications(String tenantId);
    Application cancelApplication(String applicationId, String tenantId);
    void deleteApplication(String applicationId, String tenantId);
}
