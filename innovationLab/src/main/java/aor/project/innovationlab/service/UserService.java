package aor.project.innovationlab.service;

import aor.project.innovationlab.bean.SessionBean;
import aor.project.innovationlab.dto.session.SessionLoginDto;
import aor.project.innovationlab.dto.user.UserLogInDto;
import aor.project.innovationlab.utils.JsonUtils;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
public class UserService {

    @Inject
    SessionBean sessionBean;

    public UserService() {
    }

    @GET
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        return Response.ok("BATEU").build();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(UserLogInDto userLogInDto) {
        String token = sessionBean.login(userLogInDto);
        if(token != null){
            return Response.status(200).entity(JsonUtils.convertObjectToJson(
                    new SessionLoginDto(token))).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@HeaderParam("token") String token) {
        if(sessionBean.logout(token)){
            return Response.status(200).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }


}
