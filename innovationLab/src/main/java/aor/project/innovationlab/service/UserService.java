package aor.project.innovationlab.service;

import aor.project.innovationlab.bean.SessionBean;
import aor.project.innovationlab.bean.UserBean;
import aor.project.innovationlab.dto.jwt.JwtBean;
import aor.project.innovationlab.dto.session.SessionLoginDto;
import aor.project.innovationlab.dto.user.*;
import aor.project.innovationlab.utils.JsonUtils;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
public class UserService {

    @Inject
    private SessionBean sessionBean;

    @Inject
    private UserBean userBean;

    @Inject
    private JwtBean jwtService;

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(UserLogInDto userLogInDto) {
        SessionLoginDto sessionLoginDto = userBean.loginWithValidation(userLogInDto);
        return Response.status(200).entity(JsonUtils.convertObjectToJson(sessionLoginDto)).build();
    }

    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logout(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        sessionBean.logout(sessionBean.getTokenFromAuthorizationHeader(authorizationHeader));
        return Response.status(200).entity("See you soon!").build();
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(UserLogInDto userLogInDto) {
        userBean.createNewUser(userLogInDto);
        return Response.status(201).entity("Email sending").build();
    }

    @PUT
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@HeaderParam("token") String token, UserOwnerProfileDto dto) {
        System.out.println("Token: " + token);
        System.out.println("DTO: " + dto.toString());
        userBean.updateUser(token, dto);
        return Response.status(200).entity("User updated successfully.").build();
    }

    @POST
    @Path("/reset-password/{email}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPassword(@PathParam("email") String email) {
        userBean.sendPasswordResetEmail(email);
        return Response.status(200).entity("Email sent. Check your inbox.").build();
    }

    @POST
    @Path("/change-password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response changePassword(@HeaderParam("token") String token, UserChangePasswordDto dto) {
        userBean.changePassword(token, dto);
        return Response.status(200).entity("Password changed successfully.").build();

    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@HeaderParam("token") String token, @QueryParam("email") String email) {
        UserOwnerProfileDto dto = userBean.getUserProfile(token, email);
        return Response.status(200).entity(JsonUtils.convertObjectToJson(dto)).build();

    }

    @POST
    @Path("/confirm-account")
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmAccount(@HeaderParam("token") String token, UserConfirmAccountDto dto) {
        userBean.confirmAccount(token, dto);
        return Response.status(200).entity("Account confirmed successfully!").build();
    }



}
