package reviewApplicant.service.impl;

import authentication.api.model.User;
import authentication.api.service.UserService;
import landlordProperty.model.Property;
import landlordProperty.service.PropertyService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reviewApplicant.model.ApplicantReview;
import reviewApplicant.repository.ApplicantReviewRepository;
import reviewApplicant.repository.ApplicationReaderRepository;
import tenantApplication.model.Application;
import tenantApplication.repository.TenantApplicationRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewApplicantServiceImplTest {

    @Mock
    private ApplicantReviewRepository applicantReviewRepository;
    @Mock
    private TenantApplicationRepository tenantApplicationRepository;
    @Mock
    private PropertyService propertyService;
    @Mock
    private ApplicationReaderRepository applicationReaderRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private ReviewApplicantServiceImpl reviewApplicantService;

    private Application mockApplication;
    private Property mockProperty;
    private User mockTenant;

    @BeforeEach
    void setUp() {
        mockApplication = new Application();
        mockApplication.setId("APP1");
        mockApplication.setTenantId("T1");
        mockApplication.setPropertyId("P1");
        mockApplication.setStatus(Application.ApplicationStatus.PENDING);

        mockProperty = new Property();
        mockProperty.setId("P1");
        mockProperty.setLandlordId("L1");

        mockTenant = new User();
        mockTenant.setId(new ObjectId()); // Fixed: Use ObjectId
        mockTenant.setUsername("John Doe");
        mockTenant.setOverallRating(5.0);
    }

    @Test
    void testAcceptApplication_Success() {
        when(tenantApplicationRepository.findById("APP1")).thenReturn(Optional.of(mockApplication));
        when(propertyService.getPropertyById("P1")).thenReturn(Optional.of(mockProperty));

        reviewApplicantService.acceptApplication("APP1", "L1", "Welcome!");

        assertEquals(Application.ApplicationStatus.APPROVED, mockApplication.getStatus());
        verify(tenantApplicationRepository).update(mockApplication);
        verify(applicantReviewRepository).save(any(ApplicantReview.class));
    }

    @Test
    void testAcceptApplication_UnauthorizedLandlord() {
        when(tenantApplicationRepository.findById("APP1")).thenReturn(Optional.of(mockApplication));
        when(propertyService.getPropertyById("P1")).thenReturn(Optional.of(mockProperty));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> reviewApplicantService.acceptApplication("APP1", "WRONG_LANDLORD", "Welcome!"));

        assertEquals("Unauthorized: Landlord does not own this property", exception.getMessage());
        verify(tenantApplicationRepository, never()).update(any());
        verify(applicantReviewRepository, never()).save(any());
    }

    @Test
    void testRejectApplication_Success() {
        when(tenantApplicationRepository.findById("APP1")).thenReturn(Optional.of(mockApplication));
        when(propertyService.getPropertyById("P1")).thenReturn(Optional.of(mockProperty));

        reviewApplicantService.rejectApplication("APP1", "L1", "Sorry");

        assertEquals(Application.ApplicationStatus.REJECTED, mockApplication.getStatus());
        verify(tenantApplicationRepository).update(mockApplication);
        verify(applicantReviewRepository).save(any(ApplicantReview.class));
    }

    @Test
    void testGetApplicationsForLandlord_Success() {
        when(propertyService.getAllProperties()).thenReturn(Collections.singletonList(mockProperty));
        when(applicationReaderRepository.findByPropertyIds(Collections.singletonList("P1")))
                .thenReturn(Collections.singletonList(mockApplication));
        when(userService.getUserById("T1")).thenReturn(mockTenant);

        List<Map<String, Object>> result = reviewApplicantService.getApplicationsForLandlord("L1");

        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).get("applicantName"));
        assertEquals(5.0, result.get(0).get("tenantRating"));
    }

    @Test
    void testContactApplicant_Success() {
        when(tenantApplicationRepository.findById("APP1")).thenReturn(Optional.of(mockApplication));
        when(userService.getUserById("T1")).thenReturn(mockTenant);

        reviewApplicantService.contactApplicant("APP1", "Hello");

        // Just verify no exception is thrown; essentially manual verification of
        // "system.out" or email service mock
        verify(userService).getUserById("T1");
    }
}
