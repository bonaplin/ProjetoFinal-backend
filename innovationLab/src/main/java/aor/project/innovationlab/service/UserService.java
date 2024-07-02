package aor.project.innovationlab.service;

import aor.project.innovationlab.bean.SessionBean;
import aor.project.innovationlab.bean.UserBean;
//import aor.project.innovationlab.utils.jwt.JwtBean;
import aor.project.innovationlab.dto.response.PaginatedResponse;
import aor.project.innovationlab.dto.session.SessionLoginDto;
import aor.project.innovationlab.dto.user.*;
import aor.project.innovationlab.dto.user.password.UserChangePasswordDto;
import aor.project.innovationlab.dto.user.password.UserRecoverPasswordDto;
import aor.project.innovationlab.dto.user.password.UserVerifyTokenDto;
import aor.project.innovationlab.utils.JsonUtils;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/users")
public class UserService {

    @Inject
    private SessionBean sessionBean;

    @Inject
    private UserBean userBean;

//    @Inject
//    private JwtBean jwtService;

    public UserService() {
    }

    @GET
    @Path("/test{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers(@HeaderParam("token") String auth , @PathParam("id") String id) {
        String token = sessionBean.getTokenFromAuthorizationHeader(auth);
        return Response.ok("BATEU com "+token).build();
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
    public Response logout(@HeaderParam("token") String auth) {
        sessionBean.logout((auth));
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
    public Response updateUser(@HeaderParam("token") String auth, UserOwnerProfileDto dto) {
//        String token = sessionBean.getTokenFromAuthorizationHeader(auth);
        userBean.updateUser(auth, dto);
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
    public Response changePassword(@HeaderParam("token") String token, UserRecoverPasswordDto dto) {
        userBean.changePassword(token, dto);
        return Response.status(200).entity("Password changed successfully.").build();
    }

    @PUT
    @Path("/update-password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePassword(@HeaderParam("token") String token, UserChangePasswordDto dto) {
        userBean.updatePassword(token, dto);
        return Response.status(200).entity("Password updated successfully.").build();
    }

    @PUT
    @Path("/change-visiblity")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changeVisiblity(@HeaderParam("token") String token, UserChangeVisibilityDto dto) {
        userBean.changeVisiblity(token, dto);
        return Response.status(200).entity("Visiblity changed successfully.").build();
    }

    @POST
    @Path("/confirm-account")
    @Produces(MediaType.APPLICATION_JSON)
    public Response confirmAccount(@HeaderParam("token") String token, UserConfirmAccountDto dto) {
        userBean.confirmAccount(token, dto);
        return Response.status(200).entity("Account confirmed successfully!").build();
    }

    //TODO - alterar o userType para string no endpoint e deixar como os products.
    @GET
    @Path("/")
    @Produces("application/json")
    public Response searchUsers(@QueryParam("dtoType") String dtoType,
                                @QueryParam("username") String username,
                                @QueryParam("email") String email,
                                @QueryParam("firstname") String firstname,
                                @QueryParam("lastname") String lastname,
                                @QueryParam("role") String role,
                                @QueryParam("active") Boolean active,
                                @QueryParam("confirmed") Boolean confirmed,
                                @QueryParam("privateProfile") Boolean privateProfile,
                                @QueryParam("lab") List<String> lab,
                                @QueryParam("skill") List<String> skill,
                                @QueryParam("interest") List<String> interest,
                                @QueryParam("page_number") Integer pageNumber,
                                @QueryParam("page_size") Integer pageSize,
                                @QueryParam("order_field") String orderField,
                                @QueryParam("order_direction") String orderDirection,
                                @QueryParam("id") Long id,
                                @HeaderParam("token") String token) {
        System.out.println(orderDirection + " & " + orderField);

        PaginatedResponse<Object> dto = userBean.getUsers(id,token,dtoType, username, email, firstname, lastname, role, active, confirmed, privateProfile, lab, skill, interest, pageNumber, pageSize, orderField,orderDirection);
        return Response.status(200).entity(JsonUtils.convertObjectToJson(dto)).build();
    }

    @POST
    @Path("/verify-token")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response verifyToken(@HeaderParam("token") String token) {
        try{
            sessionBean.validateUserToken(token);
        }
        catch (Exception e){
            return Response.status(401).entity("Invalid token").build();
        }
        return Response.status(200).build();
    }
    @GET
    @Path("/{projectId}")
    @Produces("application/json")
    public Response getUsersByProject(@PathParam("projectId") Long projectId, @HeaderParam("token") String token) {
        List<UserAddToProjectDto> dto = userBean.getUsersForInfo(token, projectId);
        return Response.status(200).entity(JsonUtils.convertObjectToJson(dto)).build();
    }





}
