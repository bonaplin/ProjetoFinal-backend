package aor.project.innovationlab.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        if (e instanceof IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } else if (e instanceof SecurityException) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        } else if (e instanceof NullPointerException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } else if (e instanceof IOException) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("IO exception occurred.").build();
//        } else if (e instanceof io.jsonwebtoken.security.SignatureException) {
//            return Response.status(Response.Status.UNAUTHORIZED).entity("JWT validity cannot be asserted and should not be trusted.").build();
        } else if (e instanceof RuntimeException) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal server error.").build();
        }
    }
}
