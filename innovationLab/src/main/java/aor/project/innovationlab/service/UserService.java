package aor.project.innovationlab.service;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
public class UserService {

    public UserService() {
    }

    @GET
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        return Response.ok("BATEU").build();
    }
}
