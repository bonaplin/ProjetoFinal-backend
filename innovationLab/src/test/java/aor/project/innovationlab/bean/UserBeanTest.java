package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.LabDao;
import aor.project.innovationlab.dao.ProjectUserDao;
import aor.project.innovationlab.dao.SessionDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.dto.response.LabelValueDto;
import aor.project.innovationlab.dto.response.PaginatedResponse;
import aor.project.innovationlab.dto.user.UserAddToProjectDto;
import aor.project.innovationlab.dto.user.UserLogInDto;
import aor.project.innovationlab.dto.user.UserOwnerProfileDto;
import aor.project.innovationlab.dto.user.password.UserRecoverPasswordDto;
import aor.project.innovationlab.entity.LabEntity;
import aor.project.innovationlab.entity.ProjectUserEntity;
import aor.project.innovationlab.entity.SessionEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.service.UserService;
import aor.project.innovationlab.utils.PasswordUtil;
import aor.project.innovationlab.utils.TokenUtil;
import aor.project.innovationlab.utils.logs.LoggerUtil;
import aor.project.innovationlab.validator.UserValidator;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserBeanTest {
    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserBean userBean;

    @Mock
    private SessionBean sessionBean;

    @Mock
    private LoggerUtil loggerUtil;

    @Mock
    private TokenUtil tokenUtil;

    @Mock
    private LabDao labDao;

    @Mock
    private ProjectUserDao projectUserDao;

    @Mock
    private PasswordUtil passwordUtil;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserService userService;

    @Mock
    private SessionDao sessionDao;

    private UserEntity userEntity;
    private UserOwnerProfileDto dto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");
        userEntity.setTokenVerification("validToken");
        userEntity.setTokenExpiration(Instant.now().plusSeconds(3600)); // Token expires in 1 hour

        dto = new UserOwnerProfileDto();
        dto.setUsername("newUsername");
        dto.setFirstname("NewFirstName");
        dto.setLastname("NewLastName");
        dto.setLab(1);
        dto.setAbout("NewAbout");
        dto.setImagePath("NewImagePath");
    }
    @Test
    void verifyToken_UserFound_TokenNotExpired() {
        // Arrange
        when(userDao.findUserByToken("validToken")).thenReturn(userEntity);

        // Act & Assert
        assertTrue(userBean.verifyToken("validToken"));
    }

    @Test
    void verifyToken_UserFound_TokenExpired() {
        // Arrange
        userEntity.setTokenExpiration(Instant.now().minusSeconds(3600)); // Token expired 1 hour ago
        when(userDao.findUserByToken("expiredToken")).thenReturn(userEntity);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userBean.verifyToken("expiredToken"));
    }

    @Test
    void verifyToken_UserNotFound() {
        // Arrange
        when(userDao.findUserByToken("invalidToken")).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userBean.verifyToken("invalidToken"));
    }

    @Test
    void verifyToken_UserEntityNotFound() {
        // Arrange
        when(userDao.findUserByToken("invalidToken")).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userBean.verifyToken("invalidToken"));
    }

    @Test
    void changePassword_UserNotFound() {
        // Arrange
        String token = "invalidToken";
        UserRecoverPasswordDto dto = new UserRecoverPasswordDto("newPassword", "newPassword");
        when(userDao.findUserByToken(token)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userBean.changePassword(token, dto));
    }

    @Test
    void changePassword_TokenExpired() {
        // Arrange
        String token = "expiredToken";
        UserRecoverPasswordDto dto = new UserRecoverPasswordDto("newPassword", "newPassword");
        userEntity.setTokenExpiration(Instant.now().minusSeconds(3600)); // Token expired
        when(userDao.findUserByToken(token)).thenReturn(userEntity);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userBean.changePassword(token, dto));
    }

    @Test
    void cleanToken_Success() {
        // Act
        userBean.cleanToken(userEntity);

        // Assert
        verify(userDao).merge(userEntity);
        assertNull(userEntity.getTokenVerification());
        assertNull(userEntity.getTokenExpiration());
    }

    @Test
    void cleanTokens_Success() {
        // Act
        userBean.cleanToken(userEntity);

        // Assert
        verify(userDao).merge(userEntity);
        assertNull(userEntity.getTokenVerification());
        assertNull(userEntity.getTokenExpiration());
    }

    @Test
    void cleanToken_IllegalArgumentException() {
        // Arrange
        doThrow(IllegalArgumentException.class).when(userDao).merge(any(UserEntity.class));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userBean.cleanToken(userEntity));
    }

    @Test
    void clearToken_TokenNotFoundException() {
        // Arrange
        doThrow(IllegalArgumentException.class).when(userDao).merge(any(UserEntity.class));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userBean.cleanToken(userEntity));
    }

    @Test
    void cleanToken_PersistenceException() {
        // Arrange
        doThrow(PersistenceException.class).when(userDao).merge(any(UserEntity.class));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userBean.cleanToken(userEntity));
    }

    @Test
    void cleanToken_UnexpectedException() {
        // Arrange
        doThrow(RuntimeException.class).when(userDao).merge(any(UserEntity.class));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userBean.cleanToken(userEntity));
    }

    @Test
    void loginWithValidation_UserLogInDtoIsNull() {
        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> userBean.loginWithValidation(null));
        assertTrue(thrown.getMessage().contains("Fill the fields."));
    }

    @Test
    void loginWithValidation_EmailOrPasswordIsNull() {
        // Arrange
        UserLogInDto userLogInDto = new UserLogInDto();
        userLogInDto.setEmail(null); // Email is null

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> userBean.loginWithValidation(userLogInDto));
        assertTrue(thrown.getMessage().contains("Email or password is null."));
    }

    @Test
    void loginWithValidation_EmailAndPasswordDoNotMatch() {
        // Arrange
        UserLogInDto userLogInDto = new UserLogInDto();
        userLogInDto.setEmail("user@example.com");
        userLogInDto.setPassword("password");
        when(sessionBean.login(userLogInDto)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> userBean.loginWithValidation(userLogInDto));
        assertTrue(thrown.getMessage().contains("Email and password do not match."));
    }

    @Test
    void verifyToken_isValidAndNotExpired() {
        userEntity.setTokenExpiration(Instant.now().plusSeconds(3600)); // Token expires in 1 hour
        when(userDao.findUserByToken("validToken")).thenReturn(userEntity);

        assertTrue(userBean.verifyToken("validToken"));
    }

    @Test
    void verifyToken_ValidNotExpired() {
        userEntity.setTokenExpiration(Instant.now().plusSeconds(3600)); // Token expires in 1 hour
        when(userDao.findUserByToken("validToken")).thenReturn(userEntity);

        assertTrue(userBean.verifyToken("validToken"));
    }

    @Test
    void verifyToken_ValidButExpired() {
        userEntity.setTokenExpiration(Instant.now().minusSeconds(3600)); // Token expired 1 hour ago
        when(userDao.findUserByToken("validToken")).thenReturn(userEntity);

        assertThrows(IllegalArgumentException.class, () -> userBean.verifyToken("validToken"), "Token expired.");
    }

    @Test
    void verifyToken_NotFound() {
        when(userDao.findUserByToken("invalidToken")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> userBean.verifyToken("invalidToken"), "User not found.");
    }

    @Test
    public void testGetUsersForInfo_ValidInput() {
        // Arrange
        String token = "validToken";
        long projectId = 1L;

        SessionEntity sessionEntity = new SessionEntity(); // Setup the session entity
        when(sessionDao.findSessionByToken(token)).thenReturn(sessionEntity);

        List<ProjectUserEntity> users = new ArrayList<>();
        ProjectUserEntity user1 = new ProjectUserEntity(); // Setup user1 with required fields and set as active
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L); // Set the ID or any other necessary fields
        user1.setUser(userEntity);
        user1.setActive(true);
        users.add(user1);

        when(projectUserDao.findProjectUserByProjectId(projectId)).thenReturn(users);

        // Act
        List<UserAddToProjectDto> result =userBean.getUsersForInfo(token, projectId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(UserAddToProjectDto.class, result.get(0).getClass());
    }

    @Test
    public void testGetUsersForInfo_NoActiveUsers() {
        // Arrange
        String token = "validToken";
        long projectId = 1L;

        SessionEntity sessionEntity = new SessionEntity(); // Setup the session entity
        when(sessionDao.findSessionByToken(token)).thenReturn(sessionEntity);

        List<ProjectUserEntity> users = new ArrayList<>();
        ProjectUserEntity user1 = new ProjectUserEntity(); // Setup user1 with required fields and set as inactive
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L); // Set the ID or any other necessary fields
        user1.setUser(userEntity);
        user1.setActive(false);
        users.add(user1);

        when(projectUserDao.findProjectUserByProjectId(projectId)).thenReturn(users);

        // Act
        List<UserAddToProjectDto> result =userBean.getUsersForInfo(token, projectId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetUsersForInfos_NoActiveUsers() {
        // Arrange
        String token = "validToken";
        long projectId = 1L;

        SessionEntity sessionEntity = new SessionEntity(); // Setup the session entity
        when(sessionDao.findSessionByToken(token)).thenReturn(sessionEntity);

        List<ProjectUserEntity> users = new ArrayList<>();
        ProjectUserEntity user1 = new ProjectUserEntity(); // Setup user1 with required fields and set as inactive
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L); // Set the ID or any other necessary fields
        user1.setUser(userEntity);
        user1.setActive(false);
        users.add(user1);

        when(projectUserDao.findProjectUserByProjectId(projectId)).thenReturn(users);

        // Act
        List<UserAddToProjectDto> result =userBean.getUsersForInfo(token, projectId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetUsersForInfo_MixedActiveInactiveUsers() {
        // Arrange
        String token = "validToken";
        long projectId = 1L;

        SessionEntity sessionEntity = new SessionEntity(); // Setup the session entity
        when(sessionDao.findSessionByToken(token)).thenReturn(sessionEntity);

        List<ProjectUserEntity> users = new ArrayList<>();
        ProjectUserEntity activeUser = new ProjectUserEntity(); // Setup active user
        UserEntity activeUserEntity = new UserEntity();
        activeUserEntity.setId(1L); // Set the ID or any other necessary fields
        activeUser.setUser(activeUserEntity);
        activeUser.setActive(true);

        ProjectUserEntity inactiveUser = new ProjectUserEntity(); // Setup inactive user
        UserEntity inactiveUserEntity = new UserEntity();
        inactiveUserEntity.setId(2L); // Set the ID or any other necessary fields
        inactiveUser.setUser(inactiveUserEntity);
        inactiveUser.setActive(false);

        users.add(activeUser);
        users.add(inactiveUser);

        when(projectUserDao.findProjectUserByProjectId(projectId)).thenReturn(users);

        // Act
        List<UserAddToProjectDto> result =userBean.getUsersForInfo(token, projectId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
//        assertTrue(result.stream().allMatch(dto -> dto.isActive()));
    }

    @Test
    public void testGetUsersToTask_ValidInput() {
        // Arrange
        String token = "validToken";
        Long projectId = 1L;

        SessionEntity sessionEntity = new SessionEntity(); // Setup the session entity
        when(sessionDao.findSessionByToken(token)).thenReturn(sessionEntity);

        List<UserEntity> users = new ArrayList<>();
        UserEntity user1 = new UserEntity();
        user1.setId(1L); // Set the ID or any other necessary fields
        user1.setFirstname("User One");
        users.add(user1);

        when(userDao.findUsersByProjectId(projectId)).thenReturn(users);

        // Act
        List<LabelValueDto> result =userBean.getUsersToTask(token, projectId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getValue());
    }

    @Test
    public void testGetUsersToTask_NoUsers() {
        // Arrange
        String token = "validToken";
        Long projectId = 1L;

        SessionEntity sessionEntity = new SessionEntity(); // Setup the session entity
        when(sessionDao.findSessionByToken(token)).thenReturn(sessionEntity);

        List<UserEntity> users = new ArrayList<>();
        when(userDao.findUsersByProjectId(projectId)).thenReturn(users);

        // Act
        List<LabelValueDto> result =userBean.getUsersToTask(token, projectId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
