package aor.project.innovationlab.service;

import aor.project.innovationlab.bean.SessionBean;
import aor.project.innovationlab.bean.UserBean;
import aor.project.innovationlab.dto.session.SessionLoginDto;
import aor.project.innovationlab.dto.user.UserChangePasswordDto;
import aor.project.innovationlab.dto.user.UserLogInDto;
import aor.project.innovationlab.exception.UserCreationException;
import aor.project.innovationlab.utils.JsonUtils;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
public class UserService {

    @Inject
    private SessionBean sessionBean;

    @Inject
    private UserBean userBean;

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
        SessionLoginDto sessionLoginDto = null;
        if(userLogInDto == null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if(userLogInDto.getEmail() == null || userLogInDto.getPassword() == null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        sessionLoginDto = sessionBean.login(userLogInDto);
        if(sessionLoginDto != null){
            return Response.status(200).entity(JsonUtils.convertObjectToJson(sessionLoginDto)).build();
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

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(UserLogInDto userLogInDto) {
        try {
            userBean.createNewUser(userLogInDto.getEmail(),userLogInDto.getPassword());
            return Response.status(201).build();
        } catch (UserCreationException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/reset-password/{email}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPassword(@PathParam("email") String email) {
        try {
            userBean.sendPasswordResetEmail(email);
        }
        catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
        return Response.status(200).build();
    }

    @POST
    @Path("/change-password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePassword(@HeaderParam("token") String token, UserChangePasswordDto dto) {
        try {
            userBean.changePassword(token, dto);
            return Response.status(200).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }



}
