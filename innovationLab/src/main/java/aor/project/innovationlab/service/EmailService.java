package aor.project.innovationlab.service;

import aor.project.innovationlab.email.EmailDto;
import aor.project.innovationlab.email.EmailSender;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/emails")
public class EmailService {

    private final EmailSender emailSender;

    public EmailService() {
        this.emailSender = new EmailSender();
    }

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
}
