package leaseAgreement.web;

import authentication.web.UserController;
import leaseAgreement.api.model.LeaseAgreementModel;
import leaseAgreement.api.model.LeaseAgreementStatus;
import leaseAgreement.api.service.EmailService;
import leaseAgreement.api.service.LeaseAgreementService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component(service = LeaseAgreementController.class,
        property = {
                "osgi.jaxrs.resource=true",
                "osgi.jaxrs.application.select=(osgi.jaxrs.name=.default)"
        }
)
@Path("/lease")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LeaseAgreementController {

    @Reference
    private LeaseAgreementService leaseAgreementService;

    @Reference
    private EmailService emailService;

    public LeaseAgreementController() {
    }

    // Landlord
    // UC-12 Renew Existing Landlord LeaseAgreementModel Agreement
    @PUT
    @Path("/update-lease/{applicationId}")
    public Response updateLeaseAgreement(@PathParam("applicationId") String applicationId, LeaseAgreementModel leaseData) {
        Optional<LeaseAgreementModel> existingLease = leaseAgreementService.findByApplicationId(applicationId);
        Map<String, Object> response = new HashMap<>();

        if (existingLease.isEmpty()) {
            response.put("status", "error");
            response.put("message", "lease does not exist");
            return Response.ok(response).build();
        }

        LeaseAgreementModel lease = existingLease.get();
        updateLeaseFields(lease, leaseData);
        lease.setLeaseStatus(LeaseAgreementStatus.UNDER_REVIEW_BY_TENANT);
        leaseAgreementService.save(lease);

        response.put("status", "success");
        response.put("leaseId", lease.getId().toHexString());
        return Response.ok(response).build();
    }

    // UC-13 View Lease Agreement Status
    @GET
    @Path("/status/{leaseId}")
    public Response getLeaseStatus(@PathParam("leaseId") String leaseId) {
        return leaseAgreementService.findById(leaseId)
                .map(lease -> {
                    Map<String, Object> res = new HashMap<>();
                    res.put("status", "success");
                    res.put("leaseId", lease.getId().toHexString());
                    res.put("leaseStatus", lease.getLeaseStatus());
                    return Response.ok(res).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    // UC-14 Submit Landlord LeaseAgreementModel Agreement (Create or Update)
    @POST
    @Path("/submit-landlord/{applicationId}")
    public Response submitLandlordLease(@PathParam("applicationId") String applicationId, LeaseAgreementModel leaseData) {
        if (leaseAgreementService.findByApplicationId(applicationId).isPresent()) {
            Map<String, String> err = new HashMap<>();
            err.put("message", "lease existed");
            return Response.status(Response.Status.CONFLICT).entity(err).build();
        }

        leaseData.setApplicationId(applicationId);
        leaseData.setLeaseStatus(LeaseAgreementStatus.UNDER_REVIEW_BY_TENANT);
        LeaseAgreementModel saved = leaseAgreementService.save(leaseData);

        Map<String, String> res = new HashMap<>();
        res.put("status", "success");
        res.put("leaseAgreementId", saved.getId().toHexString());
        return Response.ok(res).build();
    }

    @PUT
    @Path("/save-pdf/{leaseId}")
    public Response savePdf(@PathParam("leaseId") String leaseId,  Map<String, String> body) {
        return leaseAgreementService.findById(leaseId).map(lease -> {
            lease.setPdf(body.get("pdfBase64"));
            leaseAgreementService.save(lease);
            return Response.ok("PDF saved successfully").build();
        }).orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    // Tenant
    // UC-15 View Lease Agreement
    @GET
    @Path("/tenant/{userId}")
    public Response getLeases(@PathParam("userId") String userId) {
        return Response.ok(leaseAgreementService.findByTenantId(userId)).build();
    }

    // UC-16 Get PDF
    @GET
    @Path("/get-pdf/{leaseId}")
    public Response getPdf(@PathParam("leaseId") String leaseId) {
        return leaseAgreementService.findById(leaseId)
                .map(lease -> {
                    Map<String, String> res = new HashMap<>();
                    res.put("url", "data:application/pdf;base64," + lease.getPdf());
                    return Response.ok(res).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    // UC-17 Submit Tenant LeaseAgreementModel Agreement (Finalize)
    @PUT
    @Path("/submit-tenant/{leaseId}")
    public Response submitTenantLease(@PathParam("leaseId") String leaseId, Map<String, String> body) {
        Optional<LeaseAgreementModel> opLease = leaseAgreementService.findById(leaseId);
        if (opLease.isEmpty()) return Response.status(404).build();

        LeaseAgreementModel lease = opLease.get();
        lease.setLesseeIc(body.get("lesseeIc"));
        lease.setLesseeDesignation(body.get("lesseeDesignation"));
        lease.setLesseeSignature(body.get("lesseeSignature"));
        lease.setLeaseStatus(LeaseAgreementStatus.EFFECTIVE);
        leaseAgreementService.save(lease);

        Map<String, String> res = new HashMap<>();
        res.put("status", "success");
        return Response.ok(res).build();
    }

    private void updateLeaseFields(LeaseAgreementModel target, LeaseAgreementModel source) {
        target.setDay(source.getDay());
        target.setMonth(source.getMonth());
        target.setYear(source.getYear());
        target.setRentRmNum(source.getRentRmNum());
    }

    // UC-18 Notify landlord after tenant signed
    @POST
    @Path("/sendEmail/{recipient_email}")
    public String notifyLandlord(@PathParam("recipient_email") String recipientEmail){
        String subject = "RentSpotter Notification";

        String body = "Hi there,\n\n"
                + "Lease agreement with (" + recipientEmail + ") was signed by tenant. "
                + "Please proceed with the next step. \n\n"
                + "Best regards,\n"
                + "The AcadProBot Team";

        emailService.sendEmail(recipientEmail, subject, body);
        return "Reset Email Sent";
    }
}