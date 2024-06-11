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
                                @QueryParam("status") List<ProjectStatus> status,
                                @QueryParam("lab") List<String> lab,
                                @QueryParam("creator_email") String creatorEmail,
                                @QueryParam("skill") List<String> skill,
                                @QueryParam("interest") List<String> interest,
                                @QueryParam("participant_email") String participantEmail,
                                @QueryParam("role") ProjectUserType role,
                                @HeaderParam("token") String auth) {

        System.out.println(Color.BLUE+status+Color.BLUE);
        System.out.println(Color.BLUE+skill+Color.BLUE);
        System.out.println(Color.BLUE+interest+Color.BLUE);

                List<?> dto = projectBean.getProjectsByDto(dtoType, name, status, lab, creatorEmail, skill, interest, participantEmail, role, auth);

                return Response.ok().entity(JsonUtils.convertObjectToJson(dto)).build();
        }

        @GET
        @Path("/filter-options")
        @Produces("application/json")
        public Response getFilterOptions(@HeaderParam("token") String token) {
            return Response.ok().entity(JsonUtils.convertObjectToJson(projectBean.filterOptions(token))).build();
        }
    }


