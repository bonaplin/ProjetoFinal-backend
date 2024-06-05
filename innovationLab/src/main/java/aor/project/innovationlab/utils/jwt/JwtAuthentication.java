//package aor.project.innovationlab.utils.jwt;
//
//import jakarta.annotation.Priority;
//import jakarta.ws.rs.Priorities;
//import jakarta.ws.rs.container.ContainerRequestContext;
//import jakarta.ws.rs.container.ContainerRequestFilter;
//import jakarta.ws.rs.core.HttpHeaders;
//import jakarta.ws.rs.core.Response;
//import jakarta.ws.rs.ext.Provider;
//import jakarta.ejb.EJB;
//
//import java.util.HashSet;
//import java.util.Set;
//
///**
// * This class is responsible for authenticating JWT tokens.
// * It is a filter that intercepts requests and checks if the token is valid.
// */
//@Provider
//@Priority(Priorities.AUTHENTICATION)
//public class JwtAuthentication implements ContainerRequestFilter {
//
//    @EJB
//    private JwtBean jwtService;
//
//    private static final Set<String> EXCLUDED_PATHS = new HashSet<>();
//
//    /**
//     * Paths that do not require authentication.
//     */
//    static {
//        EXCLUDED_PATHS.add("/users/login");
//        EXCLUDED_PATHS.add("/users/confirm-account");
//        EXCLUDED_PATHS.add("/users/reset-password");
//        EXCLUDED_PATHS.add("/users/change-password");
//        EXCLUDED_PATHS.add("/labs");
//        EXCLUDED_PATHS.add("/projects");
//    }
//
//    /**
//     * Method to filter requests and check if the token is valid.
//     * @param requestContext request context.
//     */
//    @Override
//    public void filter(ContainerRequestContext requestContext) {
//        String path = requestContext.getUriInfo().getPath();
//
//        for (String excludedPath : EXCLUDED_PATHS) {
//            if (path.startsWith(excludedPath)) {
//                return;
//            }
//        }
//
//        if(EXCLUDED_PATHS.contains(path)){
//            return;
//        }
//
//        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
//
//        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
//            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
//            return;
//        }
//
//        String token = authorizationHeader.substring("Bearer".length()).trim();
//
//        try {
//            jwtService.validateUser(token, requestContext);
//        } catch (io.jsonwebtoken.security.SignatureException e) {
//            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.").build());
//        } catch (Exception e) {
//            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
//        }
//    }
//
//
//}