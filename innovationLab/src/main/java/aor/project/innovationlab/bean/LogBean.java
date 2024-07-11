package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.dto.log.LogDto;
import aor.project.innovationlab.dto.project.notes.NoteIdNoteDto;
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

    public LogEntity addNewTask(Long projectId, Long userId, Long taskId){
        LogEntity log = new LogEntity();
        ProjectEntity project = projectDao.findProjectById(projectId);
        TaskEntity task = taskDao.findTaskById(taskId);
        UserEntity user = userDao.findUserById(userId);

        if(project == null || task == null || user == null) {
            return null;
        }

        log.setType(LogType.TASK_CREATE);
        log.setUser(user);
        log.setProject(project);

        log.setTask(task);
        logDao.persist(log);
        System.out.println("Task created");
        return log;
    }
    public LogEntity addNewTaskStateChange(Long projectId, Long userId, Long taskId, TaskStatus oldStatus, TaskStatus newStatus){
        LogEntity log = new LogEntity();
        ProjectEntity project = projectDao.findProjectById(projectId);
        TaskEntity task = taskDao.findTaskById(taskId);
        UserEntity user = userDao.findUserById(userId);

        if(project == null || task == null || user == null) {
            return null;
        }

        log.setType(LogType.TASK_STATE_CHANGE);
        log.setUser(user);
        log.setProject(project);

        log.setTask(task);
        log.setOldTaskStatus(oldStatus);
        log.setNewTaskStatus(newStatus);
        System.out.println("Task state changed");
        logDao.persist(log);
        return log;
    }
    public LogEntity addNewTaskchange(Long projectId, Long userId, Long taskId){
        LogEntity log = new LogEntity();
        ProjectEntity project = projectDao.findProjectById(projectId);
        TaskEntity task = taskDao.findTaskById(taskId);
        UserEntity user = userDao.findUserById(userId);

        if(project == null || task == null || user == null) {
            return null;
        }

        log.setType(LogType.TASK_CHANGE);
        log.setUser(user);
        log.setProject(project);

        log.setTask(task);
        logDao.persist(log);
        return log;
    }
    public LogEntity addNewTaskDelete(Long projectId, Long userId, Long taskId){
        LogEntity log = new LogEntity();
        ProjectEntity project = projectDao.findProjectById(projectId);
        TaskEntity task = taskDao.findTaskById(taskId);
        UserEntity user = userDao.findUserById(userId);

        if(project == null || task == null || user == null) {
            return null;
        }

        log.setType(LogType.TASK_DELETE);
        log.setUser(user);
        log.setProject(project);

        log.setTask(task);
        logDao.persist(log);
        System.out.println("Task deleted");
        return log;
    }
    public LogEntity addNewTaskCompleted(Long projectId, Long userId, Long taskId){
        LogEntity log = new LogEntity();
        ProjectEntity project = projectDao.findProjectById(projectId);
        TaskEntity task = taskDao.findTaskById(taskId);
        UserEntity user = userDao.findUserById(userId);

        if(project == null || task == null || user == null) {
            return null;
        }

        log.setType(LogType.TASK_COMPLETE);
        log.setUser(user);
        log.setProject(project);

        log.setTask(task);
        logDao.persist(log);
        System.out.println("Task completed");
        return log;
    }


    public LogEntity addNewUser(Long projectId, Long userId, Long affectedUserId){
        LogEntity log = new LogEntity();
        ProjectEntity project = projectDao.findProjectById(projectId);
        UserEntity affectedUser = userDao.findUserById(affectedUserId);
        UserEntity user = userDao.findUserById(userId);

        if(project == null || affectedUser == null || user == null) {
            return null;
        }

        log.setType(LogType.USER_JOIN);
        log.setUser(user);
        log.setProject(project);

        log.setAffectedUser(affectedUser);
        logDao.persist(log);
//        System.out.println("User joined");
        return log;
    }
    public LogEntity addNewUserLeave(Long projectId, Long affectedUserId){
        LogEntity log = new LogEntity();
        ProjectEntity project = projectDao.findProjectById(projectId);
        UserEntity affectedUser = userDao.findUserById(affectedUserId);

        if(project == null || affectedUser == null) {
            return null;
        }

        log.setType(LogType.USER_LEAVE);
        log.setUser(affectedUser);
        log.setProject(project);

        log.setAffectedUser(affectedUser);
        logDao.persist(log);
        System.out.println("User left");
        return log;
    }
    public LogEntity addNewUserChange(Long projectId, Long userId, Long affectedUserId, UserType oldType, UserType newType){
        LogEntity log = new LogEntity();
        ProjectEntity project = projectDao.findProjectById(projectId);
        UserEntity affectedUser = userDao.findUserById(affectedUserId);
        UserEntity user = userDao.findUserById(userId);

        if(project == null || affectedUser == null || user == null) {
            return null;
        }

        log.setType(LogType.USER_CHANGE);
        log.setUser(user);
        log.setProject(project);

        log.setAffectedUser(affectedUser);
        log.setOldUserType(oldType);
        log.setNewUserType(newType);
        logDao.persist(log);
        System.out.println("User changed");
        return log;
    }
    public LogEntity addNewUserKicked(Long projectId, Long userId, Long affectedUserId){
        LogEntity log = new LogEntity();
        ProjectEntity project = projectDao.findProjectById(projectId);
        UserEntity affectedUser = userDao.findUserById(affectedUserId);
        UserEntity user = userDao.findUserById(userId);

        if(project == null || affectedUser == null || user == null) {
            return null;
        }

        log.setType(LogType.USER_KICKED);
        log.setUser(user);
        log.setProject(project);

        log.setAffectedUser(affectedUser);
        logDao.persist(log);
        System.out.println("User kicked");
        return log;
    }

    public LogEntity addNewProjectChange(Long projectId, Long userId){
        LogEntity log = new LogEntity();
        ProjectEntity project = projectDao.findProjectById(projectId);
        UserEntity user = userDao.findUserById(userId);

        if(project == null || user == null) {
            return null;
        }

        log.setType(LogType.PROJECT_CHANGE);
        log.setUser(user);
        log.setProject(project);

        logDao.persist(log);
        System.out.println("Project changed");
        return log;
    }
    public LogEntity addNewProjectStateChange(Long projectId, Long userId, ProjectStatus oldStatus, ProjectStatus newStatus){
        LogEntity log = new LogEntity();
        ProjectEntity project = projectDao.findProjectById(projectId);
        UserEntity user = userDao.findUserById(userId);

        if(project == null || user == null) {
            return null;
        }

        log.setType(LogType.PROJECT_STATE_CHANGE);
        log.setUser(user);
        log.setProject(project);

        log.setOldProjectStatus(oldStatus);
        log.setNewProjectStatus(newStatus);
        logDao.persist(log);
        System.out.println("Project state changed");
        return log;
    }

    public LogEntity addNewNote(Long projectId, Long userId, String note){
        LogEntity log = new LogEntity();
        ProjectEntity project = projectDao.findProjectById(projectId);
        UserEntity user = userDao.findUserById(userId);

        if(project == null || user == null) {
            return null;
        }

        log.setType(LogType.NOTE);
        log.setUser(user);
        log.setProject(project);
        log.setNotes(note);

        logDao.persist(log);
        System.out.println("Note added");
        return log;
    }
    public LogEntity addNewNoteTask(Long projectId, Long userId, Long taskId, String note){
        LogEntity log = new LogEntity();
        ProjectEntity project = projectDao.findProjectById(projectId);
        TaskEntity task = taskDao.findTaskById(taskId);
        UserEntity user = userDao.findUserById(userId);

        if(project == null || task == null || user == null) {
            return null;
        }

        log.setType(LogType.NOTE_TASK);
        log.setUser(user);
        log.setProject(project);
        log.setTask(task);
        log.setNotes(note);

        logDao.persist(log);
        System.out.println("Note added to task");
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
        dto.setUserPicture(entity.getUser() != null ? entity.getUser().getProfileImagePath() : null);
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
            case NOTE:
                dto.setNote(entity.getNotes() != null ? entity.getNotes() : null);
            default:
            case NOTE_TASK:
                dto.setNote(entity.getNotes() != null ? entity.getNotes() : null);
                dto.setTaskName(entity.getTask() != null ? entity.getTask().getTitle() : null);
                dto.setTaskId(entity.getTask() != null ? entity.getTask().getId() : null);
                break;
        }

        return dto;
    }

    private void test(){
        UserType ut1 = UserType.ADMIN;
        UserType ut2 = UserType.NORMAL;
        ProjectStatus ps1= ProjectStatus.PLANNING;
        ProjectStatus ps2= ProjectStatus.READY;
        TaskStatus ts1 = TaskStatus.IN_PROGRESS;
        TaskStatus ts2 = TaskStatus.PLANNED;
        addNewUser(1L, 1L, 1L);
        addNewUserChange(1L, 1L, 1L, ut1, ut2);
        addNewUserKicked(1L, 1L, 1L);
        addNewUserLeave(1L, 1L);
        addNewTask(1L, 1L, 1L);
        addNewTaskchange(1L, 1L, 1L);
        addNewTaskCompleted(1L, 1L, 1L);
        addNewTaskDelete(1L, 1L, 1L);
        addNewTaskStateChange(1L, 1L, 1L, ts1, ts2);
        addNewProjectChange(1L, 1L);
        addNewProjectStateChange(1L, 1L, ps1, ps2);
        addNewNoteTask(1L, 1L, 1L, "note");
        addNewNote(1L, 1L, "note");
    }

    public void createInitialData() {
            createOneOfEachLogType((long) 1);
            test();
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
                case NOTE:
                    log.setTask(null);
                    log.setAffectedUser(null);
                    log.setNotes("This is a note");
                    log.setNewTaskStatus(null);
                    log.setOldTaskStatus(null);
                    log.setNewUserType(null);
                    log.setOldUserType(null);
                    break;
                case NOTE_TASK:
                    log.setTask(task);
                    log.setAffectedUser(null);
                    log.setNotes("This is a note");
                    log.setNewTaskStatus(null);
                    log.setOldTaskStatus(null);
                    log.setNewUserType(null);
                    log.setOldUserType(null);
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

    public LogDto addProjectNotes(String token, Long id, NoteIdNoteDto notes) {
        String log = "Attempting to add project notes";
        SessionEntity se = sessionDao.findSessionByToken(token);
        ProjectEntity pe = projectDao.findProjectById(id);
        if(se == null || pe == null) {
            LoggerUtil.logInfo(log, "Invalid token or project", null, token);
            throw new IllegalArgumentException("Invalid token or project");
        }
        UserEntity user = se.getUser();
        ProjectUserEntity pue = projectUserDao.findProjectUserByProjectIdAndUserId(id, user.getId());

        if(pue == null || !pue.isActive()) {
            LoggerUtil.logInfo(log, "User is not a participant in the project", user.getEmail(), token);
            throw new IllegalArgumentException("User is not a participant in the project");
        }

        if(notes == null || notes.getNote() == null || notes.getNote().isEmpty()) {
            LoggerUtil.logInfo(log, "Note is required", user.getEmail(), token);
            throw new IllegalArgumentException("Note is required");
        }

        LogEntity le = new LogEntity();
        le.setProject(pe);
        le.setUser(user);
        le.setNotes(notes.getNote());
        le.setType(LogType.NOTE);
        if(notes.getId() != null){
            TaskEntity te = taskDao.findTaskById(notes.getId());
            if(te != null){
                le.setTask(te);
                le.setType(LogType.NOTE_TASK);
            }
        }
        logDao.persist(le);
        System.out.println(le.getInstant());
        LogDto dto = toDto(le);
        System.out.println(dto.getInstant());
        return toDto(le);
    }
}
