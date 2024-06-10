package aor.project.innovationlab.utils.populator;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/populator")
public class PopulatorService {

    @Inject
    private PopulatorBean populatorBean;

    @POST
    @Path("/user")
    @Produces ("application/json")
    @Consumes("application/json")
    public Response populateUsers(PopulatorUserDto dto) {
        System.out.println("service called");
        populatorBean.addUser(dto);
        return Response.ok().build();
    }
}
