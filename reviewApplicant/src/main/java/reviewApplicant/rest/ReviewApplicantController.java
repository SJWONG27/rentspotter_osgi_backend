package reviewApplicant.rest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import reviewApplicant.model.ApplicantReview;
import reviewApplicant.service.ReviewApplicantService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Component(service = ReviewApplicantController.class, property = {
        "osgi.jaxrs.resource=true",
        "osgi.jaxrs.application.select=(osgi.jaxrs.name=.default)"
})
@Path("/api/landlord/applicants")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReviewApplicantController {

    @Reference
    private ReviewApplicantService reviewApplicantService;

    // UC-26 & UC-27: View & Sort Applicant List
    @GET
    @Path("/{landlordId}")
    public Response getApplicationsForLandlord(
            @PathParam("landlordId") String landlordId,
            @QueryParam("sortBy") String sortBy,
            @QueryParam("order") String sortOrder) {
        try {
            List<Map<String, Object>> applications;

            if ("rating".equalsIgnoreCase(sortBy)) {
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

    // View Applicant Details (Info + Reviews)
    @GET
    @Path("/applicant-info/{tenantId}")
    public Response getApplicantDetails(@PathParam("tenantId") String tenantId) {
        try {
            Map<String, Object> details = reviewApplicantService.getApplicantDetails(tenantId);
            return Response.ok(details).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // UC-28: View Reviews History
    @GET
    @Path("/feedback/{landlordId}")
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

    // UC-30: Accept Applicant
    @PUT
    @Path("/accept/{applicationId}")
    public Response acceptApplication(
            @PathParam("applicationId") String applicationId,
            Map<String, Object> payload) {
        String landlordId = (String) payload.get("landlordId");
        String feedback = (String) payload.get("feedback");

        if (landlordId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "landlordId is required"))
                    .build();
        }

        try {
            reviewApplicantService.acceptApplication(applicationId, landlordId, feedback);
            return Response.ok(Map.of("message", "Application accepted successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // UC-31: Reject Applicant
    @PUT
    @Path("/reject/{applicationId}")
    public Response rejectApplication(
            @PathParam("applicationId") String applicationId,
            Map<String, Object> payload) {
        String landlordId = (String) payload.get("landlordId");
        String feedback = (String) payload.get("feedback");

        if (landlordId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "landlordId is required"))
                    .build();
        }

        try {
            reviewApplicantService.rejectApplication(applicationId, landlordId, feedback);
            return Response.ok(Map.of("message", "Application rejected successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    // UC-29: Contact Applicant
    @POST
    @Path("/contact/{applicationId}")
    public Response contactApplicant(
            @PathParam("applicationId") String applicationId,
            Map<String, Object> payload) {
        String message = (String) payload.get("message");

        try {
            reviewApplicantService.contactApplicant(applicationId, message);
            return Response.ok(Map.of("message", "Email sent successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
}
