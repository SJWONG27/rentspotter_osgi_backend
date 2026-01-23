package landlordProperty.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import landlordProperty.model.Property;
import landlordProperty.service.PropertyService;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Component(service = PropertyResource.class, property = {
        "osgi.jaxrs.resource=true"
})
@Path("/landlord/properties")
public class PropertyResource {

    @Reference
    private PropertyService propertyService;

    private static final String UPLOAD_DIR = System.getProperty("user.home") + "/rentspotter_uploads/";

    // UC-8 Upload new property
    // Test: POST http://localhost:8181/cxf/landlord/properties
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadProperty(List<Attachment> attachments) {
        try {
            Attachment jsonPart = null;
            for (Attachment att : attachments) {
                if ("data".equals(att.getContentDisposition().getParameter("name"))) {
                    jsonPart = att;
                    break;
                }
            }

            if (jsonPart == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Missing 'data' field containing JSON\"}").build();
            }

            String jsonString = jsonPart.getObject(String.class);
            ObjectMapper mapper = new ObjectMapper();
            Property property = mapper.readValue(jsonString, Property.class);

            List<Attachment> photoParts = new ArrayList<>();
            for (Attachment att : attachments) {
                if ("photos".equals(att.getContentDisposition().getParameter("name"))) {
                    photoParts.add(att);
                }
            }

            if (photoParts.size() < 2) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Validation Failed: At least 2 photos required.\"}").build();
            }

            List<String> savedNames = new ArrayList<>();
            for (Attachment att : photoParts) {
                savedNames.add(saveFile(att));
            }

            property.setCoverPhoto(savedNames.get(0));
            property.setPhotos(savedNames);

            propertyService.addProperty(property);

            return Response.status(Response.Status.CREATED).entity(property).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }

    // For tenant module
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProperties() {
        try {
            List<Property> properties = propertyService.getAllProperties();
            return Response.ok(properties).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("{\"error\": \"Error fetching properties\"}").build();
        }
    }

    // UC-6 View uploaded property list
    // Test: GET http://localhost:8181/cxf/landlord/properties?landlordId=663c6eb59b18e2e3eab6ab85
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProperties(@QueryParam("landlordId") String landlordId) {
        try {
            List<Property> properties;

            if (landlordId != null && !landlordId.isEmpty()) {
                properties = propertyService.getPropertiesByLandlord(landlordId);
            } else {
                properties = propertyService.getAllProperties();
            }

            return Response.ok(properties).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("{\"error\": \"Error fetching properties\"}").build();
        }
    }
    // UC-9 View and edit existing property details
    // Test: GET http://localhost:8181/cxf/landlord/properties/696e1b4530eddf29d030d6ec
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPropertyDetail(@PathParam("id") String id) {
        try {
            Optional<Property> property = propertyService.getPropertyById(id);

            if (property.isPresent()) {
                return Response.ok(property.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Property not found\"}").build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("{\"error\": \"Error fetching property details\"}").build();
        }
    }

    //Test: PUT http://localhost:8181/cxf/landlord/properties/696e1b4530eddf29d030d6ec
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProperty(@PathParam("id") String id, Property property) {
        try {
            boolean success = propertyService.updateProperty(id, property);

            if (success) {
                return Response.ok(propertyService.getPropertyById(id).get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Property not found or no changes made\"}").build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("{\"error\": \"Error updating property\"}").build();
        }
    }

    // UC-10 Manage property photos
    // TEst: PUT http://localhost:8181/cxf/landlord/properties/696e1b4530eddf29d030d6ec/cover-photo
    @PUT
    @Path("/{id}/cover-photo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setCoverPhoto(@PathParam("id") String id, Map<String, String> body) {
        try {
            String photoName = body.get("photo");

            if (photoName == null || photoName.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Missing 'photo' parameter in body\"}").build();
            }

            boolean success = propertyService.setCoverPhoto(id, photoName);

            if (success) {
                return Response.ok(propertyService.getPropertyById(id).get()).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Failed to set cover photo. Either the property ID is wrong, or the photo does not exist in this property's album.\"}").build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("{\"error\": \"Error setting cover photo\"}").build();
        }
    }

    //Test: DELETE http://localhost:8181/cxf/landlord/properties/696e1b4530eddf29d030d6ec/photo
    @DELETE
    @Path("/{id}/photo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePhoto(@PathParam("id") String id, Map<String, String> body) {
        try {
            String photoName = body.get("photo");

            if (photoName == null || photoName.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Missing 'photo' parameter in body\"}").build();
            }

            boolean success = propertyService.deletePropertyPhoto(id, photoName);

            if (success) {
                return Response.ok(propertyService.getPropertyById(id).get()).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Failed to delete photo. Photo not found or invalid ID.\"}").build();
            }

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("{\"error\": \"Error deleting photo\"}").build();
        }
    }

    //Test: POST http://localhost:8181/cxf/landlord/properties/696e1b4530eddf29d030d6ec/photos
    @POST
    @Path("/{id}/photos")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addPhotos(@PathParam("id") String id, List<Attachment> attachments) {
        try {
            if (!propertyService.getPropertyById(id).isPresent()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Property not found\"}").build();
            }

            List<String> newSavedNames = new ArrayList<>();

            for (Attachment att : attachments) {
                if (att.getContentDisposition().getParameter("filename") != null) {
                    newSavedNames.add(saveFile(att)); // Reuse your existing saveFile method
                }
            }

            if (newSavedNames.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"No valid photos found in request\"}").build();
            }

            boolean success = propertyService.addPropertyPhotos(id, newSavedNames);

            if (success) {
                return Response.ok(propertyService.getPropertyById(id).get()).build();
            } else {
                return Response.serverError().entity("{\"error\": \"Failed to update database\"}").build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("{\"error\": \"Error uploading photos\"}").build();
        }
    }

    // UC-11 Delete existing property
    // Test: DELETE http://localhost:8181/cxf/landlord/properties/696e1be530eddf29d030d6ed
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteProperty(@PathParam("id") String id) {
        try {
            boolean success = propertyService.deleteProperty(id);

            if (success) {
                return Response.ok("{\"message\": \"Property and associated photos deleted successfully\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Property not found\"}").build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("{\"error\": \"Error deleting property\"}").build();
        }
    }

    // UC-7 Filter properties
    // Test: GET http://localhost:8181/cxf/landlord/properties/filter
    @GET
    @Path("/filter")
    @Produces(MediaType.APPLICATION_JSON)
    public Response filterProperties(
            @QueryParam("maxPrice") Double maxPrice,
            @QueryParam("type") String type,
            @QueryParam("furnishing") String furnishing,
            @QueryParam("search") String search
    ) {
        try {
            List<Property> filteredList = propertyService.filterProperties(maxPrice, type, furnishing, search);

            return Response.ok(filteredList).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("{\"error\": \"Error filtering properties\"}").build();
        }
    }
    private String saveFile(Attachment attachment) throws IOException {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        String originalFilename = attachment.getDataHandler().getName();
        String extension = (originalFilename != null && originalFilename.contains("."))
                ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                : ".jpg";

        String uniqueFileName = UUID.randomUUID().toString() + extension;
        File file = new File(uploadDir, uniqueFileName);

        try (InputStream input = attachment.getDataHandler().getInputStream()) {
            Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return uniqueFileName;
    }
}