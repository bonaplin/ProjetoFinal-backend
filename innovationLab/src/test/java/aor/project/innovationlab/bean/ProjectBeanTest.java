package aor.project.innovationlab.bean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import aor.project.innovationlab.bean.ProjectBean;
import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.dto.response.LabelValueDto;
import aor.project.innovationlab.dto.response.LongIdResponseDto;
import aor.project.innovationlab.dto.statistics.StatisticsDto;
import aor.project.innovationlab.dto.statistics.UserSettingsDto;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.NotificationType;
import aor.project.innovationlab.enums.ProjectStatus;
import aor.project.innovationlab.enums.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ProjectBeanTest {

    @Mock
    private SessionDao sessionDao;

    @Mock
    private AppConfigDao appConfigDao;

    @Mock
    private LabDao labDao;

    @Mock
    private ProjectDao projectDao;

    @Mock
    private LogBean logBean;

    @Mock
    private UserDao userDao;

    @Mock
    private NotificationBean notificationBean;

    @InjectMocks
    private ProjectBean projectBean;

    @InjectMocks
    private SessionBean sessionBean;

    @InjectMocks
    private UserBean userBean;

    private SessionEntity validSession;
    private AppConfigEntity appConfig;
    private List<LabEntity> labEntities;
    private LabEntity labEntity;
    private StatisticsDto statisticsDto;

    private UserEntity mockUser;
    private UserEntity mockUserAdmin;
    private ProjectEntity mockProject;
    private ProjectUserEntity mockProjectUser;
    private LongIdResponseDto dto;
    private String token;
    private Long projectId;

    private SessionEntity adminSession;
    private SessionEntity nonAdminSession;
    private UserEntity adminUser;
    private UserEntity nonAdminUser;
    private UserEntity targetUser;

    @BeforeEach
    public void setUp() {
        // Set up mock user and admin
        mockUser = new UserEntity();
        mockUser.setId(2L);
        mockUser.setEmail("user@example.com");
        mockUser.setRole(UserType.MANAGER);

        mockUserAdmin = new UserEntity();
        mockUserAdmin.setId(2L);
        mockUserAdmin.setEmail("admin@admin.com");
        mockUserAdmin.setRole(UserType.ADMIN);

        // Set up a valid session
        validSession = new SessionEntity();
        validSession.setToken("valid-token");
        validSession.setExpirationDate(Instant.now().plusSeconds(3600)); // expires in one hour
        validSession.setUser(mockUser);

        // Set up app config
        appConfig = new AppConfigEntity();
        appConfig.setTimeOut(30);
        appConfig.setTimeOutAdmin(60);
        appConfig.setMaxUsers(10);
        appConfig.setUser(mockUserAdmin);

        // Set up lab entity
        labEntity = new LabEntity();
        labEntity.setId(1);
        labEntity.setLocation("Lab 1");
        labEntities = Arrays.asList(labEntity);

        // Set up DTOs
        statisticsDto = new StatisticsDto();
        dto = new LongIdResponseDto();
        dto.setValue(ProjectStatus.PLANNING.getValue());

        // Set up project and project user
        mockProject = new ProjectEntity();
        mockProject.setStatus(ProjectStatus.IN_PROGRESS);
        mockProjectUser = new ProjectUserEntity();
        mockProjectUser.setRole(UserType.MANAGER);

        // Set up token and project ID
        token = "valid-token";
        projectId = 1L;

        MockitoAnnotations.initMocks(this);

        adminUser = new UserEntity();
        adminUser.setRole(UserType.ADMIN);
        adminUser.setEmail("admin@example.com");

        nonAdminUser = new UserEntity();
        nonAdminUser.setRole(UserType.NORMAL);
        nonAdminUser.setEmail("user@example.com");

        adminSession = new SessionEntity();
        adminSession.setToken("admin-token");
        adminSession.setUser(adminUser);

        nonAdminSession = new SessionEntity();
        nonAdminSession.setToken("user-token");
        nonAdminSession.setUser(nonAdminUser);

        targetUser = new UserEntity();
        targetUser.setEmail("target@example.com");
        targetUser.setRole(UserType.NORMAL);
    }

    @Test
    public void testGetStatisticsByLab_ValidTokenAndLab() {
        // Configure mocks for the session and app config
        when(sessionDao.findSessionByToken(token)).thenReturn(validSession);
        when(appConfigDao.findLastConfig()).thenReturn(appConfig);

        // Configure mocks for the lab and project DAOs
        when(labDao.findAll()).thenReturn(labEntities);
        when(labDao.findLabById(labEntity.getId())).thenReturn(labEntity);
        when(projectDao.getStatisticsByLab(labEntity.getId())).thenReturn(statisticsDto);

        // Call the method under test
        UserSettingsDto result = projectBean.getStatisticsByLab(token, labEntity.getId());

        // Asserts to verify expected behavior
        assertNotNull(result);
        assertEquals(appConfig.getTimeOut(), result.getTimeout());
        assertEquals(labEntities.size(), result.getLabs().size());
        assertEquals(statisticsDto, result.getStatistics());
    }


    @Test
    public void testGetStatisticsByLab_ValidTokenNullLab() {
        when(sessionDao.findSessionByToken("valid-token")).thenReturn(validSession);
        when(appConfigDao.findLastConfig()).thenReturn(appConfig);
        when(labDao.findAll()).thenReturn(labEntities);

        UserSettingsDto result = projectBean.getStatisticsByLab("valid-token", null);

        assertNotNull(result);
        assertEquals(30, result.getTimeout());
        assertEquals(1, result.getLabs().size());
        assertNull(result.getStatistics());
    }

    @Test
    public void testGetStatisticsByLab_InvalidToken() {
        when(sessionDao.findSessionByToken("invalid-token")).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                projectBean.getStatisticsByLab("invalid-token", 1));
        assertEquals("Invalid token", exception.getMessage());
    }

    @Test
    public void testGetStatisticsByLab_ValidTokenInvalidLab() {
        when(sessionDao.findSessionByToken("valid-token")).thenReturn(validSession);
        when(appConfigDao.findLastConfig()).thenReturn(appConfig);
        when(labDao.findAll()).thenReturn(labEntities);
        when(labDao.findLabById(999)).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                projectBean.getStatisticsByLab("valid-token", 999));
        assertEquals("Lab not found", exception.getMessage());
    }

    @Test
    public void testGetStatisticsByLab_ProjectDaoThrowsException() {
        when(sessionDao.findSessionByToken("valid-token")).thenReturn(validSession);
        when(appConfigDao.findLastConfig()).thenReturn(appConfig);
        when(labDao.findAll()).thenReturn(labEntities);
        when(labDao.findLabById(1)).thenReturn(labEntity);
        when(projectDao.getStatisticsByLab(1)).thenThrow(new RuntimeException());

        UserSettingsDto result = projectBean.getStatisticsByLab("valid-token", 1);

        assertNotNull(result);
        assertEquals(30, result.getTimeout());
        assertEquals(1, result.getLabs().size());
    }

    @Test
    public void testGetProjectUsersEmails() {
        // Configurar dados de teste
        Long projectId = 1L;
        String currentEmail = "currentuser@example.com";

        UserEntity user1 = new UserEntity();
        user1.setEmail("user1@example.com");

        UserEntity user2 = new UserEntity();
        user2.setEmail("user2@example.com");

        UserEntity currentUser = new UserEntity();
        currentUser.setEmail(currentEmail);

        List<UserEntity> projectUsers = Arrays.asList(user1, user2, currentUser);

        ProjectEntity project = new ProjectEntity();
        project.setId(projectId);

        // Configurar mocks
        when(projectDao.findUsersByProjectId(projectId)).thenReturn(projectUsers);

        // Chamar o método sob teste
        List<String> result = projectBean.getProjectUsersEmails(project, currentEmail);

        // Verificar resultados
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("user1@example.com"));
        assertTrue(result.contains("user2@example.com"));
        assertFalse(result.contains(currentEmail));
    }

    @Test
    public void testGetProjectUsersEmails_NoUsers() {
        Long projectId = 1L;
        String currentEmail = "currentuser@example.com";

        ProjectEntity project = new ProjectEntity();
        project.setId(projectId);

        // Configurar mocks
        when(projectDao.findUsersByProjectId(projectId)).thenReturn(Collections.emptyList());

        // Chamar o método sob teste
        List<String> result = projectBean.getProjectUsersEmails(project, currentEmail);

        // Verificar resultados
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetProjectUsersEmails_OnlyCurrentUser() {
        Long projectId = 1L;
        String currentEmail = "currentuser@example.com";

        UserEntity currentUser = new UserEntity();
        currentUser.setEmail(currentEmail);

        ProjectEntity project = new ProjectEntity();
        project.setId(projectId);

        // Configurar mocks
        when(projectDao.findUsersByProjectId(projectId)).thenReturn(Collections.singletonList(currentUser));

        // Chamar o método sob teste
        List<String> result = projectBean.getProjectUsersEmails(project, currentEmail);

        // Verificar resultados
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetProjectUsersEmails_DuplicateEmails() {
        Long projectId = 1L;
        String currentEmail = "currentuser@example.com";

        UserEntity user1 = new UserEntity();
        user1.setEmail("user@example.com");

        UserEntity user2 = new UserEntity();
        user2.setEmail("user@example.com");

        UserEntity currentUser = new UserEntity();
        currentUser.setEmail(currentEmail);

        List<UserEntity> projectUsers = Arrays.asList(user1, user2, currentUser);

        ProjectEntity project = new ProjectEntity();
        project.setId(projectId);

        // Configurar mocks
        when(projectDao.findUsersByProjectId(projectId)).thenReturn(projectUsers);

        // Chamar o método sob teste
        List<String> result = projectBean.getProjectUsersEmails(project, currentEmail);

        // Verificar resultados
        assertNotNull(result);
        assertEquals(2, result.size()); // Espera-se 2 usuários, mesmo com e-mails duplicados, exceto o atual
        assertTrue(result.contains("user@example.com"));
        assertFalse(result.contains(currentEmail));
    }

    @Test
    public void testGetProjectUsersEmails_NullProject() {
        String currentEmail = "currentuser@example.com";

        // Chamar o método sob teste
        List<String> result = projectBean.getProjectUsersEmails(null, currentEmail);

        // Verificar resultados
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetProjectUsersEmails_NullCurrentEmail() {
        Long projectId = 1L;

        UserEntity user1 = new UserEntity();
        user1.setEmail("user1@example.com");

        UserEntity user2 = new UserEntity();
        user2.setEmail("user2@example.com");

        List<UserEntity> projectUsers = Arrays.asList(user1, user2);

        ProjectEntity project = new ProjectEntity();
        project.setId(projectId);

        // Configurar mocks
        when(projectDao.findUsersByProjectId(projectId)).thenReturn(projectUsers);

        // Chamar o método sob teste
        List<String> result = projectBean.getProjectUsersEmails(project, null);

        // Verificar resultados
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("user1@example.com"));
        assertTrue(result.contains("user2@example.com"));
    }

    @Test
    public void testGetProjectUsersEmails_EmptyCurrentEmail() {
        Long projectId = 1L;
        String currentEmail = "";

        UserEntity user1 = new UserEntity();
        user1.setEmail("user1@example.com");

        UserEntity user2 = new UserEntity();
        user2.setEmail("user2@example.com");

        List<UserEntity> projectUsers = Arrays.asList(user1, user2);

        ProjectEntity project = new ProjectEntity();
        project.setId(projectId);

        // Configurar mocks
        when(projectDao.findUsersByProjectId(projectId)).thenReturn(projectUsers);

        // Chamar o método sob teste
        List<String> result = projectBean.getProjectUsersEmails(project, currentEmail);

        // Verificar resultados
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("user1@example.com"));
        assertTrue(result.contains("user2@example.com"));
    }


    @Test
    public void testUpdateRole_InvalidToken() {
        String token = "invalid-token";
        LabelValueDto dto = new LabelValueDto("target@example.com", 2L);

        when(sessionDao.findSessionByToken(token)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            projectBean.updateRole(token, dto);
        });

        assertEquals("Invalid token", exception.getMessage());
        verify(sessionDao).findSessionByToken(token);
        verifyNoInteractions(userDao);
    }

    @Test
    public void testUpdateRole_UserNotAuthorized() {
        String token = "user-token";
        LabelValueDto dto = new LabelValueDto("target@example.com", 2L);

        when(sessionDao.findSessionByToken(token)).thenReturn(nonAdminSession);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            projectBean.updateRole(token, dto);
        });

        assertEquals("User is not authorized to access this resource", exception.getMessage());
        verify(sessionDao).findSessionByToken(token);
        verifyNoInteractions(userDao);
    }

    @Test
    public void testUpdateRole_UserNotFound() {
        String token = "admin-token";
        LabelValueDto dto = new LabelValueDto("nonexistent@example.com", 2L);

        when(sessionDao.findSessionByToken(token)).thenReturn(adminSession);
        when(userDao.findUserByEmail(dto.getLabel())).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            projectBean.updateRole(token, dto);
        });

        assertEquals("User not found", exception.getMessage());
        verify(sessionDao).findSessionByToken(token);
        verify(userDao).findUserByEmail(dto.getLabel());
        verifyNoMoreInteractions(userDao);
    }


    @Test
    public void testUpdateRole_Success() {
        String token = "admin-token";
        LabelValueDto dto = new LabelValueDto("target@example.com", 1L); // Change role to ADMIN

        when(sessionDao.findSessionByToken(token)).thenReturn(adminSession);
        when(userDao.findUserByEmail(dto.getLabel())).thenReturn(targetUser);

        projectBean.updateRole(token, dto);

        assertEquals(UserType.ADMIN, targetUser.getRole());
        verify(sessionDao).findSessionByToken(token);
        verify(userDao).findUserByEmail(dto.getLabel());
        verify(userDao).merge(targetUser);
    }

    // Other tests...

}
