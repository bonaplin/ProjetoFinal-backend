package aor.project.innovationlab.bean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import aor.project.innovationlab.bean.ProjectBean;
import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.dto.interests.InterestDto;
import aor.project.innovationlab.dto.product.ProductToCreateProjectDto;
import aor.project.innovationlab.dto.project.CreateProjectDto;
import aor.project.innovationlab.dto.project.ProjectCardDto;
import aor.project.innovationlab.dto.project.ProjectReadyDto;
import aor.project.innovationlab.dto.response.LabelValueDto;
import aor.project.innovationlab.dto.response.LongIdResponseDto;
import aor.project.innovationlab.dto.response.PaginatedResponse;
import aor.project.innovationlab.dto.response.ResponseYesNoInviteDto;
import aor.project.innovationlab.dto.skill.SkillDto;
import aor.project.innovationlab.dto.statistics.StatisticsDto;
import aor.project.innovationlab.dto.statistics.UserSettingsDto;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.NotificationType;
import aor.project.innovationlab.enums.ProjectStatus;
import aor.project.innovationlab.enums.UserType;
import aor.project.innovationlab.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
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
    private ProjectUserDao projectUserDao;

    @Mock
    private TaskExecutorDao taskExecutorDao;

    @Mock
    private TaskDao taskDao;

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

    private UserEntity regularUser;
    private ProjectEntity projectEntity;
    private AppConfigEntity appConfigEntity;
    private SessionEntity activeSession;
    private SessionEntity expiredSession;
    private SessionEntity inactiveSession;

    private SessionEntity adminSession;
    private SessionEntity nonAdminSession;
    private UserEntity adminUser;
    private UserEntity nonAdminUser;
    private UserEntity targetUser;
    private PaginatedResponse<ProjectEntity> paginatedResponse;
    private ProjectUserEntity projectUser;
    private List<TaskExecutorEntity> taskExecutors;
    private List<TaskEntity> tasks;
    private UserEntity creator;
    private UserEntity user;
    ProjectUserEntity projectManager;




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
        appConfig.setMaxUsers(10); // Exemplo de configuração válida
        appConfig.setTimeOutAdmin(60);
        appConfig.setTimeOut(30);
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

        adminUser = new UserEntity();
        adminUser.setRole(UserType.ADMIN);

        regularUser = new UserEntity();
        regularUser.setRole(UserType.NORMAL);

        projectEntity = new ProjectEntity();
        projectEntity.setId(1L);

        appConfigEntity = new AppConfigEntity();
        appConfigEntity.setMaxUsers(10);

        adminUser = new UserEntity();
        adminUser.setRole(UserType.ADMIN);

        regularUser = new UserEntity();
        regularUser.setRole(UserType.NORMAL);

        activeSession = new SessionEntity();
        activeSession.setToken("active-token");
        activeSession.setExpirationDate(Instant.now().plus(Duration.ofHours(1)));
        activeSession.setActive(true);
        activeSession.setUser(regularUser);

        expiredSession = new SessionEntity();
        expiredSession.setToken("expired-token");
        expiredSession.setExpirationDate(Instant.now().minus(Duration.ofHours(1)));
        expiredSession.setActive(true);
        expiredSession.setUser(regularUser);

        inactiveSession = new SessionEntity();
        inactiveSession.setToken("inactive-token");
        inactiveSession.setExpirationDate(Instant.now().plus(Duration.ofHours(1)));
        inactiveSession.setActive(false);
        inactiveSession.setUser(regularUser);

        // Inicializa o criador do projeto
        creator = new UserEntity();
        creator.setId(2L);
        creator.setEmail("creator@example.com");
        ProjectEntity project = new ProjectEntity();
        project.setId(1L);
        project.setStatus(ProjectStatus.READY);
        project.setLab(labEntity);
        ProjectEntity project3 = new ProjectEntity();
        project3 = new ProjectEntity();
        project3.setId(3L);
        project.setCreator(creator);

        // Inicializa o usuário do projeto
        projectUser = new ProjectUserEntity();
        projectUser.setUser(user);
        projectUser.setProject(project);
        projectUser.setActive(true);
        projectUser.setRole(UserType.NORMAL);

        // Inicializa listas vazias para tarefas e executores de tarefas
        taskExecutors = Collections.emptyList();
        tasks = Collections.emptyList();


        paginatedResponse = new PaginatedResponse<>();
        paginatedResponse.setTotalPages(1);
        paginatedResponse.setResults(Collections.singletonList(project));

        projectManager = new ProjectUserEntity();
        projectManager.setRole(UserType.MANAGER);
        projectManager.setActive(true);
        projectManager.setUser(validSession.getUser());
        projectManager.setProject(projectEntity);
    }



    @Test
    public void testLeaveProject_InvalidToken() {
        Long projectId = 1L;

        // Configura o comportamento do mock para um token inválido
        when(sessionDao.findSessionByToken("invalid-token")).thenReturn(null);

        // Verifica se a exceção apropriada é lançada
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectBean.leaveProject("invalid-token", projectId);
        });

        // Verifica a mensagem da exceção
        assertEquals("Invalid token or project", exception.getMessage());
        verify(projectUserDao, never()).merge(any());
    }

    @Test
    public void testLeaveProject_InvalidProject() {
        Long projectId = 1L;

        // Configura o comportamento do mock para um projeto inválido
        when(sessionDao.findSessionByToken("valid-token")).thenReturn(validSession);
        when(projectDao.findProjectById(projectId)).thenReturn(null);

        // Verifica se a exceção apropriada é lançada
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectBean.leaveProject("valid-token", projectId);
        });

        // Verifica a mensagem da exceção
        assertEquals("Invalid token or project", exception.getMessage());
        verify(projectUserDao, never()).merge(any());
    }

    @Test
    public void testLeaveProject_UserNotParticipant() {
        Long projectId = 1L;

        // Configura o comportamento do mock para um usuário que não é participante
        when(sessionDao.findSessionByToken("valid-token")).thenReturn(validSession);
        when(projectDao.findProjectById(projectId)).thenReturn(projectEntity);
        when(projectUserDao.findProjectUserByProjectIdAndUserId(projectId, validSession.getUser().getId())).thenReturn(null);

        // Verifica se a exceção apropriada é lançada
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectBean.leaveProject("valid-token", projectId);
        });

        // Verifica a mensagem da exceção
        assertEquals("User is not a participant in the project", exception.getMessage());
        verify(projectUserDao, never()).merge(any());
    }

    @Test
    public void testLeaveProject_UserWithTasks() {
        Long projectId = 3L;

        // Configura o comportamento do mock para o usuário ter tarefas atribuídas
        TaskEntity task = new TaskEntity();
        tasks = Collections.singletonList(task);
        when(sessionDao.findSessionByToken("valid-token")).thenReturn(validSession);
        when(projectDao.findProjectById(projectId)).thenReturn(projectEntity);
        when(projectUserDao.findProjectUserByProjectIdAndUserId(projectId, validSession.getUser().getId())).thenReturn(projectUser);
        when(taskExecutorDao.findTaskExecutorByProjectIdAndUserId(projectId, validSession.getUser().getId())).thenReturn(taskExecutors);
        when(taskDao.findTasksByProjectIdAndUserId(projectId, validSession.getUser().getId())).thenReturn(tasks);

        // Verifica se a exceção apropriada é lançada
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectBean.leaveProject("valid-token", projectId);
        });

        // Verifica a mensagem da exceção
        assertEquals("User is responsible for tasks in the project, cannot leave the project with tasks assigned", exception.getMessage());
        verify(projectUserDao, never()).merge(any());
    }





    @Test
    public void testLeaveProject_Successful() {
        Long projectId = 3L;

        when(sessionDao.findSessionByToken("valid-token")).thenReturn(validSession);
        when(projectDao.findProjectById(projectId)).thenReturn(projectEntity);
        when(projectUserDao.findProjectUserByProjectIdAndUserId(projectId, validSession.getUser().getId())).thenReturn(projectUser);
        when(taskExecutorDao.findTaskExecutorByProjectIdAndUserId(projectId, validSession.getUser().getId())).thenReturn(taskExecutors);
        when(taskDao.findTasksByProjectIdAndUserId(projectId, validSession.getUser().getId())).thenReturn(tasks);
//        when(projectUserDao.findProjectUserByProjectIdAndUserId(projectId, user.getId())).thenReturn(projectUser);
//        when(taskExecutorDao.findTaskExecutorByProjectIdAndUserId(projectId, user.getId())).thenReturn(taskExecutors);
//        when(taskDao.findTasksByProjectIdAndUserId(projectId, user.getId())).thenReturn(tasks);

        projectBean.leaveProject("valid-token", projectId);

        verify(projectUserDao).merge(projectUser);
        assertFalse(projectUser.isActive());
        assertEquals(UserType.KICKED, projectUser.getRole());
    }
    // ------------------------------ ------------------------------ ------------------------------

    @Test
    public void testGetReadyProjects_InvalidToken() {
        String token = "invalid-token";
        Integer pageNumber = 1;
        Integer pageSize = 10;

        when(sessionDao.findSessionByToken(token)).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectBean.getReadyProjects(token, pageNumber, pageSize);
        });

        assertEquals("Invalid token", exception.getMessage());
        verify(projectDao, never()).findProjects(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    public void testGetReadyProjects_UserNotAuthorized() {
        String token = "user-token";
        Integer pageNumber = 1;
        Integer pageSize = 10;

        when(sessionDao.findSessionByToken(token)).thenReturn(nonAdminSession);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectBean.getReadyProjects(token, pageNumber, pageSize);
        });

        assertEquals("User is not authorized to access this resource", exception.getMessage());
        verify(projectDao, never()).findProjects(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }
//
//    @Test
//    @Disabled
//    public void testGetReadyProjects_SuccessfulResponse() {
//        String token = "admin-token";
//        Integer pageNumber = 1;
//        Integer pageSize = 10;
//
//        when(sessionDao.findSessionByToken(token)).thenReturn(adminSession);
//        when(projectDao.findProjects(
//                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), eq(pageNumber), eq(pageSize), any(), any()
//        )).thenReturn(paginatedResponse);
//
//        PaginatedResponse<Object> response = projectBean.getReadyProjects(token, pageNumber, pageSize);
//
//        assertNotNull(response);
//        assertEquals(1, response.getTotalPages());
//        assertEquals(1, response.getResults().size());
//        assertTrue(response.getResults().get(0) instanceof ProjectReadyDto);
//
//        ProjectReadyDto dto = (ProjectReadyDto) response.getResults().get(0);
//        assertEquals(1L, dto.getId());
//        // Add more assertions based on the fields in ProjectReadyDto
//        assertEquals("Lab Location", dto.getLab());
//    }

    @Test
    public void testGetReadyProjects_ValidTokenAdminUser() {
        String token = "admin-token";
        Integer pageNumber = 1;
        Integer pageSize = 10;

        when(sessionDao.findSessionByToken(token)).thenReturn(adminSession);
        when(projectDao.findProjects(
                any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), eq(pageNumber), eq(pageSize), any(), any()
        )).thenReturn(paginatedResponse);

        PaginatedResponse<Object> response = projectBean.getReadyProjects(token, pageNumber, pageSize);

        verify(sessionDao, times(1)).findSessionByToken(token);
        verify(projectDao, times(1)).findProjects(
                any(), eq(List.of(ProjectStatus.READY)), any(), any(), any(), any(), any(), any(), any(), any(), eq(pageNumber), eq(pageSize), any(), any()
        );

        assertNotNull(response);
        assertEquals(1, response.getTotalPages());
        assertEquals(1, response.getResults().size());
    }



    @Test
//    @Disabled
    public void testUpdateTimeout_SuccessfulTimeoutUpdate() {
        String token = "admin-token";
        int newTimeout = 45;

        when(sessionDao.findSessionByToken(token)).thenReturn(adminSession);
        when(appConfigDao.findLastConfig()).thenReturn(appConfig);

        projectBean.updateTimeout(token, newTimeout);

        ArgumentCaptor<AppConfigEntity> captor = ArgumentCaptor.forClass(AppConfigEntity.class);
        verify(appConfigDao, times(1)).merge(captor.capture());

        AppConfigEntity capturedConfig = captor.getValue();

        assertNotNull(capturedConfig);
        assertEquals(newTimeout, capturedConfig.getTimeOut());
        assertEquals(adminUser.getId(), capturedConfig.getUser().getId());
//        assertEquals(adminUser.getEmail(), capturedConfig.getUser().getEmail());
        assertEquals(adminUser.getRole(), capturedConfig.getUser().getRole());
        assertEquals(appConfig.getMaxUsers(), capturedConfig.getMaxUsers());
        assertEquals(appConfig.getTimeOutAdmin(), capturedConfig.getTimeOutAdmin());
    }


    // ------------------------------ ------------------------------ ------------------------------

    @Test
    public void testValidateUserToken_TokenNotFound() {
        when(sessionDao.findSessionByToken("invalid-token")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                sessionBean.validateUserToken("invalid-token")
        );

        assertEquals("Token not found", exception.getMessage());
    }

    @Test
    public void testValidateUserToken_TokenExpired() {
        when(sessionDao.findSessionByToken("expired-token")).thenReturn(expiredSession);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                sessionBean.validateUserToken("expired-token")
        );

        assertEquals("Token expired", exception.getMessage());
    }

    @Test
    public void testValidateUserToken_TokenNotActive() {
        when(sessionDao.findSessionByToken("inactive-token")).thenReturn(inactiveSession);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                sessionBean.validateUserToken("inactive-token")
        );

        assertEquals("Token not active", exception.getMessage());
    }
    @Test
    public void testUpdateTimeout_ValidTokenAdminUser() {
        String token = "admin-token";
        int newTimeout = 45;

        when(sessionDao.findSessionByToken(token)).thenReturn(adminSession);
        when(appConfigDao.findLastConfig()).thenReturn(appConfig);

        projectBean.updateTimeout(token, newTimeout);

        verify(appConfigDao, times(1)).merge(any(AppConfigEntity.class));
    }

    @Test
    public void testUpdateTimeout_InvalidToken() {
        String token = "invalid-token";
        int newTimeout = 45;

        when(sessionDao.findSessionByToken(token)).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectBean.updateTimeout(token, newTimeout);
        });

        assertEquals("Invalid token", exception.getMessage());
        verify(appConfigDao, never()).merge(any(AppConfigEntity.class));
    }

    @Test
    public void testUpdateTimeout_UserNotAuthorized() {
        String token = "user-token";
        int newTimeout = 45;

        when(sessionDao.findSessionByToken(token)).thenReturn(nonAdminSession);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectBean.updateTimeout(token, newTimeout);
        });

        assertEquals("User is not authorized to access this resource", exception.getMessage());
        verify(appConfigDao, never()).merge(any(AppConfigEntity.class));
    }

    @Test
    public void testValidateUserToken_AdminUser() {
        activeSession.setUser(adminUser); // Define usuário admin na sessão ativa
        when(sessionDao.findSessionByToken("active-token")).thenReturn(activeSession); // Simula retorno do DAO

        // Simula o retorno válido do AppConfigDao
        when(appConfigDao.findLastConfig()).thenReturn(appConfig);

        UserEntity user = sessionBean.validateUserToken("active-token"); // Executa método de validação

        assertEquals(adminUser, user); // Verifica se o usuário retornado é o admin

        // Verifica se a expiração do token está dentro do intervalo esperado
        Instant expectedMinExpiration = Instant.now().plus(Duration.ofMinutes(29));
        Instant expectedMaxExpiration = Instant.now().plus(Duration.ofMinutes(31));
        Instant actualExpiration = user.getTokenExpiration();

        assertTrue(actualExpiration.isAfter(expectedMinExpiration) || actualExpiration.equals(expectedMinExpiration));
//        assertTrue(actualExpiration.isBefore(expectedMaxExpiration) || actualExpiration.equals(expectedMaxExpiration));
    }

    @Test
    public void testValidateUserToken_RegularUser() {
        when(sessionDao.findSessionByToken("active-token")).thenReturn(activeSession); // Simula retorno do DAO

        // Simula o retorno válido do AppConfigDao
        when(appConfigDao.findLastConfig()).thenReturn(appConfig);

        UserEntity user = sessionBean.validateUserToken("active-token"); // Executa método de validação

        assertEquals(regularUser, user); // Verifica se o usuário retornado é o regular

        // Verifica se a expiração do token está dentro do intervalo esperado
//        Instant expectedMinExpiration = Instant.now().plus(Duration.ofMinutes(59)); // Regular user expira após 59 minutos
        Instant expectedMaxExpiration = Instant.now().plus(Duration.ofMinutes(61)); // Regular user expira antes de 61 minutos
        Instant actualExpiration = user.getTokenExpiration();

//        assertTrue(actualExpiration.isAfter(expectedMinExpiration) || actualExpiration.equals(expectedMinExpiration));
        assertTrue(actualExpiration.isBefore(expectedMaxExpiration) || actualExpiration.equals(expectedMaxExpiration));
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

    @Test
    public void testInviteResponse_InvalidTokenOrProject() {
        // Configura o comportamento do mock para retornar null
        when(sessionDao.findSessionByToken("invalid-token")).thenReturn(null);
        when(projectDao.findProjectById(1L)).thenReturn(null);

        // Verifica se a exceção apropriada é lançada
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            projectBean.inviteResponse("invalid-token", 1L, new ResponseYesNoInviteDto());
        });
        assertEquals("Invalid token or project", thrown.getMessage());
    }

    @Test
    public void testInviteResponse_NoInviteData() {
        // Configura os mocks para retornar valores válidos
        when(sessionDao.findSessionByToken("valid-token")).thenReturn(validSession);
        when(projectDao.findProjectById(1L)).thenReturn(projectEntity);

        // Verifica se a exceção apropriada é lançada
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            projectBean.inviteResponse("valid-token", 1L, null);
        });
        assertEquals("User invite data is required", thrown.getMessage());
    }

//    @Test
//    @Disabled
//    public void testInviteResponse_NotProjectManager() {
//        // Configura o mock para o projectUserDao
//        when(sessionDao.findSessionByToken("valid-token")).thenReturn(validSession);
//        when(projectDao.findProjectById(1L)).thenReturn(projectEntity);
//        when(projectUserDao.findProjectUserByProjectIdAndUserId(1L, 1L)).thenReturn(null);
//
//        // Verifica se a exceção apropriada é lançada
//        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
//            projectBean.inviteResponse("valid-token", 1L, new ResponseYesNoInviteDto());
//        });
//        assertEquals("User is not a manager of the project", thrown.getMessage());
//    }

    @Test
    void testCreateProject_missingToken() {
        CreateProjectDto createProjectDto = new CreateProjectDto();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            projectBean.createProject(null, createProjectDto);
        });

        assertEquals("Token is required", exception.getMessage());
    }

    @Test
    void testCreateProject_invalidToken() {
        String token = "invalidToken";
        CreateProjectDto createProjectDto = new CreateProjectDto();

        when(sessionDao.findSessionByToken(token)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            projectBean.createProject(token, createProjectDto);
        });

        assertEquals("Invalid token", exception.getMessage());
    }




}
