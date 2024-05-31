package aor.project.innovationlab.service;

import aor.project.innovationlab.bean.LabBean;
import aor.project.innovationlab.bean.ProjectBean;
import aor.project.innovationlab.bean.SessionBean;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

@Path("/projects")
public class ProjectService {

    @Inject
    private ProjectBean projectBean;

    @Inject
    private SessionBean sessionBean;

    @GET
    @Path("/")
    @Produces("application/json")
    public Response getProjectsByUser(@HeaderParam("token") String token, @QueryParam("email") String email) {
        return Response.ok(projectBean.getProjectsByUser(token, email)).build();
    }
}
