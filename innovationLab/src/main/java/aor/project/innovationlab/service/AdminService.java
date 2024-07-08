package aor.project.innovationlab.service;

import aor.project.innovationlab.bean.ProjectBean;
import aor.project.innovationlab.dto.response.PaginatedResponse;
import aor.project.innovationlab.dto.response.ResponseYesNoInviteDto;
import aor.project.innovationlab.email.EmailSender;
import aor.project.innovationlab.utils.JsonUtils;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/admin")
public class AdminService {

    @Inject
    private ProjectBean projectBean;

    @GET
    @Path("/projects")
    @Produces("application/json")
    public Response getReadyProjects(@HeaderParam("token") String token, @QueryParam("page_number") Integer pageNumber,  @QueryParam("page_size") Integer pageSize) {
        PaginatedResponse<Object> readyProjects = projectBean.getReadyProjects(token, pageNumber,pageSize);
        return Response.ok().entity(JsonUtils.convertObjectToJson(readyProjects)).build();
    }

    @PUT
    @Path("/projects/{projectId}")
    @Produces("application/json")
    @Consumes("application/json")
    public Response approveProject(@HeaderParam("token") String token, @PathParam("projectId") Long projectId, ResponseYesNoInviteDto responseYesNoInviteDto) {
        projectBean.approveProject(token, projectId, responseYesNoInviteDto);
        return Response.ok().build();
    }

}
