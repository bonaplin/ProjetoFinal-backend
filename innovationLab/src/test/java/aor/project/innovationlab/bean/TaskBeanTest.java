package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.dto.response.IdNameDto;
import aor.project.innovationlab.dto.task.TaskCreateDto;
import aor.project.innovationlab.dto.task.TaskDto;
import aor.project.innovationlab.dto.task.TaskGanttDto;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.NotificationType;
import aor.project.innovationlab.enums.TaskStatus;
import aor.project.innovationlab.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TaskBeanTest {

    @Mock
    private TaskEntity task;

    @Mock
    private TaskEntity prerequisite;

    @Mock
    private SessionDao sessionDao;

    @Mock
    private ProjectDao projectDao;

    @Mock
    private ProjectUserDao projectUserDao;

    @Mock
    private TaskExecutorAdditionalDao taskExecutorAdditionalDao;

    @Mock
    private ExecutorDao executorDao;

    @Mock
    private TaskExecutorDao taskExecutorDao;

    @Mock
    private TaskPrerequisiteDao taskPrerequisiteDao;

    @InjectMocks
    @Spy
    private TaskBean taskBean;

    @Mock
    private LogBean logBean;

    @Mock
    private TaskDao taskDao;

    @Mock
    private UserDao userDao;

    @Mock
    private NotificationBean notificationBean;

    @InjectMocks
    private TaskService taskService;

    @Captor
    private ArgumentCaptor<TaskExecutorEntity> taskExecutorCaptor;

    @Captor
    private ArgumentCaptor<TaskEntity> taskCaptor;

    private TaskCreateDto validDto;
    private UserEntity user;
    private String token = "testToken";

    private final String validToken = "validToken";
    private final Long validProjectId = 1L;
    private final String validDtoType = "IdNameDto";
    private SessionEntity validSessionEntity;
    private ProjectEntity validProjectEntity;
    private UserEntity validUserEntity;
    private ProjectUserEntity validProjectUserEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        validDto = new TaskCreateDto();
        validDto.setTitle("Valid Task Title");
        validDto.setDescription("Valid Task Description");
        validDto.setProjectId(1L); // Assuming the project ID is a required field
        validDto.setInitialDate(LocalDate.now());
        validDto.setFinalDate(LocalDate.now().plusDays(5));
        user = new UserEntity(/* initialize user with valid data */);
        // Mock successful validations and required behaviors
        when(userDao.findUserById(anyLong())).thenReturn(new UserEntity());
        when(projectDao.findProjectById(anyLong())).thenReturn(new ProjectEntity());
        ProjectUserEntity pue = new ProjectUserEntity();
        pue.setActive(true);
        // Mock successful validations
        when(userDao.findUserById(anyLong())).thenReturn(new UserEntity());
        when(projectDao.findProjectById(anyLong())).thenReturn(new ProjectEntity());
        when(taskPrerequisiteDao.findByTaskId(anyLong())).thenReturn(Collections.emptyList());


    }
    @Test
    void testFindPrerequisiteTaskEntityByPrerequisite_foundAndActive() {
        TaskPrerequisiteEntity activePrerequisite = new TaskPrerequisiteEntity();
        activePrerequisite.setPrerequisite(prerequisite);
        activePrerequisite.setActive(true);

        Set<TaskPrerequisiteEntity> prerequisites = new HashSet<>();
        prerequisites.add(activePrerequisite);

        when(task.getPrerequisites()).thenReturn(prerequisites);


        TaskPrerequisiteEntity result = taskBean.findPrerequisiteTaskEntityByPrerequisite(task, prerequisite);

        assertNotNull(result);
        assertEquals(activePrerequisite, result);
    }

    @Test
    void testFindPrerequisiteTaskEntityByPrerequisite_notFound() {
        Set<TaskPrerequisiteEntity> prerequisites = new HashSet<>();

        when(task.getPrerequisites()).thenReturn(prerequisites);

        TaskPrerequisiteEntity result = taskBean.findPrerequisiteTaskEntityByPrerequisite(task, prerequisite);

        assertNull(result);
    }

    @Test
    void testFindPrerequisiteTaskEntityByPrerequisite_foundButInactive() {
        TaskPrerequisiteEntity inactivePrerequisite = new TaskPrerequisiteEntity();
        inactivePrerequisite.setPrerequisite(prerequisite);
        inactivePrerequisite.setActive(false);

        Set<TaskPrerequisiteEntity> prerequisites = new HashSet<>();
        prerequisites.add(inactivePrerequisite);

        when(task.getPrerequisites()).thenReturn(prerequisites);


        TaskPrerequisiteEntity result = taskBean.findPrerequisiteTaskEntityByPrerequisite(task, prerequisite);

        assertNull(result);
    }

    @Test
    void testFindPrerequisiteTaskEntityByPrerequisite_multiplePrerequisites() {
        TaskEntity anotherPrerequisite = mock(TaskEntity.class);

        TaskPrerequisiteEntity inactivePrerequisite = new TaskPrerequisiteEntity();
        inactivePrerequisite.setPrerequisite(prerequisite);
        inactivePrerequisite.setActive(false);

        TaskPrerequisiteEntity activePrerequisite = new TaskPrerequisiteEntity();
        activePrerequisite.setPrerequisite(prerequisite);
        activePrerequisite.setActive(true);

        TaskPrerequisiteEntity otherActivePrerequisite = new TaskPrerequisiteEntity();
        otherActivePrerequisite.setPrerequisite(anotherPrerequisite);
        otherActivePrerequisite.setActive(true);

        Set<TaskPrerequisiteEntity> prerequisites = new HashSet<>();
        prerequisites.add(inactivePrerequisite);
        prerequisites.add(activePrerequisite);
        prerequisites.add(otherActivePrerequisite);

        when(task.getPrerequisites()).thenReturn(prerequisites);


        TaskPrerequisiteEntity result = taskBean.findPrerequisiteTaskEntityByPrerequisite(task, prerequisite);

        assertNotNull(result);
        assertEquals(activePrerequisite, result);
    }

    @Test
    void testAddExecutorToTask_success() {
        long taskId = 1L;
        String executorEmail = "executor@example.com";

        TaskEntity task = new TaskEntity();
        task.setId(taskId);
        task.setTitle("Test Task");
        task.setExecutors(new HashSet<>());

        UserEntity executor = new UserEntity();
        executor.setEmail(executorEmail);

        ProjectEntity project = new ProjectEntity();
        project.setId(1L);
        task.setProject(project);

        when(taskDao.findTaskById(taskId)).thenReturn(task);
        when(userDao.findUserByEmail(executorEmail)).thenReturn(executor);

        taskBean.addExecutorToTask(taskId, executorEmail);

        verify(taskExecutorDao, times(1)).merge(taskExecutorCaptor.capture());
        TaskExecutorEntity taskExecutor = taskExecutorCaptor.getValue();
        assertEquals(task, taskExecutor.getTask());
        assertEquals(executor, taskExecutor.getExecutor());
        assertTrue(taskExecutor.isActive());

        verify(taskDao, times(1)).merge(taskCaptor.capture());
        TaskEntity mergedTask = taskCaptor.getValue();
        assertTrue(mergedTask.getExecutors().contains(taskExecutor));

        verify(notificationBean, times(1)).sendNotification(eq("admin@admin"), eq(executorEmail), contains("You have been added as an executor to the task"), eq(NotificationType.TASK_EXECUTOR_CHANGED), eq(project.getId()));
    }

    @Test
    void testAddExecutorToTask_taskNotFound() {
        long taskId = 1L;
        String executorEmail = "executor@example.com";

        when(taskDao.findTaskById(taskId)).thenReturn(null);

        taskBean.addExecutorToTask(taskId, executorEmail);

        verify(taskExecutorDao, never()).merge(any(TaskExecutorEntity.class));
        verify(taskDao, never()).merge(any(TaskEntity.class));
        verify(notificationBean, never()).sendNotification(anyString(), anyString(), anyString(), any(NotificationType.class), anyLong());
    }

    @Test
    void testAddExecutorToTask_executorNotFound() {
        long taskId = 1L;
        String executorEmail = "executor@example.com";

        TaskEntity task = new TaskEntity();
        task.setId(taskId);
        task.setTitle("Test Task");

        ProjectEntity project = new ProjectEntity();
        project.setId(1L);
        task.setProject(project);

        when(taskDao.findTaskById(taskId)).thenReturn(task);
        when(userDao.findUserByEmail(executorEmail)).thenReturn(null);

        taskBean.addExecutorToTask(taskId, executorEmail);

        verify(taskExecutorDao, never()).merge(any(TaskExecutorEntity.class));
        verify(taskDao, never()).merge(any(TaskEntity.class));
//        verify(notificationBean, never()).sendNotification(anyString(), anyString(), anyString(), any(NotificationType.class), anyLong());
    }

    @Test
    void testIdNameDto() {
        // Arrange
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(1L);
        taskEntity.setTitle("Test Task");

        // Act
        IdNameDto result = taskBean.idNameDto(taskEntity);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Task", result.getName());
    }
    @Test
    void testIdNameDto1() {
        // Arrange
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(2L);
        taskEntity.setTitle("Test Task2");

        // Act
        IdNameDto result = taskBean.idNameDto(taskEntity);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Test Task2", result.getName());
    }

    @Test
    void testGetTasks_invalidToken() {
        when(sessionDao.findSessionByToken(anyString())).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            taskBean.getTasks("invalidToken", 1L, "TaskDto");
        });

        assertEquals("Invalid token", exception.getMessage());
    }

    @Test
    void testGetTasks_projectNotFound() {
        SessionEntity session = new SessionEntity();
        UserEntity user = new UserEntity();
        session.setUser(user);

        when(sessionDao.findSessionByToken(anyString())).thenReturn(session);
        when(projectDao.findProjectById(anyLong())).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            taskBean.getTasks("validToken", 1L, "TaskDto");
        });

        assertEquals("Project not found", exception.getMessage());
    }

    @Test
    void testGetTasks_userNotParticipant() {
        SessionEntity session = new SessionEntity();
        UserEntity user = new UserEntity();
        user.setEmail("user@example.com");
        session.setUser(user);
        ProjectEntity project = new ProjectEntity();

        when(sessionDao.findSessionByToken(anyString())).thenReturn(session);
        when(projectDao.findProjectById(anyLong())).thenReturn(project);
        when(projectUserDao.findProjectUserByProjectIdAndUserId(anyLong(), anyLong())).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            taskBean.getTasks("validToken", 1L, "TaskDto");
        });

        assertEquals("User is not a participant in the project", exception.getMessage());
    }

    @Test
    void testGetTasks_noTasksFound() {
        SessionEntity session = new SessionEntity();
        UserEntity user = new UserEntity();
        user.setEmail("user@example.com");
        session.setUser(user);
        ProjectEntity project = new ProjectEntity();
        ProjectUserEntity projectUser = new ProjectUserEntity();
        projectUser.setActive(true);

        when(sessionDao.findSessionByToken(anyString())).thenReturn(session);
        when(projectDao.findProjectById(anyLong())).thenReturn(project);
        when(projectUserDao.findProjectUserByProjectIdAndUserId(anyLong(), anyLong())).thenReturn(projectUser);
        when(taskDao.findTasksByProjectId(anyLong())).thenReturn(new ArrayList<>());

        List<Object> result = taskBean.getTasks("validToken", 1L, "TaskDto");

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTasks_invalidDtoType() {
        SessionEntity session = new SessionEntity();
        UserEntity user = new UserEntity();
        user.setEmail("user@example.com");
        session.setUser(user);
        ProjectEntity project = new ProjectEntity();
        ProjectUserEntity projectUser = new ProjectUserEntity();
        projectUser.setActive(true);

        when(sessionDao.findSessionByToken(anyString())).thenReturn(session);
        when(projectDao.findProjectById(anyLong())).thenReturn(project);
        when(projectUserDao.findProjectUserByProjectIdAndUserId(anyLong(), anyLong())).thenReturn(projectUser);
        when(taskDao.findTasksByProjectId(anyLong())).thenReturn(List.of(new TaskEntity()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            taskBean.getTasks("validToken", 1L, "InvalidDtoType");
        });

        assertEquals("Invalid dto type", exception.getMessage());
    }

    @Test
    void testGetDependentTasks_taskFoundWithNoDependencies() {
        Long taskId = 1L;
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setPrerequisiteForTasks(Collections.emptySet());

        when(taskDao.findTaskById(taskId)).thenReturn(taskEntity);

        Set<TaskEntity> result = taskBean.getDependentTasks(taskId);

        assertEquals(Collections.emptySet(), result);
    }

    @Test
    void testUpdatePresentationTask_presentationTaskNotFound() {
        Long projectId = 1L;
        when(taskDao.findTaskByProjectIdAndStatus(projectId, TaskStatus.PRESENTATION)).thenReturn(null);

        taskBean.updatePresentationTask(projectId);

        verify(taskDao, never()).merge(any(TaskEntity.class));
        verify(projectDao, never()).merge(any(ProjectEntity.class));
    }

    @Test
    void testGetLatestFinalDate_noTasks() {
        Long projectId = 1L;
        Long excludeTaskId = 2L;

        when(taskDao.findTasksByProjectId(projectId)).thenReturn(new ArrayList<>());

        LocalDate result = taskBean.getLatestFinalDate(projectId, excludeTaskId);

        assertEquals(LocalDate.MIN, result);
    }

    @Test
    void testGetLatestFinalDate_withTasksExcludingOne() {
        Long projectId = 1L;
        Long excludeTaskId = 2L;
        LocalDate date1 = LocalDate.of(2023, 7, 1);
        LocalDate date2 = LocalDate.of(2023, 7, 10);
        LocalDate date3 = LocalDate.of(2023, 7, 5);

        TaskEntity task1 = new TaskEntity();
        task1.setId(1L);
        task1.setFinalDate(date1);

        TaskEntity task2 = new TaskEntity();
        task2.setId(excludeTaskId);
        task2.setFinalDate(date2);

        TaskEntity task3 = new TaskEntity();
        task3.setId(3L);
        task3.setFinalDate(date3);

        List<TaskEntity> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);

        when(taskDao.findTasksByProjectId(projectId)).thenReturn(tasks);
//        when(taskBean.updateLatestFinalDate(task1, LocalDate.MIN, new HashSet<>())).thenReturn(date1);
//        when(taskBean.updateLatestFinalDate(task3, LocalDate.MIN, new HashSet<>())).thenReturn(date3);

        // Mocking updateLatestFinalDate calls
        doAnswer(invocation -> {
            TaskEntity task = invocation.getArgument(0);
            LocalDate currentLatestFinalDate = invocation.getArgument(1);
            if (task.getFinalDate().isAfter(currentLatestFinalDate)) {
                return task.getFinalDate();
            }
            return currentLatestFinalDate;
        }).when(taskBean).updateLatestFinalDate(any(TaskEntity.class), any(LocalDate.class), any(HashSet.class));

        LocalDate result = taskBean.getLatestFinalDate(projectId, excludeTaskId);

        assertEquals(date3, result);
    }

    @Test
    void testGetLatestFinalDate_noTasksFound() {
        Long projectId = 1L;
        Long excludeTaskId = 2L;

        when(taskDao.findTasksByProjectId(projectId)).thenReturn(new ArrayList<>());

        LocalDate result = taskBean.getLatestFinalDate(projectId, excludeTaskId);

        assertEquals(LocalDate.MIN, result);
    }

    @Test
    void testGetLatestFinalDate_withLatestDate() {
        Long projectId = 1L;
        Long excludeTaskId = 2L;
        LocalDate date1 = LocalDate.of(2023, 7, 1);
        LocalDate date2 = LocalDate.of(2023, 7, 10);
        LocalDate date3 = LocalDate.of(2023, 7, 15);

        TaskEntity task1 = new TaskEntity();
        task1.setId(1L);
        task1.setFinalDate(date1);

        TaskEntity task2 = new TaskEntity();
        task2.setId(excludeTaskId);
        task2.setFinalDate(date2);

        TaskEntity task3 = new TaskEntity();
        task3.setId(3L);
        task3.setFinalDate(date3);

        List<TaskEntity> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);

        when(taskDao.findTasksByProjectId(projectId)).thenReturn(tasks);

        doAnswer(invocation -> {
            TaskEntity task = invocation.getArgument(0);
            LocalDate currentLatestFinalDate = invocation.getArgument(1);
            if (task.getFinalDate().isAfter(currentLatestFinalDate)) {
                return task.getFinalDate();
            }
            return currentLatestFinalDate;
        }).when(taskBean).updateLatestFinalDate(any(TaskEntity.class), any(LocalDate.class), any(HashSet.class));

        LocalDate result = taskBean.getLatestFinalDate(projectId, excludeTaskId);

        assertEquals(date3, result);
    }

    @Test
    void testGetLatestFinalDate_multipleTasksWithSameDates() {
        Long projectId = 1L;
        Long excludeTaskId = 2L;
        LocalDate date = LocalDate.of(2023, 7, 5);

        TaskEntity task1 = new TaskEntity();
        task1.setId(1L);
        task1.setFinalDate(date);

        TaskEntity task2 = new TaskEntity();
        task2.setId(excludeTaskId);
        task2.setFinalDate(date);

        TaskEntity task3 = new TaskEntity();
        task3.setId(3L);
        task3.setFinalDate(date);

        List<TaskEntity> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);

        when(taskDao.findTasksByProjectId(projectId)).thenReturn(tasks);

        doAnswer(invocation -> {
            TaskEntity task = invocation.getArgument(0);
            LocalDate currentLatestFinalDate = invocation.getArgument(1);
            if (task.getFinalDate().isAfter(currentLatestFinalDate)) {
                return task.getFinalDate();
            }
            return currentLatestFinalDate;
        }).when(taskBean).updateLatestFinalDate(any(TaskEntity.class), any(LocalDate.class), any(HashSet.class));

        LocalDate result = taskBean.getLatestFinalDate(projectId, excludeTaskId);

        assertEquals(date, result);
    }

    @Test
    void testUpdateLatestFinalDate_noDependencies() {
        TaskEntity task = new TaskEntity();
        task.setId(1L);
        task.setInitialDate(LocalDate.of(2023, 7, 1));
        task.setDuration(Period.ofDays(5));

        Set<Long> visitedTaskIds = new HashSet<>();
        LocalDate latestFinalDate = taskBean.updateLatestFinalDate(task, LocalDate.MIN, visitedTaskIds);

        assertEquals(LocalDate.of(2023, 7, 6), latestFinalDate);
    }

    @Test
    void testUpdateLatestFinalDate_withDependencies() {
        TaskEntity task1 = new TaskEntity();
        task1.setId(1L);
        task1.setInitialDate(LocalDate.of(2023, 7, 1));
        task1.setDuration(Period.ofDays(5));

        TaskEntity task2 = new TaskEntity();
        task2.setId(2L);
        task2.setInitialDate(LocalDate.of(2023, 7, 7));
        task2.setDuration(Period.ofDays(3));

        // Stub the method getDependentTasks using doReturn
        doReturn(Set.of(task2)).when(taskBean).getDependentTasks(1L);
        doReturn(Collections.emptySet()).when(taskBean).getDependentTasks(2L);

        Set<Long> visitedTaskIds = new HashSet<>();
        LocalDate latestFinalDate = taskBean.updateLatestFinalDate(task1, LocalDate.MIN, visitedTaskIds);

        assertEquals(LocalDate.of(2023, 7, 10), latestFinalDate);
    }

    @Test
    void testUpdateLatestFinalDate_withMultipleDependencies() {
        TaskEntity task1 = new TaskEntity();
        task1.setId(1L);
        task1.setInitialDate(LocalDate.of(2023, 7, 1));
        task1.setDuration(Period.ofDays(5));

        TaskEntity task2 = new TaskEntity();
        task2.setId(2L);
        task2.setInitialDate(LocalDate.of(2023, 7, 7));
        task2.setDuration(Period.ofDays(3));

        TaskEntity task3 = new TaskEntity();
        task3.setId(3L);
        task3.setInitialDate(LocalDate.of(2023, 7, 11));
        task3.setDuration(Period.ofDays(2));

        // Stub the method getDependentTasks using doReturn
        doReturn(Set.of(task2)).when(taskBean).getDependentTasks(1L);
        doReturn(Set.of(task3)).when(taskBean).getDependentTasks(2L);
        doReturn(Collections.emptySet()).when(taskBean).getDependentTasks(3L);

        Set<Long> visitedTaskIds = new HashSet<>();
        LocalDate latestFinalDate = taskBean.updateLatestFinalDate(task1, LocalDate.MIN, visitedTaskIds);

        assertEquals(LocalDate.of(2023, 7, 13), latestFinalDate);
    }

    @Test
    void testUpdateLatestFinalDate_withVaryingDependencyDates() {
        TaskEntity task1 = new TaskEntity();
        task1.setId(1L);
        task1.setInitialDate(LocalDate.of(2023, 7, 1));
        task1.setDuration(Period.ofDays(5));

        TaskEntity task2 = new TaskEntity();
        task2.setId(2L);
        task2.setInitialDate(LocalDate.of(2023, 7, 7));
        task2.setDuration(Period.ofDays(3));

        TaskEntity task3 = new TaskEntity();
        task3.setId(3L);
        task3.setInitialDate(LocalDate.of(2023, 7, 10));
        task3.setDuration(Period.ofDays(2));

        TaskEntity task4 = new TaskEntity();
        task4.setId(4L);
        task4.setInitialDate(LocalDate.of(2023, 7, 15));
        task4.setDuration(Period.ofDays(1));

        // Stub the method getDependentTasks using doReturn
        doReturn(Set.of(task2)).when(taskBean).getDependentTasks(1L);
        doReturn(Set.of(task3)).when(taskBean).getDependentTasks(2L);
        doReturn(Set.of(task4)).when(taskBean).getDependentTasks(3L);
        doReturn(Collections.emptySet()).when(taskBean).getDependentTasks(4L);

        Set<Long> visitedTaskIds = new HashSet<>();
        LocalDate latestFinalDate = taskBean.updateLatestFinalDate(task1, LocalDate.MIN, visitedTaskIds);

        assertEquals(LocalDate.of(2023, 7, 16), latestFinalDate);
    }

    @Test
    void testNoCycle() {
        TaskEntity taskA = new TaskEntity();
        taskA.setId(1L);
        TaskEntity taskB = new TaskEntity();
        taskB.setId(2L);
        taskBean.addPrerequisite(taskB.getId(), taskA.getId());

        assertFalse(taskBean.createsDependencyCycle(taskA, taskB));
    }

    @Test
    void testDirectCycle() {
        TaskEntity taskA = new TaskEntity();
        taskA.setId(1L);
        taskBean.addPrerequisite(taskA.getId(), taskA.getId());

        assertTrue(taskBean.createsDependencyCycle(taskA, taskA));
    }

    @Test
    void testIndirectCycle() {
        TaskEntity taskA = new TaskEntity();
        taskA.setId(1L);
        TaskEntity taskB = new TaskEntity();
        taskB.setId(2L);
        TaskEntity taskC = new TaskEntity();
        taskC.setId(3L);
        taskBean.addPrerequisite(taskB.getId(), taskA.getId());
        taskBean.addPrerequisite(taskC.getId(), taskB.getId());
        taskBean.addPrerequisite(taskA.getId(), taskC.getId());

        assertFalse(taskBean.createsDependencyCycle(taskA, taskB));
    }

    @Test
    void testMultipleBranchesNoCycle() {
        TaskEntity taskA = new TaskEntity();
        taskA.setId(1L);
        TaskEntity taskB = new TaskEntity();
        taskB.setId(2L);
        TaskEntity taskC = new TaskEntity();
        taskC.setId(3L);
        TaskEntity taskD = new TaskEntity();
        taskD.setId(4L);
        taskBean.addPrerequisite(taskB.getId(), taskA.getId());
        taskBean.addPrerequisite(taskC.getId(), taskA.getId());

        assertFalse(taskBean.createsDependencyCycle(taskA, taskB));
    }

    @Test
    void testMultipleBranchesWithCycle() {
        TaskEntity taskA = new TaskEntity();
        taskA.setId(1L);
        TaskEntity taskB = new TaskEntity();
        taskB.setId(2L);
        TaskEntity taskC = new TaskEntity();
        taskC.setId(3L);
        TaskEntity taskD = new TaskEntity();
        taskD.setId(4L);
        taskBean.addPrerequisite(taskB.getId(), taskA.getId());
        taskBean.addPrerequisite(taskC.getId(), taskA.getId());
        taskBean.addPrerequisite(taskA.getId(), taskD.getId());

        assertFalse(taskBean.createsDependencyCycle(taskA, taskB));
    }

    @Test
    void testFindMaxDependentFinalDateWithNoDependentTaskIds() {
        assertNull(taskBean.findMaxDependentFinalDate(null));
        assertNull(taskBean.findMaxDependentFinalDate(Collections.emptyList()));
    }

    @Test
    void testFindMaxDependentFinalDateWithAllInactiveDependentTasks() {
        TaskEntity task1 = new TaskEntity();
        task1.setActive(false);
        when(taskDao.findTaskById(1L)).thenReturn(task1);

        TaskEntity task2 = new TaskEntity();
        task2.setActive(false);
        when(taskDao.findTaskById(2L)).thenReturn(task2);

        assertNull(taskBean.findMaxDependentFinalDate(Arrays.asList(1L, 2L)));
    }

    @Test
    void testFindMaxDependentFinalDateWithActiveDependentTasks() {
        TaskEntity task1 = new TaskEntity();
        task1.setActive(true);
        task1.setFinalDate(LocalDate.of(2023, 1, 1));
        when(taskDao.findTaskById(1L)).thenReturn(task1);

        TaskEntity task2 = new TaskEntity();
        task2.setActive(true);
        task2.setFinalDate(LocalDate.of(2023, 1, 10));
        when(taskDao.findTaskById(2L)).thenReturn(task2);

        assertEquals(LocalDate.of(2023, 1, 10), taskBean.findMaxDependentFinalDate(Arrays.asList(1L, 2L)));
    }

    @Test
    void testFindMaxDependentFinalDateWithMixedActiveAndInactiveDependentTasks() {
        TaskEntity task1 = new TaskEntity();
        task1.setActive(false);
        when(taskDao.findTaskById(1L)).thenReturn(task1);

        TaskEntity task2 = new TaskEntity();
        task2.setActive(true);
        task2.setFinalDate(LocalDate.of(2023, 1, 5));
        when(taskDao.findTaskById(2L)).thenReturn(task2);

        assertEquals(LocalDate.of(2023, 1, 5), taskBean.findMaxDependentFinalDate(Arrays.asList(1L, 2L)));
    }

    @Test
    void testValidateTaskCreateDtoWithNullDto() {
        assertThrows(IllegalArgumentException.class, () -> taskBean.validateTaskCreateDto(null, user, token));
    }

    @Test
    void testDeleteTaskWithInvalidToken() {
        String invalidToken = "invalidToken";
        Long taskId = 1L;

        when(sessionDao.findSessionByToken(invalidToken)).thenReturn(null);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> taskBean.deleteTask(invalidToken, taskId));

        assertEquals("Invalid token or task", thrown.getMessage());
    }

    @Test
    void testDeleteTaskWithInvalidTaskId() {
        String token = "validToken";
        Long invalidTaskId = 999L;

        when(sessionDao.findSessionByToken(token)).thenReturn(new SessionEntity());
        when(taskDao.findTaskById(invalidTaskId)).thenReturn(null);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> taskBean.deleteTask(token, invalidTaskId));

        assertEquals("Invalid token or task", thrown.getMessage());
    }

    @Test
    void testDeleteTaskSuccessfully() {
        String token = "validToken";
        Long taskId = 1L;
        TaskEntity task = new TaskEntity();
        task.setId(taskId);
        task.setActive(true);

        // Ensure the task has a non-null project associated with it
        ProjectEntity projectEntity = new ProjectEntity();
        projectEntity.setId(1L); // Ensure this matches the project ID expected in the test
        task.setProject(projectEntity);

        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setUser(new UserEntity());

        when(sessionDao.findSessionByToken(token)).thenReturn(sessionEntity);
        when(taskDao.findTaskById(taskId)).thenReturn(task);

        taskBean.deleteTask(token, taskId);

        verify(taskDao, times(1)).merge(task);
        assertFalse(task.isActive());
    }

    @Test
    void testFindPrerequisiteTaskEntityByPrerequisite_FoundAndActive() {
        // Arrange
        TaskEntity task = mock(TaskEntity.class);
        TaskEntity prerequisite = mock(TaskEntity.class);
        TaskPrerequisiteEntity taskPrerequisiteEntity = mock(TaskPrerequisiteEntity.class);

        Set<TaskPrerequisiteEntity> prerequisites = new HashSet<>();
        prerequisites.add(taskPrerequisiteEntity);

        when(task.getPrerequisites()).thenReturn(prerequisites);
        when(taskPrerequisiteEntity.getPrerequisite()).thenReturn(prerequisite);
        when(taskPrerequisiteEntity.isActive()).thenReturn(true);

        // Act
        TaskPrerequisiteEntity result = taskBean.findPrerequisiteTaskEntityByPrerequisite(task, prerequisite);

        // Assert
        assertNotNull(result);
        assertEquals(taskPrerequisiteEntity, result);
    }

    @Test
    void testFindPrerequisiteTaskEntityByPrerequisite_MultipleActivePrerequisites() {
        TaskPrerequisiteEntity firstActivePrerequisite = mock(TaskPrerequisiteEntity.class);
        TaskPrerequisiteEntity secondActivePrerequisite = mock(TaskPrerequisiteEntity.class);
        Set<TaskPrerequisiteEntity> prerequisites = new HashSet<>(Arrays.asList(firstActivePrerequisite, secondActivePrerequisite));

        when(task.getPrerequisites()).thenReturn(prerequisites);
        when(firstActivePrerequisite.getPrerequisite()).thenReturn(prerequisite);
        when(firstActivePrerequisite.isActive()).thenReturn(true);
        when(secondActivePrerequisite.getPrerequisite()).thenReturn(prerequisite);
        when(secondActivePrerequisite.isActive()).thenReturn(true);

        TaskPrerequisiteEntity result = taskBean.findPrerequisiteTaskEntityByPrerequisite(task, prerequisite);

        // Assuming the method should return the first match it finds
        assertNotNull(result);
    }

    @Test
    void testFindPrerequisiteTaskEntityByPrerequisite_MultiplePrerequisitesSomeInactive() {
        TaskPrerequisiteEntity activePrerequisite = mock(TaskPrerequisiteEntity.class);
        TaskPrerequisiteEntity inactivePrerequisite = mock(TaskPrerequisiteEntity.class);
        Set<TaskPrerequisiteEntity> prerequisites = new HashSet<>(Arrays.asList(activePrerequisite, inactivePrerequisite));

        when(task.getPrerequisites()).thenReturn(prerequisites);
        when(activePrerequisite.getPrerequisite()).thenReturn(prerequisite);
        when(activePrerequisite.isActive()).thenReturn(true);
        when(inactivePrerequisite.getPrerequisite()).thenReturn(prerequisite);
        when(inactivePrerequisite.isActive()).thenReturn(false);

        TaskPrerequisiteEntity result = taskBean.findPrerequisiteTaskEntityByPrerequisite(task, prerequisite);

        assertNotNull(result);
        assertEquals(activePrerequisite, result);
    }

    @Test
    void testFindPrerequisiteTaskEntityByPrerequisite_NullPrerequisite() {
        TaskPrerequisiteEntity result = taskBean.findPrerequisiteTaskEntityByPrerequisite(task, null);

        assertNull(result);
    }

    @Test
    void getProjectTasks_InvalidTokenOrProject() {
        when(sessionDao.findSessionByToken(anyString())).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> taskBean.getProjectTasks("invalidToken", validProjectId, validDtoType));
        assertEquals("Invalid token or project", exception.getMessage());
    }

}
