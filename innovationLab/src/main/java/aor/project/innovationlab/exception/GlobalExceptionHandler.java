package aor.project.innovationlab.exception;

import aor.project.innovationlab.utils.Color;
import aor.project.innovationlab.utils.logs.LoggerUtil;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

/**
 * Global exception handler for the application.
 */
@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        System.out.println(Color.CYAN + "GlobalExceptionHandler" + Color.RESET);

        // Extrai a causa raiz da exceção, se houver
        Throwable rootCause = getRootCause(e);
        String errorMessage = rootCause.getMessage();

        // Log a mensagem de erro
        LoggerUtil.logError("erro", errorMessage, null, null);

        if (rootCause instanceof IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        } else if (rootCause instanceof SecurityException) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(errorMessage)
                    .build();
        } else if (rootCause instanceof NullPointerException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        } else if (rootCause instanceof IOException) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("IO exception occurred.")
                    .build();
        } else if (rootCause instanceof RuntimeException) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorMessage)
                    .build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Internal server error.")
                    .build();
        }
    }

    private Throwable getRootCause(Throwable e) {
        Throwable cause = null;
        Throwable result = e;

        while ((cause = result.getCause()) != null && result != cause) {
            result = cause;
        }

        return result;
    }
}



