package tenantApplication.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tenantApplication.model.Application;
import tenantApplication.repository.TenantApplicationRepository;
import landlordProperty.service.PropertyService;
import landlordProperty.model.Property;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TenantApplicationServiceImplTest {

    @Mock
    private TenantApplicationRepository tenantApplicationRepository;

    @Mock
    private PropertyService propertyService;

    @InjectMocks
    private TenantApplicationServiceImpl tenantApplicationService;

    // Method: submitApplication()
    // Scenario 1: Successful submission
    @Test
    void submitApplication_Success() {
        String tenantId = "T123";
        String propertyId = "P456";
        Double income = 5000.0;
        String occupation = "Engineer";
        String message = "Hi";
        
        Property mockProperty = new Property();
        mockProperty.setId(propertyId);

        when(propertyService.getPropertyById(propertyId)).thenReturn(Optional.of(mockProperty));
        when(tenantApplicationRepository.findPendingApplication(tenantId, propertyId)).thenReturn(Optional.empty());

        Application result = tenantApplicationService.submitApplication(tenantId, propertyId, income, occupation, message);

        assertNotNull(result);
        assertEquals(tenantId, result.getTenantId());
        assertEquals(Application.ApplicationStatus.PENDING, result.getStatus());
        verify(tenantApplicationRepository, times(1)).save(any(Application.class));
    }

    // Scenario 2: Duplicate pending application
    @Test
    void submitApplication_DuplicatePending() {
        String tenantId = "T123";
        String propertyId = "P456";
        
        Property mockProperty = new Property();
        when(propertyService.getPropertyById(propertyId)).thenReturn(Optional.of(mockProperty));
        
        Application existingApp = new Application();
        existingApp.setStatus(Application.ApplicationStatus.PENDING);
        when(tenantApplicationRepository.findPendingApplication(tenantId, propertyId)).thenReturn(Optional.of(existingApp));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            tenantApplicationService.submitApplication(tenantId, propertyId, 5000.0, "Eng", "Hi")
        );
        assertEquals("You already have a pending application for this property.", exception.getMessage());
        verify(tenantApplicationRepository, never()).save(any(Application.class));
    }

    // Scenario 3: Property not found
    @Test
    void submitApplication_PropertyNotFound() {
        when(propertyService.getPropertyById("P999")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            tenantApplicationService.submitApplication("T1", "P999", 5000.0, "Occ", "Msg")
        );
        assertEquals("Property not found", exception.getMessage());
    }

    // Method: cancelApplication()
    // Scenario 4: Successful cancellation
    @Test
    void cancelApplication_Success() {
        String appId = "APP001";
        String tenantId = "T123";
        Application app = new Application();
        app.setTenantId(tenantId);
        app.setStatus(Application.ApplicationStatus.PENDING);

        when(tenantApplicationRepository.findById(appId)).thenReturn(Optional.of(app));

        Application result = tenantApplicationService.cancelApplication(appId, tenantId);

        assertEquals(Application.ApplicationStatus.CANCELLED, result.getStatus());
        verify(tenantApplicationRepository, times(1)).update(app);
    }

    // Scenario 5: Unauthorized cancellation
    @Test
    void cancelApplication_Unauthorized() {
        String appId = "APP001";
        Application app = new Application();
        app.setTenantId("OTHER_TENANT");
        
        when(tenantApplicationRepository.findById(appId)).thenReturn(Optional.of(app));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            tenantApplicationService.cancelApplication(appId, "T123")
        );
        assertEquals("Unauthorized", exception.getMessage());
    }
}
