package aor.project.innovationlab.service;

import aor.project.innovationlab.bean.ProjectBean;
import aor.project.innovationlab.bean.SessionBean;
import aor.project.innovationlab.dto.project.ProjectCardDto;
import aor.project.innovationlab.dto.project.ProjectDto;
import aor.project.innovationlab.enums.ProjectStatus;
import aor.project.innovationlab.enums.ProjectUserType;
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

    @GET
    @Path("/search")
    @Produces("application/json")
    public Response getProjects(@QueryParam("name") String name,
                                @QueryParam("status") ProjectStatus status,
                                @QueryParam("lab_id") Long labId,
                                @QueryParam("creator_email") String creatorEmail,
                                @QueryParam("skill") String skill,
                                @QueryParam("interest") String interest,
                                @QueryParam("participant_email") String participantEmail,
                                @QueryParam("role") ProjectUserType role,
                                @QueryParam("requesting_user_email") String requestingUserEmail) {
        List<ProjectCardDto> dto = projectBean.getProjects(name, status, labId, creatorEmail, skill, interest, participantEmail, role, requestingUserEmail);
        return Response.ok().entity(JsonUtils.convertObjectToJson(dto)).build();
    }

}
