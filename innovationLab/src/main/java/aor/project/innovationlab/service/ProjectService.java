package aor.project.innovationlab.service;

import aor.project.innovationlab.bean.ProjectBean;
import aor.project.innovationlab.bean.SessionBean;
import aor.project.innovationlab.dto.project.ProjectCardDto;
import aor.project.innovationlab.dto.project.ProjectDto;
import aor.project.innovationlab.enums.ProjectStatus;
import aor.project.innovationlab.enums.ProjectUserType;
import aor.project.innovationlab.utils.Color;
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

//    @GET
//    @Path("/")
//    @Produces("application/json")
//    public Response getProjectsByUser(@HeaderParam("token") String token, @QueryParam("email") String email) {
//        List<ProjectDto> dto = projectBean.getProjectsByUser(token, email);
//        if (dto == null) {
//            return Response.status(Response.Status.UNAUTHORIZED).entity("Unauthorized").build();
//        } else {
//            return Response.ok().entity(JsonUtils.convertObjectToJson(dto)).build();
//        }
//    }

//    @GET
//    @Path("/search")
//    @Produces("application/json")
//    public Response getProjects(@QueryParam("name") String name,
//                                @QueryParam("status") ProjectStatus status,
//                                @QueryParam("lab_id") Long labId,
//                                @QueryParam("creator_email") String creatorEmail,
//                                @QueryParam("skill") String skill,
//                                @QueryParam("interest") String interest,
//                                @QueryParam("participant_email") String participantEmail,
//                                @QueryParam("role") ProjectUserType role,
//                                @HeaderParam("token") String token) {
//        List<ProjectCardDto> dto = projectBean.getProjects(name, status, labId, creatorEmail, skill, interest, participantEmail, role, token);
//        return Response.ok().entity(JsonUtils.convertObjectToJson(dto)).build();
//    }

    /**
     * Get projects by dto, this method is used to get projects by a specific dto
     * @param dtoType
     * @param name
     * @param status
     * @param labId
     * @param creatorEmail
     * @param skill
     * @param interest
     * @param participantEmail
     * @param role
     * @param auth
     * @return
     */
    @GET
    @Path("/")
    @Produces("application/json")
    public Response getProjectsByDto(
                                @QueryParam("dtoType") String dtoType,
                                @QueryParam("name") String name,
                                @QueryParam("status") ProjectStatus status,
                                @QueryParam("lab_id") Long labId,
                                @QueryParam("creator_email") String creatorEmail,
                                @QueryParam("skill") String skill,
                                @QueryParam("interest") String interest,
                                @QueryParam("participant_email") String participantEmail,
                                @QueryParam("role") ProjectUserType role,
                                @HeaderParam("token") String auth) {

                List<?> dto = projectBean.getProjectsByDto(dtoType, name, status, labId, creatorEmail, skill, interest, participantEmail, role, auth);

                return Response.ok().entity(JsonUtils.convertObjectToJson(dto)).build();
        }
    }


