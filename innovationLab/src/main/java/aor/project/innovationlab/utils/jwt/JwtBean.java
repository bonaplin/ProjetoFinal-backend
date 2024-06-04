package aor.project.innovationlab.utils.jwt;

import aor.project.innovationlab.bean.SessionBean;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.enums.UserType;
import aor.project.innovationlab.utils.logs.LoggerUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

/**
 * This class is responsible for generating and validating JWT tokens.
 */
@Stateless
public class JwtBean {

    @EJB
    UserDao userDao;
    @Inject
    SessionBean sessionBean;
    private final Key key;

    /**
     * Constructor that generates a secret key.
     */
    public JwtBean() {
        // Gera uma chave secreta. Em um aplicativo real, você deve armazenar essa chave de forma segura.
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public Claims decodeJWT(String jwt) {
        // Parse the JWT string and get the claims
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt);

        return jws.getBody();
    }

    /**
     * Method to generate a JWT token for a user.
     * @param email
     * @param type
     * @return
     */
    public String generateToken(String email, UserType type) {

        Instant exp = sessionBean.generateExpirationDate();

        return Jwts.builder()
                .setSubject(email)
                .claim("role", type.name())
                .claim("email", email)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    /**
     * Method to validate a token.
     * It checks if the token is expired or if the signature does not match.
     * @param token
     * @param requestContext
     * @return
     */
    private String validateToken(String token, ContainerRequestContext requestContext){
        String log = "Attempting to validate token";
        String useremail="";
        try{
            useremail = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            LoggerUtil.logError(log, "JWT token is expired", null, token);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("JWT token is expired").build());
        }catch (Exception e){
            LoggerUtil.logError(log, e.getMessage(), null,token);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.").build());
        }
        return useremail;
    }

    /**
     * Method to validate a user.
     * It checks if the user is active.
     * If the user is not found, it returns a 401 response.
     * @param token
     * @param requestContext
     */
    public void validateUser(String token, ContainerRequestContext requestContext) {
        String log = "Attempting to validate user";
        String userEmail = validateToken(token, requestContext);
        if(userEmail == null || userEmail.isEmpty() ){
            LoggerUtil.logError(log, "User not found", userEmail, token);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }
        UserEntity userEntity = userDao.findUserByEmail(userEmail);
        if (userEntity == null) {
            LoggerUtil.logError(log, "UserEntity not found", userEmail, token);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }
        if (!userEntity.getActive()) {
            LoggerUtil.logError(log, "User is not active", userEmail, token);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("User is not active").build());

        }
    }
}