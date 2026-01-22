package tenantApplication.service.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import tenantApplication.model.Application;
import tenantApplication.service.TenantApplicationService;
import tenantApplication.repository.TenantApplicationRepository;
import landlordProperty.service.PropertyService;
import landlordProperty.model.Property;

import java.util.List;
import java.util.Optional;

@Component(service = TenantApplicationService.class)
public class TenantApplicationServiceImpl implements TenantApplicationService {

    @Reference
    private TenantApplicationRepository tenantApplicationRepository;

    @Reference
    private PropertyService propertyService;

    @Override
    public List<Property> getAvailableProperties(Double maxPrice, String type, String furnishing) {
        return propertyService.filterProperties(maxPrice, type, furnishing);
    }

    @Override
    public Property getPropertyDetails(String propertyId) {
        return propertyService.getPropertyById(propertyId).orElse(null);
    }

    @Override
    public Application submitApplication(String tenantId, String propertyId, Double monthlyIncome, String occupation, String message) {
        if (tenantId == null || tenantId.isEmpty()) throw new RuntimeException("Tenant ID is required");
        if (propertyId == null || propertyId.isEmpty()) throw new RuntimeException("Property ID is required");
        if (monthlyIncome == null || monthlyIncome <= 0) throw new RuntimeException("Monthly income must be greater than 0");
        if (occupation == null || occupation.isEmpty()) throw new RuntimeException("Occupation is required");
        if (message == null || message.isEmpty()) throw new RuntimeException("Message is required");

        // Validation: Property exists?
        Property prop = getPropertyDetails(propertyId);
        if (prop == null) {
            throw new RuntimeException("Property not found");
        }

        // Check for duplicate pending applications via repository
        Optional<Application> existingApp = tenantApplicationRepository.findPendingApplication(tenantId, propertyId);
        if (existingApp.isPresent()) {
            throw new RuntimeException("You already have a pending application for this property.");
        }

        Application app = new Application(tenantId, propertyId, monthlyIncome, occupation, message);
        tenantApplicationRepository.save(app);
        
        return app;
    }

    @Override
    public List<Application> getTenantApplications(String tenantId) {
        return tenantApplicationRepository.findByTenantId(tenantId);
    }

    @Override
    public Application cancelApplication(String applicationId, String tenantId) {
        Optional<Application> appOpt = tenantApplicationRepository.findById(applicationId);
        if (!appOpt.isPresent()) throw new RuntimeException("Application not found");
        
        Application app = appOpt.get();
        if (!app.getTenantId().equals(tenantId)) throw new RuntimeException("Unauthorized");
        if (app.getStatus() != Application.ApplicationStatus.PENDING) throw new RuntimeException("Cannot cancel non-pending application");

        app.setStatus(Application.ApplicationStatus.CANCELLED);
        tenantApplicationRepository.update(app);
        return app;
    }

    @Override
    public void deleteApplication(String applicationId, String tenantId) {
        Optional<Application> appOpt = tenantApplicationRepository.findById(applicationId);
        if (appOpt.isPresent()) {
            Application app = appOpt.get();
            if (!app.getTenantId().equals(tenantId)) throw new RuntimeException("Unauthorized");
            if (app.getStatus() != Application.ApplicationStatus.REJECTED) throw new RuntimeException("Only rejected applications can be deleted");

            tenantApplicationRepository.delete(applicationId);
        }
    }
}
