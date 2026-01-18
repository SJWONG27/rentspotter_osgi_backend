package tenantApplication.rest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import tenantApplication.model.Application;
import tenantApplication.service.TenantApplicationService;
import landlordProperty.model.Property;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Component(service = TenantResource.class, property = { "osgi.jaxrs.resource=true" })
@Path("/api/tenant")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TenantResource {

    @Reference
    private TenantApplicationService tenantApplicationService;

    // UC-1: View property listings
    @GET
    @Path("/properties")
    public Response getProperties(@QueryParam("maxPrice") Double maxPrice,
                                  @QueryParam("propertyType") String propertyType,
                                  @QueryParam("furnishedStatus") String furnishedStatus) {
        return Response.ok(tenantApplicationService.getAvailableProperties(maxPrice, propertyType, furnishedStatus)).build();
    }

    // UC-2: View details
    @GET
    @Path("/properties/{id}")
    public Response getPropertyDetails(@PathParam("id") String id) {
        Property p = tenantApplicationService.getPropertyDetails(id);
        if (p == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(p).build();
    }

    // UC-3: Submit application
    @POST
    @Path("/applications")
    public Response submitApplication(Map<String, Object> payload) {
        try {
            String tenantId = (String) payload.get("tenantId");
            String propertyId = (String) payload.get("propertyId");
            // Handle number conversion carefully
            Double monthlyIncome = Double.valueOf(payload.get("monthlyIncome").toString());
            String occupation = (String) payload.get("occupation");
            String message = (String) payload.get("message");

            Application app = tenantApplicationService.submitApplication(tenantId, propertyId, monthlyIncome, occupation, message);
            return Response.ok(app).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // UC-4 & UC-5: Track status / View history
    @GET
    @Path("/applications")
    public Response getApplications(@QueryParam("tenantId") String tenantId) {
        if (tenantId == null) return Response.status(Response.Status.BAD_REQUEST).entity("Missing tenantId").build();
        return Response.ok(tenantApplicationService.getTenantApplications(tenantId)).build();
    }

    // UC-5: Cancel
    @PATCH
    @Path("/applications/{id}/cancel")
    public Response cancelApplication(@PathParam("id") String id, Map<String, String> payload) {
        try {
            String tenantId = payload.get("tenantId");
            Application app = tenantApplicationService.cancelApplication(id, tenantId);
            return Response.ok(app).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    // UC-5: Delete
    @DELETE
    @Path("/applications/{id}")
    public Response deleteApplication(@PathParam("id") String id, @QueryParam("tenantId") String tenantId) {
        try {
            tenantApplicationService.deleteApplication(id, tenantId);
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
