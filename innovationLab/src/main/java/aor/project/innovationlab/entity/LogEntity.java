package aor.project.innovationlab.entity;

import aor.project.innovationlab.enums.LogType;
import aor.project.innovationlab.enums.ProjectStatus;
import aor.project.innovationlab.enums.TaskStatus;
import aor.project.innovationlab.enums.UserType;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "log")
@NamedQuery(name = "Log.findLogByProjectId", query = "SELECT l FROM LogEntity l WHERE l.project.id = :projectId")
public class LogEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private long id;

    @Column(name = "instant", nullable = false)
    private Instant instant = Instant.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private LogType type;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;



    @ManyToOne
    @JoinColumn(name = "affected_user_id")
    private UserEntity affectedUser;

    @Enumerated
    @Column(name = "new_user_type")
    private UserType newUserType;

    @Enumerated
    @Column(name = "old_user_type")
    private UserType oldUserType;



    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_project_status")
    private ProjectStatus newProjectStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_project_status")
    private ProjectStatus oldProjectStatus;



    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskEntity task;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_task_status")
    private TaskStatus newTaskStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_task_status")
    private TaskStatus oldTaskStatus;


    @Column(name = "notes")
    private String notes;


    public UserEntity getAffectedUser() {
        return affectedUser;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserType getNewUserType() {
        return newUserType;
    }

    public void setNewUserType(UserType newUserType) {
        this.newUserType = newUserType;
    }

    public UserType getOldUserType() {
        return oldUserType;
    }

    public void setOldUserType(UserType oldUserType) {
        this.oldUserType = oldUserType;
    }

    public void setAffectedUser(UserEntity affectedUser) {
        this.affectedUser = affectedUser;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public ProjectStatus getNewProjectStatus() {
        return newProjectStatus;
    }

    public void setNewProjectStatus(ProjectStatus newProjectStatus) {
        this.newProjectStatus = newProjectStatus;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public LogType getType() {
        return type;
    }

    public void setType(LogType type) {
        this.type = type;
    }

    public TaskEntity getTask() {
        return task;
    }

    public void setTask(TaskEntity task) {
        this.task = task;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public TaskStatus getOldTaskStatus() {
        return oldTaskStatus;
    }

    public void setOldTaskStatus(TaskStatus oldTaskStatus) {
        this.oldTaskStatus = oldTaskStatus;
    }

    public ProjectStatus getOldProjectStatus() {
        return oldProjectStatus;
    }

    public void setOldProjectStatus(ProjectStatus oldProjectStatus) {
        this.oldProjectStatus = oldProjectStatus;
    }

    public TaskStatus getNewTaskStatus() {
        return newTaskStatus;
    }

    public void setNewTaskStatus(TaskStatus newTaskStatus) {
        this.newTaskStatus = newTaskStatus;
    }


    @PrePersist
    @PreUpdate
    public void validate() {
        this.instant = Instant.now();
        if(!isValid()) {
            throw new IllegalStateException("Invalid log entity");
        }
    }

    public boolean isValid() {
        if(type == null || user == null) return false;

        switch (type){
            case PROJECT_CHANGE:
                return isProjectChangeValid();
            case PROJECT_STATE_CHANGE:
                return isProjectStateChangeValid();
            case TASK_STATE_CHANGE:
                return isTaskChangeValid();
            case TASK_CREATE:
            case TASK_DELETE:
            case TASK_COMPLETE:
            case TASK_CHANGE:
                return isTaskAffected();
            case USER_JOIN:
            case USER_LEAVE:
            case USER_KICKED:
                return isUserAffected();
            case USER_CHANGE:
                return isUserChangeValid();
            case NOTE:
                return isNoteValid();
            case NOTE_TASK:
                return isNoteTaskValid();
            default:
                return false;
        }
    }
    private boolean isNoteValid() {
        return notes != null && project != null && affectedUser == null && newProjectStatus == null && oldProjectStatus == null && task == null && newTaskStatus == null && oldTaskStatus == null && oldUserType == null && newUserType == null;
    }

    private boolean isNoteTaskValid() {
        return notes != null && task != null && project != null && affectedUser == null && newProjectStatus == null && oldProjectStatus == null && newTaskStatus == null && oldTaskStatus == null && oldUserType == null && newUserType == null;
    }

    private boolean isProjectChangeValid() {
        return project != null &&
                affectedUser == null && newProjectStatus == null && oldProjectStatus == null && task == null && newTaskStatus == null && oldTaskStatus == null && oldUserType == null && newUserType == null;
    }

    private boolean isProjectStateChangeValid() {
        return project != null && oldProjectStatus != null && newProjectStatus != null &&
                affectedUser == null && task == null && newTaskStatus == null && oldTaskStatus == null;
    }

    private boolean isTaskChangeValid() {
        return task != null && oldTaskStatus != null && newTaskStatus != null &&
                affectedUser == null && newProjectStatus == null && oldProjectStatus == null;
    }

    private boolean isTaskAffected() {
        return task != null &&
                affectedUser == null && newProjectStatus == null && oldProjectStatus == null && newTaskStatus == null && oldTaskStatus == null && oldUserType == null && newUserType == null;
    }

    private boolean isUserAffected() {
        return affectedUser != null &&
                newProjectStatus == null && oldProjectStatus == null && task == null && newTaskStatus == null && oldTaskStatus == null && oldUserType == null && newUserType == null;
    }

    private boolean isUserChangeValid() {
        return affectedUser != null && oldUserType != null && newUserType != null &&
                newProjectStatus == null && oldProjectStatus == null && task == null && newTaskStatus == null && oldTaskStatus == null;
    }

}
