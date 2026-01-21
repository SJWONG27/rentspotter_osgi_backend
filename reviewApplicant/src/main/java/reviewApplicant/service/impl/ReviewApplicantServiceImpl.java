package reviewApplicant.service.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import reviewApplicant.model.ApplicantReview;
import reviewApplicant.repository.ApplicantReviewRepository;
import reviewApplicant.repository.ApplicationReaderRepository;
import reviewApplicant.service.ReviewApplicantService;
import tenantApplication.model.Application;
import tenantApplication.repository.TenantApplicationRepository;
import landlordProperty.model.Property;
import landlordProperty.service.PropertyService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

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

    @Override
    public List<Application> getApplicationsForLandlord(String landlordId) {
        // Get all properties for this landlord
        List<Property> allProperties = propertyService.getAllProperties();
        List<String> landlordPropertyIds = allProperties.stream()
                .filter(p -> landlordId.equals(p.getLandlordId()))
                .map(Property::getId)
                .collect(Collectors.toList());

        // Use our local reader to get applications by property IDs
        return applicationReaderRepository.findByPropertyIds(landlordPropertyIds);
    }

    @Override
    public List<Application> getApplicationsForLandlordSorted(String landlordId, String sortOrder) {
        List<Application> applications = getApplicationsForLandlord(landlordId);
        
        // Sort by application date
        Comparator<Application> comparator = Comparator.comparing(Application::getApplicationDate);
        
        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }
        
        return applications.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @Override
    public ApplicantReview reviewApplication(String applicationId, String landlordId, 
                                             ApplicantReview.ReviewDecision decision, String feedback) {
        Application application = tenantApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        Property property = propertyService.getPropertyById(application.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!landlordId.equals(property.getLandlordId())) {
            throw new RuntimeException("Unauthorized: Landlord does not own this property");
        }

        // Update application status
        if (decision == ApplicantReview.ReviewDecision.APPROVED) {
            application.setStatus(Application.ApplicationStatus.APPROVED);
        } else {
            application.setStatus(Application.ApplicationStatus.REJECTED);
        }
        tenantApplicationRepository.update(application);

        // Create and save review
        ApplicantReview review = new ApplicantReview(
                applicationId,
                landlordId,
                application.getTenantId(),
                application.getPropertyId(),
                decision,
                feedback
        );

        return applicantReviewRepository.save(review);
    }

    @Override
    public List<ApplicantReview> getReviewHistory(String landlordId) {
        return applicantReviewRepository.findByLandlordId(landlordId);
    }
}
