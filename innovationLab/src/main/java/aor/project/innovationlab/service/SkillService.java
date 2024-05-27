package aor.project.innovationlab.service;

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

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSkill(@HeaderParam("token") String token, SkillDto skillDto) {
        try {
            SkillDto skill = skillBean.addSkill(token,skillDto);
            return Response.status(200).entity(skill).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSkill(@HeaderParam("token") String token, SkillDto skillDto) {
        System.out.println(Color.GREEN + skillDto + Color.GREEN);
        try {
            skillBean.deleteSkill(token,skillDto);
            return Response.status(200).entity("Skill deleted successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSkills(@HeaderParam("token") String token, @QueryParam("userEmail") String userEmail) {
        try {
            if(userEmail != null) return Response.status(200).entity(skillBean.getUserSkills(token, userEmail)).build();
            return Response.status(200).entity(skillBean.getAllSkills(token)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/types")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSkillType(@HeaderParam("token") String token) {
        try {
            System.out.println(Color.GREEN + "Get all skill types" + Color.GREEN);
            return Response.status(200).entity(skillBean.getAllSkillType(token)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
