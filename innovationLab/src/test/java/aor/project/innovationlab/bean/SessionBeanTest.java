package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.AppConfigDao;
import aor.project.innovationlab.dao.SessionDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.dto.session.SessionLoginDto;
import aor.project.innovationlab.dto.user.UserLogInDto;
import aor.project.innovationlab.entity.AppConfigEntity;
import aor.project.innovationlab.entity.SessionEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.enums.TokenStatus;
import aor.project.innovationlab.enums.UserType;
import aor.project.innovationlab.utils.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SessionBeanTest {

    @Mock
    private AppConfigDao appConfigDao; // Mock AppConfigDao

    @Mock
    private SessionDao sessionDao;

    @InjectMocks
    private SessionBean sessionBean;

    @Mock
    private UserDao userDao;

    @Mock
    private EmailBean emailBean;

    @Captor
    private ArgumentCaptor<SessionEntity> sessionEntityCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testIsValidUserByToken_NotFound() {
        // Arrange
        String token = "notFoundToken";
        when(sessionDao.findSessionByToken(token)).thenReturn(null);

        // Act
        TokenStatus status = sessionBean.isValidUserByToken(token);

        // Assert
        assertEquals(TokenStatus.NOT_FOUND, status);
    }

    @Test
    void testIsValidUserByToken_Expired() {
        // Arrange
        String token = "expiredToken";
        SessionEntity expiredSession = new SessionEntity();
        expiredSession.setExpirationDate(Instant.now().minusSeconds(3600)); // 1 hour ago
        when(sessionDao.findSessionByToken(token)).thenReturn(expiredSession);

        // Act
        TokenStatus status = sessionBean.isValidUserByToken(token);

        // Assert
        assertEquals(TokenStatus.EXPIRED, status);
    }

    @Test
    void testValidateUserToken_TokenNotFound() {
        // Arrange
        String token = "notFoundToken";
        when(sessionDao.findSessionByToken(token)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> sessionBean.validateUserToken(token));
        assertTrue(thrown.getMessage().contains("Token not found"));
    }

    @Test
    void testValidateUserToken_TokenExpired() {
        // Arrange
        String token = "expiredToken";
        SessionEntity expiredSession = new SessionEntity();
        expiredSession.setExpirationDate(Instant.now().minus(1, ChronoUnit.HOURS));
        when(sessionDao.findSessionByToken(token)).thenReturn(expiredSession);

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> sessionBean.validateUserToken(token));
        assertTrue(thrown.getMessage().contains("Token expired"));
    }







}
