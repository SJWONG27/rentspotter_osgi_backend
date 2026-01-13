package authentication.web;

import authentication.api.dto.RegisterRequest;
import authentication.api.model.User;
import authentication.api.service.UserService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

// This tells OSGi this is a JAX-RS Resource
@Component(service = UserController.class,
        property = {
                "osgi.jaxrs.resource=true",
                "osgi.jaxrs.application.select=(osgi.jaxrs.name=.default)"
        }
)
@Path("/auth") // Your URL will be /auth/...
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {

    @Reference
    private UserService userService;

    // JAX-RS needs a no-args constructor or @Reference injection
    public UserController() {}


    @POST
    @Path("/register")
    public Map<String, Object> register(RegisterRequest req) {
        User user = userService.register(req);
        Map<String, Object> res = new HashMap<>();
        res.put("status", "success");
        res.put("user", user);
        return res;
    }

    @POST
    @Path("/login")
    public Map<String, Object> login(Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        // This calls your UserService which should return the User + Token
        return userService.login(email, password);
    }
}