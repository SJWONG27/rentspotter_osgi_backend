package reviewApplicant.rest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import reviewApplicant.model.ApplicantReview;
import reviewApplicant.service.ReviewApplicantService;
import tenantApplication.model.Application;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Component(
    service = ReviewApplicantController.class, 
    property = { "osgi.jaxrs.resource=true" }
)
@Path("/api/review")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReviewApplicantController {

    @Reference
    private ReviewApplicantService reviewApplicantService;

    /**
     * UC-12: View applicants for landlord's properties
     * GET /api/review/landlord/{landlordId}/applications
     */
    @GET
    @Path("/landlord/{landlordId}/applications")
    public Response getApplicationsForLandlord(
            @PathParam("landlordId") String landlordId,
            @QueryParam("sort") String sortOrder
    ) {
        try {
            List<Application> applications;
            
            if (sortOrder != null && !sortOrder.isEmpty()) {
                applications = reviewApplicantService.getApplicationsForLandlordSorted(landlordId, sortOrder);
            } else {
                applications = reviewApplicantService.getApplicationsForLandlord(landlordId);
            }
            
            return Response.ok(applications).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    /**
     * UC-13: Accept or reject an applicant
     * POST /api/review/application/{applicationId}/review
     * Body: { "landlordId": "...", "decision": "APPROVED"/"REJECTED", "feedback": "..." }
     */
    @POST
    @Path("/application/{applicationId}/review")
    public Response reviewApplication(
            @PathParam("applicationId") String applicationId,
            Map<String, Object> payload
    ) {
        try {
            String landlordId = (String) payload.get("landlordId");
            String decisionStr = (String) payload.get("decision");
            String feedback = (String) payload.get("feedback");

            if (landlordId == null || decisionStr == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "landlordId and decision are required"))
                        .build();
            }

            ApplicantReview.ReviewDecision decision;
            try {
                decision = ApplicantReview.ReviewDecision.valueOf(decisionStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "Invalid decision. Use APPROVED or REJECTED"))
                        .build();
            }

            ApplicantReview review = reviewApplicantService.reviewApplication(
                    applicationId, landlordId, decision, feedback
            );

            return Response.ok(review).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    /**
     * UC-14: Leave feedback on tenant (integrated with accept/reject above)
     * This is handled by the reviewApplication endpoint with the feedback field
     */

    /**
     * View review history for a landlord
     * GET /api/review/landlord/{landlordId}/history
     */
    @GET
    @Path("/landlord/{landlordId}/history")
    public Response getReviewHistory(@PathParam("landlordId") String landlordId) {
        try {
            List<ApplicantReview> reviews = reviewApplicantService.getReviewHistory(landlordId);
            return Response.ok(reviews).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
}
