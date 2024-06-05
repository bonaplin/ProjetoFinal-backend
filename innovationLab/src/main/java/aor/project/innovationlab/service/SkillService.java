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
    public Response addSkill(@HeaderParam("token") String auth, SkillDto skillDto) {
//        String token = sessionBean.getTokenFromAuthorizationHeader(auth);
        SkillDto skill = skillBean.addSkill(auth,skillDto);
        return Response.status(200).entity(skill).build();
    }

    @PUT
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSkill(@HeaderParam("token") String auth, SkillDto skillDto) {
//        String token = sessionBean.getTokenFromAuthorizationHeader(auth);
        skillBean.deleteSkill(auth,skillDto);
        return Response.status(200).entity("Skill deleted successfully").build();
    }

    //String name, String type, String userEmail, String projectName
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSkills(@HeaderParam("token")
                                     String auth,
                                 @QueryParam("userEmail") String userEmail,
                                 @QueryParam("skillType") String skillType,
                                 @QueryParam("projectName") String projectName,
                                 @QueryParam("name") String name){
        return Response.status(200).entity(skillBean.getSkills(name,skillType,userEmail,projectName)).build();
    }

    @GET
    @Path("/types")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSkillType(@HeaderParam("token") String auth) {
//            String token = sessionBean.getTokenFromAuthorizationHeader(auth);
            return Response.status(200).entity(skillBean.getAllSkillType(auth)).build();
    }




}
