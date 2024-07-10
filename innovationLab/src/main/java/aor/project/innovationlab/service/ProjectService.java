package aor.project.innovationlab.service;

import aor.project.innovationlab.bean.*;
import aor.project.innovationlab.dto.log.LogDto;
import aor.project.innovationlab.dto.project.notes.NoteIdNoteDto;
import aor.project.innovationlab.dto.response.IdNameDto;
import aor.project.innovationlab.dto.response.PaginatedResponse;
import aor.project.innovationlab.dto.project.CreateProjectDto;
import aor.project.innovationlab.dto.project.ProjectInviteDto;
import aor.project.innovationlab.dto.response.ResponseYesNoInviteDto;
import aor.project.innovationlab.dto.user.project.UserChangeRoleDto;
import aor.project.innovationlab.dto.user.project.UserInviteDto;
import aor.project.innovationlab.dto.user.project.UserToChangeRoleKickDto;
import aor.project.innovationlab.enums.ProjectStatus;
import aor.project.innovationlab.enums.UserType;
import aor.project.innovationlab.utils.Color;
import aor.project.innovationlab.utils.JsonUtils;
import aor.project.innovationlab.utils.logs.LoggerUtil;
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

    @Inject
    private MessageBean messageBean;

    @Inject
    private LogBean logBean;

    @Inject
    private TaskBean taskBean;

    /**
     * Get projects by dto, this method is used to get projects by a specific dto
     *
     * @param dtoType
     * @param name
     * @param status
     * @param lab
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
            @QueryParam("status") List<ProjectStatus> status,
            @QueryParam("lab") List<String> lab,
            @QueryParam("creator_email") String creatorEmail,
            @QueryParam("skill") List<String> skill,
            @QueryParam("interest") List<String> interest,
            @QueryParam("participant_email") String participantEmail,
            @QueryParam("role") UserType role,
            @QueryParam("id") Long id,
            @QueryParam("page_number") Integer pageNumber,
            @QueryParam("page_size") Integer pageSize,
            @QueryParam("order_field") String orderField,
            @QueryParam("order_direction") String orderDirection,
            @HeaderParam("token") String auth) {

        PaginatedResponse<Object> dto = projectBean.getProjectsByDto(dtoType, name, status, lab, creatorEmail, skill, interest, participantEmail, role, orderField, orderDirection, auth, pageNumber, pageSize, id);

        return Response.ok().entity(JsonUtils.convertObjectToJson(dto)).build();
    }

    @GET
    @Path("/filter-options")
    @Produces("application/json")
    public Response getFilterOptions(@HeaderParam("token") String token) {
        return Response.ok().entity(JsonUtils.convertObjectToJson(projectBean.filterOptions(token))).build();
    }

    @GET
    @Path("/landing-page")
    @Produces("application/json")
    public Response getProjectsForLandingPage() {
        return Response.ok().entity(JsonUtils.convertObjectToJson(projectBean.getProjectsByDto("ProjectsForLandingPage", null, null, null, null, null, null, null, null, null, null, null, 1, 20, null))).build();
    }

    @POST
    @Path("/invite")
    @Consumes("application/json")
    public Response inviteToProject(@HeaderParam("token") String token, ProjectInviteDto projectInviteDto) {
        projectBean.inviteToProject(token, projectInviteDto);
        return Response.status(200).entity("Invitation sent successfully!").build();
    }

    @POST
    @Path("/invite/{tokenAuthorization}/{accept}")
    public Response acceptInvite(@PathParam("tokenAuthorization") String tokenAuthorization, @PathParam("accept") boolean accept, @HeaderParam("token") String token) {
        projectBean.respondToInvite(tokenAuthorization, token, accept);
        return Response.status(200).build();
    }


    @POST
    @Path("/")
    @Consumes("application/json")
    public Response createProject(@HeaderParam("token") String token, CreateProjectDto CreateProjectDto) {


        projectBean.createProject(token, CreateProjectDto);
        return Response.status(200).entity("Project created successfully!").build();
    }

    @GET
    @Path("/invite-projects")
    @Produces("application/json")
    @Consumes("application/json")
    public Response getProjectsForInvitation(@HeaderParam("token") String token, @QueryParam("email") String email) {
        List<IdNameDto> projects = projectBean.getProjectsForInvitation(token, email);
        return Response.ok().entity(JsonUtils.convertObjectToJson(projects)).build();
    }

    @GET
    @Path("/{id}/messages")
    @Produces("application/json")
    public Response getProjectMessages(@HeaderParam("token") String token, @PathParam("id") Long id) {
        return Response.ok().entity(JsonUtils.convertObjectToJson(projectBean.getProjectMessages(token, id))).build();
    }

    @GET
    @Path("/{id}/msg")
    @Produces("application/json")
    public Response getProjectMsg(@HeaderParam("token") String token, @PathParam("id") Long id, @QueryParam("page_number") Integer pageNumber, @QueryParam("page_size") Integer pageSize) {
        PaginatedResponse<Object> dto = messageBean.getProjectMessages(token, id, pageNumber, pageSize);
        return Response.ok().entity(JsonUtils.convertObjectToJson(dto)).build();
    }

    @GET
    @Path("/{id}/logs")
    @Produces("application/json")
    public Response getProjectLogs(@HeaderParam("token") String token, @PathParam("id") Long id, @QueryParam("page_number") Integer pageNumber, @QueryParam("page_size") Integer pageSize) {
        PaginatedResponse<Object> dto = logBean.getProjectLogs(token, id, pageNumber, pageSize);
        return Response.ok().entity(JsonUtils.convertObjectToJson(dto)).build();

    }

    @GET
    @Path("/{id}/tasks")
    @Produces("application/json")
    public Response getProjectTasks(@HeaderParam("token") String token, @PathParam("id") Long id, @QueryParam("dtoType") String dtoType) {
        List<Object> tasks = taskBean.getProjectTasks(token, id, dtoType);
        System.out.println(Color.CYAN+tasks.size()+Color.CYAN);
        return Response.ok().entity(JsonUtils.convertObjectToJson(tasks)).build();
    }

    @POST
    @Path("/{id}/notes")
    @Consumes("application/json")
    @Produces("application/json")
    public Response addProjectNotes(@HeaderParam("token") String token, @PathParam("id") Long id, NoteIdNoteDto notes) {
        LogDto dto = logBean.addProjectNotes(token, id, notes);
        return Response.status(200).entity(JsonUtils.convertObjectToJson(dto)).build();
    }

    @GET
    @Path("{id}/users")
    @Produces("application/json")
    public Response getUsersByProject(@PathParam("id") Long projectId, @HeaderParam("token") String token) {
        List<UserToChangeRoleKickDto> dto = projectBean.getUsersByProject(token, projectId);
        return Response.status(200).entity(JsonUtils.convertObjectToJson(dto)).build();
    }

    @PUT
    @Path("users/{userId}/role")
    @Produces("application/json")
    public Response changeUserRole(@PathParam("userId") Long userId, @HeaderParam("token") String token, UserChangeRoleDto userChangeRoleDto) {
        UserToChangeRoleKickDto dto = projectBean.changeUserRole(token, userId, userChangeRoleDto);
        return Response.status(200).entity(JsonUtils.convertObjectToJson(dto)).build();
    }

    @PUT
    @Path("users/{userId}/{projectId}/kick")
    @Produces("application/json")
    public Response kickUser(@PathParam("userId") Long userId, @HeaderParam("token") String token, @PathParam("projectId") Long projectId) {
        projectBean.kickUser(token, userId, projectId);
        return Response.status(200).entity("User kicked successfully!").build();
    }

    @GET
    @Path("/{id}/invites")
    @Produces("application/json")
    @Consumes("application/json")
    public Response getInvites(@HeaderParam("token") String token, @PathParam("id") Long projectId) {
        List<UserInviteDto> dto = projectBean.getInvites(token, projectId);
        return Response.status(200).entity(JsonUtils.convertObjectToJson(dto)).build();
    }

    @POST
    @Path("/{id}/proposed")
    @Produces("application/json")
    public Response proposeProject(@HeaderParam("token") String token, @PathParam("id") Long projectId) {
        projectBean.proposeProject(token, projectId);
        return Response.status(200).entity("Project proposed successfully!").build();
    }

    @PUT
    @Path("/{projectId}/invite-response")
    @Consumes("application/json")
    @Produces("application/json")
    public Response inviteResponse(@HeaderParam("token") String token, @PathParam("projectId") Long projectId, ResponseYesNoInviteDto dto) {
            projectBean.inviteResponse(token, projectId, dto);
            return Response.status(200).build();
    }

    @PUT
    @Path("/{projectId}/leave")
    @Produces("application/json")
    public Response leaveProject(@HeaderParam("token") String token, @PathParam("projectId") Long projectId) {
        projectBean.leaveProject(token, projectId);
        return Response.status(200).entity("You have left the project successfully!").build();
    }
}


