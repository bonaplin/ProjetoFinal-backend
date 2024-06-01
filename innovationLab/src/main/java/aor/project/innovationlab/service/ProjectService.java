package aor.project.innovationlab.service;

import aor.project.innovationlab.bean.LabBean;
import aor.project.innovationlab.bean.ProjectBean;
import aor.project.innovationlab.bean.SessionBean;
import aor.project.innovationlab.dto.project.ProjectDto;
import aor.project.innovationlab.utils.JsonUtils;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

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
        List<ProjectDto> dto = projectBean.getProjectsByUser(token, email);
        return Response.ok().entity(JsonUtils.convertObjectToJson(dto)).build();
    }



}
