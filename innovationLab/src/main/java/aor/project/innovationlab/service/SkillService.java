package aor.project.innovationlab.service;

import aor.project.innovationlab.bean.SessionBean;
import aor.project.innovationlab.bean.SkillBean;
import aor.project.innovationlab.dto.skill.SkillDto;
import aor.project.innovationlab.utils.Color;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/skills")
public class SkillService {

    @Inject
    private SkillBean skillBean;
    @Inject
    private SessionBean sessionBean;

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSkill(@HeaderParam("Authorization") String auth, SkillDto skillDto) {
        String token = sessionBean.getTokenFromAuthorizationHeader(auth);
        SkillDto skill = skillBean.addSkill(token,skillDto);
        return Response.status(200).entity(skill).build();
    }

    @PUT
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSkill(@HeaderParam("Authorization") String auth, SkillDto skillDto) {
        String token = sessionBean.getTokenFromAuthorizationHeader(auth);
        skillBean.deleteSkill(token,skillDto);
        return Response.status(200).entity("Skill deleted successfully").build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSkills(@HeaderParam("Authorization") String auth, @QueryParam("userEmail") String userEmail) {
        String token = sessionBean.getTokenFromAuthorizationHeader(auth);
        if(userEmail != null) return Response.status(200).entity(skillBean.getUserSkills(token, userEmail)).build();
        return Response.status(200).entity(skillBean.getAllSkills(token)).build();
    }

    @GET
    @Path("/types")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSkillType(@HeaderParam("Authorization") String auth) {
            return Response.status(200).entity(skillBean.getAllSkillType(auth)).build();
    }
}
