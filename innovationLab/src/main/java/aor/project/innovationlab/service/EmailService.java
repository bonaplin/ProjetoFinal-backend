package aor.project.innovationlab.service;

import aor.project.innovationlab.bean.EmailBean;
import aor.project.innovationlab.dto.response.PagAndUnreadResponse;
import aor.project.innovationlab.dto.emails.EmailPageDto;
import aor.project.innovationlab.dto.emails.EmailResponseDto;
import aor.project.innovationlab.dto.emails.EmailSendDto;
import aor.project.innovationlab.email.EmailDto;
import aor.project.innovationlab.email.EmailSender;
import aor.project.innovationlab.utils.JsonUtils;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/emails")
public class EmailService {

    private final EmailSender emailSender;

    public EmailService() {
        this.emailSender = new EmailSender();
    }

    @EJB
    private EmailBean emailBean;

    @Path("/activate")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendActivationEmail(EmailDto prop) {
        emailSender.sendVerificationEmail(prop.getTo(), prop.getToken());
        return Response.status(200).entity("Activation email sent successfully. Check your inbox.").build();
    }

    @Path("/password")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response sendPasswordResetEmail(EmailDto prop) {
        emailSender.sendPasswordResetEmail(prop.getTo(), prop.getToken());
        return Response.status(200).entity("Password reset email sent successfully. Check your inbox.").build();
    }

    @Path("/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchEmails(
            @QueryParam("dtoType") String dtoType,
            @QueryParam("from") String from,
            @QueryParam("to") String to,
            @QueryParam("group_id") Long groupId,
            @QueryParam("id") Long id,
            @QueryParam("is_read") Boolean isRead,
            @QueryParam("page_number") Integer pageNumber,
            @QueryParam("page_size") Integer pageSize,
            @QueryParam("order_field") String orderField,
            @QueryParam("order_direction") String orderDirection,
            @QueryParam("search") String searchText,
            @HeaderParam("token") String token
    ) {
        PagAndUnreadResponse<Object> dto = emailBean.getEmails(dtoType, from, to,groupId, id,isRead, pageNumber, pageSize, orderField, orderDirection, searchText, token);
        return Response.status(200).entity(JsonUtils.convertObjectToJson(dto)).build();
    }

    @Path("/{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response markMailAsRead(@PathParam("id") Long id, @HeaderParam("token") String token) {
        EmailPageDto email = emailBean.markMailAsRead(id, token);
        return Response.status(200).entity(JsonUtils.convertObjectToJson(email)).build();
    }

    @Path("/delete/{id}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendEmail(@PathParam("id") Long id, @HeaderParam("token") String token) {
        EmailPageDto email = emailBean.deleteEmail(id, token);
        return Response.status(200).entity(JsonUtils.convertObjectToJson(email)).build();
    }

    @Path("/{id}/response")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendEmailResponse(@PathParam("id") Long id, @HeaderParam("token") String token, EmailResponseDto emailDto) {
        EmailResponseDto email = emailBean.sendEmailResponse(id, emailDto, token);
        return Response.status(200).entity(JsonUtils.convertObjectToJson(email)).build();
    }

    @Path("/send")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendEmail(@HeaderParam("token") String token, EmailSendDto emailDto) {
        emailBean.sendMailToUser(token, emailDto.getTo(), emailDto.getSubject(), emailDto.getBody());
        return Response.status(200).entity("Email sent successfully.").build();
    }
}
