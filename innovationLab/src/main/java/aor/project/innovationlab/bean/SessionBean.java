package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.SessionDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.dto.user.UserLogInDto;
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

    public TokenStatus isValidUserByToken(String token){
        SessionEntity sessionEntity = sessionDao.findSessionByToken(token);
        if(sessionEntity == null) return TokenStatus.NOT_FOUND;
        if(sessionEntity.getExpirationDate().isBefore(Instant.now())) return TokenStatus.EXPIRED;
        return sessionEntity.isActive() ? TokenStatus.VALID : TokenStatus.EXPIRED;
    }

    /**
     * Method to login a user
     * It checks if the user exists and if the password is correct
     * If the user exists and the password is correct, it creates a session for the user
     * The session is created with a random token
     * @param userLogInDto - dto with the user email and password
     * @return - true if the user exists and the password is correct, false otherwise
     */
    public String login(UserLogInDto userLogInDto) {
        if(userLogInDto == null) return null;

        String email = userLogInDto.getEmail();
        String password = userLogInDto.getPassword();
        if(email == null || password == null) return null;

        UserEntity userEntity = userDao.findUserByEmail(email);
        if (userEntity == null) return null;

        if (PasswordUtil.checkPassword(password, userEntity.getPassword())) {
            return createSession(userEntity);
        }
        return null;
    }

    public boolean logout(String token){
        SessionEntity sessionEntity = sessionDao.findSessionByToken(token);
        System.out.println("logout");
        if(sessionEntity == null) return false;

        TokenStatus tokenStatus = isValidUserByToken(token);
        if(!tokenStatus.equals(TokenStatus.VALID)) return false;
        System.out.println("logout2");
        sessionEntity.setActive(false);
        sessionEntity.setLogoutDate(Instant.now());
        sessionDao.merge(sessionEntity);
        System.out.println("logout3");
        return true;
    }

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

    private Instant generateExpirationDate() {
        return Instant.now().plus(Duration.ofMinutes(DEFAULT_TOKEN_EXPIRATION_MINUTES));
    }
}
