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
import java.util.List;
import java.util.Collections;

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

    // Scenario 3: Input Validation for Application Submission
    @Test
    void submitApplication_InvalidInput() {
        // Test 1: Empty Tenant ID
        assertThrows(RuntimeException.class, () -> 
            tenantApplicationService.submitApplication("", "P1", 100.0, "Occ", "Msg"), "Tenant ID is required"
        );

        // Test 2: Empty Property ID
        assertThrows(RuntimeException.class, () -> 
            tenantApplicationService.submitApplication("T1", "", 100.0, "Occ", "Msg"), "Property ID is required"
        );

        // Test 3: Zero or Negative Income
        assertThrows(RuntimeException.class, () -> 
            tenantApplicationService.submitApplication("T1", "P1", 0.0, "Occ", "Msg"), "Monthly income must be greater than 0"
        );

        // Test 4: Empty Occupation
        assertThrows(RuntimeException.class, () -> 
            tenantApplicationService.submitApplication("T1", "P1", 100.0, "", "Msg"), "Occupation is required"
        );

         // Test 5: Empty Message
        assertThrows(RuntimeException.class, () -> 
            tenantApplicationService.submitApplication("T1", "P1", 100.0, "Occ", ""), "Message is required"
        );
    }

    // Scenario 4: Property not found
    @Test
    void submitApplication_PropertyNotFound() {
        when(propertyService.getPropertyById("P999")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            tenantApplicationService.submitApplication("T1", "P999", 5000.0, "Occ", "Msg")
        );
        assertEquals("Property not found", exception.getMessage());
    }

    // Method: getTenantApplications()
    // Scenario 1: Retrieving Tenant Applications
    @Test
    void getTenantApplications_Success() {
        String tenantId = "T123";
        List<Application> mockList = java.util.Collections.singletonList(new Application());
        when(tenantApplicationRepository.findByTenantId(tenantId)).thenReturn(mockList);

        List<Application> result = tenantApplicationService.getTenantApplications(tenantId);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(tenantApplicationRepository, times(1)).findByTenantId(tenantId);
    }

    // Method: cancelApplication()
    // Scenario 1: Successful cancellation
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

    // Scenario 2 & 3: Unauthorized & Not Pending
    @Test
    void cancelApplication_Validation() {
        String appId = "APP001";
        
        // Case 1: Unauthorized
        Application app1 = new Application();
        app1.setTenantId("OTHER_TENANT");
        when(tenantApplicationRepository.findById(appId)).thenReturn(Optional.of(app1));
        
        RuntimeException ex1 = assertThrows(RuntimeException.class, () ->
            tenantApplicationService.cancelApplication(appId, "T123")
        );
        assertEquals("Unauthorized", ex1.getMessage());

        // Case 2: Not Pending
        Application app2 = new Application();
        app2.setTenantId("T123");
        app2.setStatus(Application.ApplicationStatus.APPROVED);
        when(tenantApplicationRepository.findById(appId)).thenReturn(Optional.of(app2));

        RuntimeException ex2 = assertThrows(RuntimeException.class, () ->
            tenantApplicationService.cancelApplication(appId, "T123")
        );
        assertEquals("Cannot cancel non-pending application", ex2.getMessage());
    }

    // Method: deleteApplication()
    // Scenario 1 & 2: Deleting a Rejected Application & Deleting a Non-Rejected Application
    @Test
    void deleteApplication_Scenario() {
        String appId = "APP001";
        String tenantId = "T123";

        // Case 1: Success (Rejected)
        Application app1 = new Application();
        app1.setTenantId(tenantId);
        app1.setStatus(Application.ApplicationStatus.REJECTED);
        when(tenantApplicationRepository.findById(appId)).thenReturn(Optional.of(app1));

        tenantApplicationService.deleteApplication(appId, tenantId);
        verify(tenantApplicationRepository, times(1)).delete(appId);

        // Case 2: Failure (Not Rejected, e.g. Pending)
        Application app2 = new Application();
        app2.setTenantId(tenantId);
        app2.setStatus(Application.ApplicationStatus.PENDING);
        when(tenantApplicationRepository.findById(appId)).thenReturn(Optional.of(app2));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            tenantApplicationService.deleteApplication(appId, tenantId)
        );
        assertEquals("Only rejected applications can be deleted", ex.getMessage());
    }

}
