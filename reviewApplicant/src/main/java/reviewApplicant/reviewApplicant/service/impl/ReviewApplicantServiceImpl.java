package reviewApplicant.service.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import authentication.api.service.UserService;
import authentication.api.model.User;
import reviewApplicant.model.ApplicantReview;
import reviewApplicant.repository.ApplicantReviewRepository;
import reviewApplicant.repository.ApplicationReaderRepository;
import reviewApplicant.service.ReviewApplicantService;
import tenantApplication.model.Application;
import tenantApplication.repository.TenantApplicationRepository;
import landlordProperty.model.Property;
import landlordProperty.service.PropertyService;

import java.util.*;
import java.util.stream.Collectors;

@Component(service = ReviewApplicantService.class)
public class ReviewApplicantServiceImpl implements ReviewApplicantService {

    @Reference
    private ApplicantReviewRepository applicantReviewRepository;

    @Reference
    private TenantApplicationRepository tenantApplicationRepository;

    @Reference
    private PropertyService propertyService;

    @Reference
    private ApplicationReaderRepository applicationReaderRepository;

    @Reference
    private UserService userService;

    @Override
    public List<Map<String, Object>> getApplicationsForLandlord(String landlordId) {
        // 1. Get properties
        List<Property> allProperties = propertyService.getAllProperties();
        List<String> landlordPropertyIds = allProperties.stream()
                .filter(p -> landlordId.equals(p.getLandlordId()))
                .map(Property::getId)
                .collect(Collectors.toList());

        // 2. Get applications
        List<Application> applications = applicationReaderRepository.findByPropertyIds(landlordPropertyIds);

        // 3. Enrich
        return enrichApplications(applications);
    }

    private List<Map<String, Object>> enrichApplications(List<Application> apps) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Application app : apps) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", app.getId());
            map.put("tenantId", app.getTenantId());
            map.put("propertyId", app.getPropertyId());
            map.put("status", app.getStatus());
            map.put("message", app.getMessage());
            map.put("monthlyIncome", app.getMonthlyIncome());
            map.put("occupation", app.getOccupation());
            map.put("applicationDate", app.getApplicationDate());

            // Enrich Name & Rating from User Service
            try {
                User user = userService.getUserById(app.getTenantId());
                if (user != null) {
                    map.put("applicantName", user.getUsername());
                    map.put("tenantRating", user.getOverallRating() != null ? user.getOverallRating() : 0.0);
                } else {
                    map.put("applicantName", "Unknown");
                    map.put("tenantRating", 0.0);
                }
            } catch (Exception e) {
                map.put("applicantName", "Error fetching data");
                map.put("tenantRating", 0.0);
            }

            result.add(map);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getApplicationsForLandlordSorted(String landlordId, String sortOrder) {
        List<Map<String, Object>> applications = getApplicationsForLandlord(landlordId);

        Comparator<Map<String, Object>> comparator = (m1, m2) -> {
            Double r1 = (Double) m1.getOrDefault("tenantRating", 0.0);
            Double r2 = (Double) m2.getOrDefault("tenantRating", 0.0);
            return r1.compareTo(r2);
        };

        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }

        return applications.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @Override
    public void acceptApplication(String applicationId, String landlordId, String feedback) {
        processDecision(applicationId, landlordId, ApplicantReview.ReviewDecision.APPROVED, feedback);
    }

    @Override
    public void rejectApplication(String applicationId, String landlordId, String feedback) {
        processDecision(applicationId, landlordId, ApplicantReview.ReviewDecision.REJECTED, feedback);
    }

    private void processDecision(String applicationId, String landlordId, ApplicantReview.ReviewDecision decision,
            String feedback) {
        Application application = tenantApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        Property property = propertyService.getPropertyById(application.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!landlordId.equals(property.getLandlordId())) {
            throw new RuntimeException("Unauthorized: Landlord does not own this property");
        }

        // Only Pending applications? (Optional check, but good practice)

        if (decision == ApplicantReview.ReviewDecision.APPROVED) {
            application.setStatus(Application.ApplicationStatus.APPROVED);
        } else {
            application.setStatus(Application.ApplicationStatus.REJECTED);
        }
        tenantApplicationRepository.update(application);

        ApplicantReview review = new ApplicantReview(
                applicationId,
                landlordId,
                application.getTenantId(),
                application.getPropertyId(),
                decision,
                feedback);
        applicantReviewRepository.save(review);
    }

    @Override
    public List<ApplicantReview> getReviewHistory(String landlordId) {
        return applicantReviewRepository.findByLandlordId(landlordId);
    }

    @Override
    public void contactApplicant(String applicationId, String message) {
        Application application = tenantApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        User tenant = userService.getUserById(application.getTenantId());
        String email = (tenant != null) ? tenant.getEmail() : "unknown@email.com";

        System.out.println(">>> [EMAIL SERVICE] Sending email to: " + email);
        System.out.println(">>> Body: " + message);
    }

    @Override
    public Map<String, Object> getApplicantDetails(String tenantId) {
        User user = userService.getUserById(tenantId);
        if (user == null) {
            throw new RuntimeException("Tenant not found");
        }

        List<ApplicantReview> reviews = applicantReviewRepository.findByTenantId(tenantId);

        Map<String, Object> result = new HashMap<>();
        result.put("tenant", user);
        result.put("reviews", reviews);

        return result;
    }
}
