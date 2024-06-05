package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.SessionDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.utils.TokenUtil;

import aor.project.innovationlab.dto.session.SessionLoginDto;
import aor.project.innovationlab.dto.user.UserLogInDto;
import aor.project.innovationlab.entity.SessionEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.utils.PasswordUtil;
import aor.project.innovationlab.enums.TokenStatus;
import aor.project.innovationlab.utils.logs.LoggerUtil;
import io.jsonwebtoken.Claims;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@ApplicationScoped
public class SessionBean  {

    @EJB
    private UserDao userDao;
    @EJB
    private SessionDao sessionDao;
//
//    @EJB
//    private JwtBean jwtService;

    public static int DEFAULT_TOKEN_EXPIRATION_MINUTES = 60;

    /**
     * Method to set the default expiration time of a token
     * It sets the default expiration time of a token in minutes
     * @param minutes
     * @param token
     */
    private void setDefaultTokenExpirationMinutes(int minutes, String token){
        TokenStatus tokenStatus = isValidUserByToken(token);
        if(!tokenStatus.equals(TokenStatus.VALID)) return;

        //TODO: verify if the user has permission to change the expiration time

        DEFAULT_TOKEN_EXPIRATION_MINUTES = minutes;
    }

    /**
     * Method to check if a token is valid
     * It checks if the token exists, if it is active and if it is not expired
     * If is valid, it returns TokenStatus.VALID and sets the expiration date to a new value
     * @param token
     * @return - TokenStatus.VALID if the token is valid, TokenStatus.NOT_FOUND if the token does not exist, TokenStatus.EXPIRED if the token is expired
     */
    public TokenStatus isValidUserByToken(String token){
        SessionEntity sessionEntity = sessionDao.findSessionByToken(token);
        if(sessionEntity == null) return TokenStatus.NOT_FOUND;
        if(sessionEntity.getExpirationDate().isBefore(Instant.now())){
            return TokenStatus.EXPIRED;
        }
        else{
            sessionEntity.setExpirationDate(generateExpirationDate());
            sessionDao.merge(sessionEntity);
        }
        return sessionEntity.isActive() ? TokenStatus.VALID : TokenStatus.EXPIRED;
    }

    /**
     * Method to validate a user token
     * It checks if the token exists, if it is active and if it is not expired
     * If the token is valid, it updates the expiration date
     * If the token is not valid, it throws an exception
     * @param token
     */
    public void validateUserToken(String token) {
        SessionEntity sessionEntity = sessionDao.findSessionByToken(token);
        if(sessionEntity == null) {
            // Log the event
            System.out.println("Token not found: " + token);
            throw new IllegalArgumentException("Token not found");
        }
        if(sessionEntity.getExpirationDate().isBefore(Instant.now())) {
            // Log the event
            System.out.println("Token expired: " + token);
            throw new IllegalArgumentException("Token expired");
        }
        if(!sessionEntity.isActive()) {
            // Log the event
            System.out.println("Token not active: " + token);
            throw new IllegalArgumentException("Token not active");
        }
        // If the token is valid, update its expiration date
        updateTokenExpiration(sessionEntity);
    }

    private void updateTokenExpiration(SessionEntity sessionEntity) {
        sessionEntity.setExpirationDate(generateExpirationDate());
        sessionDao.merge(sessionEntity);
    }

    /**
     * Method to login a user
     * It checks if the user exists and if the password is correct
     * If the user exists and the password is correct, it creates a session for the user
     * The session is created with a random token
     * @param userLogInDto - dto with the user email and password
     * @return - true if the user exists and the password is correct, false otherwise
     */
    public SessionLoginDto login(UserLogInDto userLogInDto) {
        String log = "Attempting to login";
        if(userLogInDto == null){
            LoggerUtil.logError(log, "UserLogInDto is null",null,null);
            throw new IllegalArgumentException("UserLogInDto is null");
        }

        String email = userLogInDto.getEmail();
        String password = userLogInDto.getPassword();
        if(email == null || password == null) {
            LoggerUtil.logError(log, "Email: "+email+" or password is null",null,null);
            throw new IllegalArgumentException("Email or password is null");
        }

        UserEntity userEntity = userDao.findUserByEmail(email);
        if (userEntity == null) {
            return null;
        }

        if (PasswordUtil.checkPassword(password, userEntity.getPassword())) {
            SessionLoginDto sessionLoginDto = new SessionLoginDto();
            sessionLoginDto.setToken(createSession(userEntity));
            return sessionLoginDto;
        }
        return null;
    }

    /**
     * Method to logout a user
     * It sets the session as inactive and sets the logout date
     * @param token
     * @return - true if the session exists and is set as inactive, false otherwise
     */
    public void logout(String token){
        SessionEntity sessionEntity = sessionDao.findSessionByToken(token);
        if(sessionEntity == null){ throw new IllegalArgumentException("Token not found");}

        TokenStatus tokenStatus = isValidUserByToken(token);
        if(!tokenStatus.equals(TokenStatus.VALID)){ throw new IllegalArgumentException("Token not valid");}
        sessionEntity.setActive(false);
        sessionEntity.setLogoutDate(Instant.now());
        sessionDao.merge(sessionEntity);
    }

    /**
     * Method to create a session for a user
     * It creates a session for the user with a random token and an expiration date
     * The session is persisted in the database
     * If the token already exists, it generates a new token, until it finds a token that does not exist and persists the session
     *
     * @param userEntity
     * @return
     */
    private String createSession(UserEntity userEntity) {
        String log = "Attempting to create a session";
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setUser(userEntity);

//        String token = jwtService.generateToken(userEntity.getEmail(),userEntity.getRole());
//
//        Claims claims = jwtService.decodeJWT(token);
//        Date expirationDate = claims.getExpiration();
        String token = TokenUtil.generateToken();
        sessionEntity.setToken(token);
        sessionEntity.setExpirationDate(generateExpirationDate());

        while (true) {
            try {
                sessionDao.persist(sessionEntity);
                LoggerUtil.logInfo(log, "Session created",userEntity.getEmail(),sessionEntity.getToken());
                break;  // if the session is persisted, break the loop
            } catch (PersistenceException e) { // if the session is not persisted, generate a new token
                if (e.getCause() instanceof ConstraintViolationException) {
                    LoggerUtil.logError(log, "Token already exists, will try another one",userEntity.getEmail(),sessionEntity.getToken());
//                    token = jwtService.generateToken(userEntity.getEmail(),userEntity.getRole());
                    token = TokenUtil.generateToken();
                    sessionEntity.setToken(token);
                } else {
                    LoggerUtil.logError(log, "Error while creating session",userEntity.getEmail(),sessionEntity.getToken());
                    throw e; // if the exception is not a ConstraintViolationException, throw it
                }
            }
        }
        return sessionEntity.getToken();
    }

    /**
     * Method to generate the expiration date of a session
     * @return
     */
    public Instant generateExpirationDate() {
        return Instant.now().plus(Duration.ofMinutes(DEFAULT_TOKEN_EXPIRATION_MINUTES));
    }

//    public void generateSessionToken(UserEntity userEntity) {
//        String newToken = TokenUtil.generateToken();
//        SessionEntity sessionEntity = new SessionEntity();
//        sessionEntity.setUser(userEntity);
//        sessionEntity.setToken(newToken);
//        sessionEntity.setExpirationDate(generateExpirationDate());
//        sessionDao.merge(sessionEntity);
//    }

    /**
     * Method to get the user of a session
     * It returns the user of the session with the token
     * @param token
     * @return
     */
    public UserEntity getUserByToken(String token) {
        if(token == null) return null;
        SessionEntity sessionEntity = sessionDao.findSessionByToken(token);
        if(sessionEntity == null) return null;
        return sessionEntity.getUser();
    }

    public String getTokenFromAuthorizationHeader(String auth) {
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization header");
        }
        return auth.substring("Bearer".length()).trim();
    }
}
