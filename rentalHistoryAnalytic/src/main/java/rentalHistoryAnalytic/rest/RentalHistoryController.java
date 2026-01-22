package rentalHistoryAnalytic.rest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import rentalHistoryAnalytic.model.Rating;
import rentalHistoryAnalytic.model.RentalRecord;
import rentalHistoryAnalytic.service.DocumentService;
import rentalHistoryAnalytic.service.HistoryService;
import rentalHistoryAnalytic.service.RatingService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = RentalHistoryController.class, property = { "osgi.jaxrs.resource=true" })
@Path("/api/history")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RentalHistoryController {
    @Reference
    private HistoryService historyService;
    @Reference
    private RatingService ratingService;
    @Reference
    private DocumentService documentService;
    // UC-19: View Rental History
    @GET
    @Path("/tenant/{tenantId}")
    public Response getRentalHistory(@PathParam("tenantId") String tenantId) {
        List<RentalRecord> history = historyService.getTenantHistory(tenantId);
        return Response.ok(history).build();
    }
    // UC-20: View Tenant Trust Score
    @GET
    @Path("/tenant/score/{tenantId}")
    public Response getTenantScore(@PathParam("tenantId") String tenantId) {
        Double tenantScore = ratingService.getTrustScore(tenantId);
        return Response.ok(tenantScore).build();
    }
    // UC-21: View Portfolio Analytic
    @GET
    @Path("/landlord/{landlordId}")
    public Response getLandlordDashboard(@PathParam("landlordId") String landlordId) {
        List<RentalRecord> portfolio = historyService.getLandlordPortfolio(landlordId);
        return Response.ok(portfolio).build();
    }
    // UC-22: View Landlord Trust Score
    @GET
    @Path("/landlord/score/{landlordId}")
    public Response getLandlordScore(@PathParam("landlordId") String landlordId) {
        Double landlordScore = ratingService.getTrustScore(landlordId);
        return Response.ok(landlordScore).build();
    }
    // UC-23 & UC-24: Rate Landlord / Rate Tenant
    @POST
    @Path("/rate")
    public Response submitRating(Map<String, Object> payload) {
        try{
            Rating rating = new Rating();
            rating.setRaterId(payload.get("raterId").toString());
            rating.setRatedUserId(payload.get("ratedUserId").toString());
            rating.setScore(Integer.parseInt(payload.get("score").toString()));
            rating.setComment(payload.get("comment").toString());

            Rating submittedRating = ratingService.submitRating(rating);
            return Response.ok(submittedRating).build();
        }
        catch (Exception e){
            Map<String, Object> errorMessage = new HashMap<>();
            errorMessage.put("timestamp", LocalDateTime.now());
            errorMessage.put("message", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
        }
    }
    // UC-25: Generate Reference Letter
    @GET
    @Path("/document/{recordId}")
    public Response downloadLetter(@PathParam("recordId") String recordId) {
        String referenceLetter = documentService.generateReferenceLetter(recordId);
        return Response.ok(referenceLetter).build();
    }
}
