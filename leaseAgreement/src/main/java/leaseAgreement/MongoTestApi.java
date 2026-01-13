package leaseAgreement;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

//@Component(service = Object.class)
@Component(
        service = Object.class, // Or Object.class
        property = {
                "osgi.jaxrs.resource=true",
                "osgi.jaxrs.application.select=(osgi.jaxrs.name=.default)"
        }
)
@Path("/mongo-test")
public class MongoTestApi {

    @Reference
    private MongoClientProvider mongoProvider;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkConnection() {
        try {
            // Actively try to reach the DB
            var dbNames = mongoProvider.getClient().getDatabase("users").runCommand(new org.bson.Document("ping", 1));
            return Response.ok("{\"status\": \"Connected\", \"firstDb\": \"" + dbNames + "\"}").build();
        } catch (Exception e) {
            return Response.status(500)
                    .entity("{\"status\": \"Failed\", \"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}


