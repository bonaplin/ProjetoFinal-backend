package aor.project.innovationlab.service;

import aor.project.innovationlab.bean.ProjectBean;
import aor.project.innovationlab.bean.SessionBean;
import aor.project.innovationlab.dto.IdNameDto;
import aor.project.innovationlab.dto.PaginatedResponse;
import aor.project.innovationlab.dto.project.CreateProjectDto;
import aor.project.innovationlab.dto.project.ProjectInviteDto;
import aor.project.innovationlab.enums.ProjectStatus;
import aor.project.innovationlab.enums.UserType;
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

    /**
     * Get projects by dto, this method is used to get projects by a specific dto
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
        System.out.println(" orderDirection: " + orderDirection + " orderField " + orderField);

        PaginatedResponse<Object> dto = projectBean.getProjectsByDto(dtoType, name, status, lab, creatorEmail, skill, interest, participantEmail, role, orderField, orderDirection, auth, pageNumber, pageSize, id);



                return Response.ok().entity(JsonUtils.convertObjectToJson(dto)).build();
        }

        @GET
        @Path("/filter-options")
        @Produces("application/json")
        public Response getFilterOptions(@HeaderParam("token") String token) {
            return Response.ok().entity(JsonUtils.convertObjectToJson(projectBean.filterOptions(token))).build();
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
        public Response acceptInvite(@PathParam("tokenAuthorization") String tokenAuthorization, @PathParam("accept") boolean accept, @HeaderParam("token") String token){
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
    }


