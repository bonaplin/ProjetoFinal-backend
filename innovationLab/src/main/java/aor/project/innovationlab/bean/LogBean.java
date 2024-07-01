package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.dto.log.LogDto;
import aor.project.innovationlab.dto.response.PaginatedResponse;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.LogType;
import aor.project.innovationlab.enums.ProjectStatus;
import aor.project.innovationlab.enums.TaskStatus;
import aor.project.innovationlab.enums.UserType;
import aor.project.innovationlab.utils.Color;
import aor.project.innovationlab.utils.logs.LoggerUtil;
import com.mysql.cj.log.Log;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Stateless
public class LogBean {

    @EJB
    private LogDao logDao;

    @EJB
    private ProjectDao projectDao;

    @EJB
    private UserDao userDao;

    @EJB
    private TaskDao taskDao;

    @EJB
    private SessionDao sessionDao;

    @EJB
    private MessageDao messageDao;

    @EJB
    private ProjectUserDao projectUserDao;

    public LogBean() {
    }

    //TODO - Implementar métodos de log. Ex: changeTaskStatus, changeProjectStatus, etc.


    public LogEntity addNewUser(Long projectId, Long userId, Long affectedUserId, LogType type){
        LogEntity log = new LogEntity();
        ProjectEntity project = projectDao.findProjectById(projectId);
        UserEntity affectedUser = userDao.findUserById(affectedUserId);
        UserEntity user = userDao.findUserById(userId);

        if(project == null || affectedUser == null || user == null) {
            return null;
        }

        log.setType(type);
        log.setUser(user);
        log.setProject(project);

        log.setAffectedUser(affectedUser);
        logDao.persist(log);
        return log;
    }

    public LogEntity addNewTask(Long projectId, Long userId, Long taskId, LogType type){
        LogEntity log = new LogEntity();
        ProjectEntity project = projectDao.findProjectById(projectId);
        TaskEntity task = taskDao.findTaskById(taskId);
        UserEntity user = userDao.findUserById(userId);

        if(project == null || task == null || user == null) {
            return null;
        }

        log.setType(type);
        log.setUser(user);
        log.setProject(project);

        log.setTask(task);
        logDao.persist(log);
        return log;
    }

    public PaginatedResponse<Object> getProjectLogs(String token, Long id, Integer pageNumber, Integer pageSize) {
        String log = "Attempting to get logs for project with id: " + id;
        String msg = "";
        SessionEntity session = sessionDao.findSessionByToken(token);
        if (session == null) {
            log = "Failed to get logs for project with id: " + id;
            msg = "Invalid session token";
            LoggerUtil.logInfo(log, msg, null, token);
            throw new IllegalArgumentException("Invalid session token");
        }

        ProjectEntity project = projectDao.findProjectById(id);
        UserEntity user = session.getUser();

        if (project == null) {
            log = "Failed to get logs for project with id: " + id;
            msg = "Project not found";
            LoggerUtil.logInfo(log, msg, user.getEmail(), token);
            throw new IllegalArgumentException("Project not found");
        }

        ProjectUserEntity pue = projectDao.findProjectUserByProjectAndUserId(project.getId(), user.getId());
        if(pue == null) {
            log = "Failed to get logs for project with id: " + id;
            msg = "User not found in the project";
            LoggerUtil.logInfo(log, msg, user.getEmail(), token);
            throw new IllegalArgumentException("User not found in the project");
        }
        if(!pue.isActive()) {
            log = "Failed to get logs for project with id: " + id;
            msg = "User is not active in the project";
            LoggerUtil.logInfo(log, msg, user.getEmail(), token);
            throw new IllegalArgumentException("User is not active in the project");
        }
        if(pageNumber == null || pageNumber < 0) {
            pageNumber = 1;
        }
        if(pageSize == null || pageSize < 0) {
            pageSize = 30;
        }
        PaginatedResponse<LogEntity> logResponse = logDao.findLogsByProjectId(id, pageNumber, pageSize);

        List<LogEntity> logs = logResponse.getResults();

        PaginatedResponse<Object> response = new PaginatedResponse<>();
        response.setTotalPages(logResponse.getTotalPages());
        response.setResults(logs.stream().map(this::toDto).collect(Collectors.toList()));

        return response;
    }

    public LogDto toDto(LogEntity entity){
        LogDto dto = new LogDto();
        dto.setId(entity.getId());
        dto.setInstant(entity.getInstant());

        dto.setUserEmail(entity.getUser() != null ? entity.getUser().getEmail() : null);
        dto.setUserFirstName(entity.getUser() != null ? entity.getUser().getFirstname() : null);

        dto.setType(entity.getType() != null ? entity.getType().getValue() : null);

        dto.setProjectId(entity.getProject() != null ? entity.getProject().getId() : null);



        switch (entity.getType()) {
            case PROJECT_CHANGE:
            case PROJECT_STATE_CHANGE:
                dto.setNewProjectStatus(entity.getNewProjectStatus() != null ? entity.getNewProjectStatus().getValue() : null);
                dto.setOldProjectStatus(entity.getOldProjectStatus() != null ? entity.getOldProjectStatus().getValue() : null);
                break;
            case TASK_CREATE:
            case TASK_CHANGE:
            case TASK_DELETE:
            case TASK_COMPLETE:
            case TASK_STATE_CHANGE:
                dto.setTaskId(entity.getTask() != null ? entity.getTask().getId() : null);
                dto.setTaskName(entity.getTask() != null ? entity.getTask().getTitle() : null);
                dto.setNewTaskStatus(entity.getNewTaskStatus() != null ? entity.getNewTaskStatus().getValue() : null);
                dto.setOldTaskStatus(entity.getOldTaskStatus() != null ? entity.getOldTaskStatus().getValue() : null);
                System.out.println(entity.getNewTaskStatus());
                System.out.println(entity.getOldTaskStatus());
                break;
            case USER_JOIN:
            case USER_LEAVE:
            case USER_CHANGE:
            case USER_KICKED:
                dto.setAffectedUserEmail(entity.getAffectedUser() != null ? entity.getAffectedUser().getEmail() : null);
                dto.setAffectedUserFirstName(entity.getAffectedUser() != null ? entity.getAffectedUser().getFirstname() : null);
                dto.setNewUserType(entity.getNewUserType() != null ? entity.getNewUserType().getValue() : null);
                dto.setOldUserType(entity.getOldUserType() != null ? entity.getOldUserType().getValue() : null);
                break;
            default:
                break;
        }

        return dto;
    }

    public void createInitialData() {
            createOneOfEachLogType((long) 1);
    }

    public void createOneOfEachLogType(Long projectId) {
        ProjectEntity project = projectDao.findProjectById(projectId);

        if(project == null) {
            throw new IllegalArgumentException("Project not found");
        }

        List<TaskEntity> tasks = taskDao.findTasksByProjectId(projectId);
        List<UserEntity> users = projectUserDao.findUsersByProjectId(projectId);

        if(tasks.isEmpty() || users.isEmpty()) {
            return;
        }

        TaskEntity task = tasks.get(0);
        UserEntity user = users.get(0);

        for (LogType logType : LogType.values()) {
            LogEntity log = new LogEntity();
            log.setType(logType);
            log.setUser(user);
            log.setProject(project);

            switch (logType) {
                case PROJECT_CHANGE:
                    log.setTask(null);
                    log.setAffectedUser(null);
                    break;
                case PROJECT_STATE_CHANGE:
                    log.setTask(null);
                    log.setAffectedUser(null);
                    log.setNewProjectStatus(ProjectStatus.READY);
                    log.setOldProjectStatus(ProjectStatus.PLANNING);
                    break;
                case TASK_CREATE:
                case TASK_CHANGE:
                case TASK_DELETE:
                case TASK_COMPLETE:
                    log.setTask(task);
                    log.setAffectedUser(null);
                    break;
                case TASK_STATE_CHANGE:
                    log.setNewTaskStatus(TaskStatus.IN_PROGRESS);
                    log.setOldTaskStatus(TaskStatus.PLANNED);
                    log.setTask(task);
                    log.setAffectedUser(null);
                    break;
                case USER_JOIN:
                case USER_LEAVE:
                case USER_KICKED:

                    log.setAffectedUser(user);
                    log.setTask(null);
                    break;
                case USER_CHANGE:
                    log.setAffectedUser(user);
                    log.setTask(null);
                    log.setNewUserType(UserType.ADMIN);
                    log.setOldUserType(UserType.NORMAL);
                    break;
                default:
                    break;
            }

            if (log.isValid()) {
                logDao.persist(log);
            } else {
                System.out.println("Invalid log entity for log type: " + logType);
            }
        }
    }
}
