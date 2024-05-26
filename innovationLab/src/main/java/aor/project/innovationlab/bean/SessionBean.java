package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.SessionDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.dto.session.SessionLoginDto;
import aor.project.innovationlab.dto.user.UserLogInDto;
import aor.project.innovationlab.entity.LogEntity;
import aor.project.innovationlab.entity.SessionEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.utils.PasswordUtil;
import aor.project.innovationlab.enums.TokenStatus;
import aor.project.innovationlab.utils.TokenUtil;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class SessionBean  {

    @EJB
    private UserDao userDao;
    @EJB
    private SessionDao sessionDao;

    public static int DEFAULT_TOKEN_EXPIRATION_MINUTES = 5;

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
        SessionLoginDto sessionLoginDto = new SessionLoginDto();
        if(userLogInDto == null) return null;

        String email = userLogInDto.getEmail();
        String password = userLogInDto.getPassword();
        if(email == null || password == null) return null;

        UserEntity userEntity = userDao.findUserByEmail(email);
        if (userEntity == null) return null;

        if (PasswordUtil.checkPassword(password, userEntity.getPassword())) {
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
    public boolean logout(String token){
        SessionEntity sessionEntity = sessionDao.findSessionByToken(token);
        if(sessionEntity == null) return false;

        TokenStatus tokenStatus = isValidUserByToken(token);
        if(!tokenStatus.equals(TokenStatus.VALID)) return false;
        sessionEntity.setActive(false);
        sessionEntity.setLogoutDate(Instant.now());
        sessionDao.merge(sessionEntity);
        return true;
    }

    /**
     * Method to create a session for a user
     * It creates a session with a random token and sets the expiration date
     *
     * @param userEntity
     * @return
     */
    private String createSession(UserEntity userEntity) {
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setUser(userEntity);
        sessionEntity.setToken(TokenUtil.generateToken());
        sessionEntity.setExpirationDate(generateExpirationDate());
        try{
            sessionDao.persist(sessionEntity);
        }
        catch (Exception e){
            sessionEntity.setToken(UUID.randomUUID().toString());
            sessionDao.persist(sessionEntity);
            System.out.println("Token already exists, creating new token");
        }
        return sessionEntity.getToken();
    }

    /**
     * Method to generate the expiration date of a session
     * @return
     */
    private Instant generateExpirationDate() {
        return Instant.now().plus(Duration.ofMinutes(DEFAULT_TOKEN_EXPIRATION_MINUTES));
    }

    public void generateSessionToken(UserEntity userEntity) {
        String newToken = TokenUtil.generateToken();
        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setUser(userEntity);
        sessionEntity.setToken(newToken);
        sessionEntity.setExpirationDate(generateExpirationDate());
        sessionDao.merge(sessionEntity);
    }

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
}
