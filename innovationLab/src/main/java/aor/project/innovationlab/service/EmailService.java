package aor.project.innovationlab.service;

import aor.project.innovationlab.bean.EmailBean;
import aor.project.innovationlab.dto.PaginatedResponse;
import aor.project.innovationlab.email.EmailDto;
import aor.project.innovationlab.email.EmailSender;
import aor.project.innovationlab.utils.Color;
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
            @HeaderParam("token") String token
    ) {
        PaginatedResponse<Object> dto = emailBean.getEmails(dtoType, from, to,groupId, id,isRead, pageNumber, pageSize, orderField, orderDirection, token);
        return Response.status(200).entity(JsonUtils.convertObjectToJson(dto)).build();
    }
}
