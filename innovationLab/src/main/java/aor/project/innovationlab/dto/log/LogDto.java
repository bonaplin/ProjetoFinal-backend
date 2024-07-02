package aor.project.innovationlab.dto.log;

import java.time.Instant;

public class LogDto {
    private Long id;
    private Instant instant;
    private String userEmail;
    private String userFirstName;

    private Integer type;

    private String affectedUserEmail;
    private String affectedUserFirstName;
    private Integer newUserType;
    private Integer oldUserType;

    private Long projectId;
    private Integer newProjectStatus;
    private Integer oldProjectStatus;

    private Long taskId;
    private String taskName;
    private Integer newTaskStatus;
    private Integer oldTaskStatus;

    private String note;


    public LogDto(){}

    public String getAffectedUserEmail() {
        return affectedUserEmail;
    }

    public void setAffectedUserEmail(String affectedUserEmail) {
        this.affectedUserEmail = affectedUserEmail;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getOldUserType() {
        return oldUserType;
    }

    public void setOldUserType(Integer oldUserType) {
        this.oldUserType = oldUserType;
    }

    public Integer getOldTaskStatus() {
        return oldTaskStatus;
    }

    public void setOldTaskStatus(Integer oldTaskStatus) {
        this.oldTaskStatus = oldTaskStatus;
    }

    public Integer getOldProjectStatus() {
        return oldProjectStatus;
    }

    public void setOldProjectStatus(Integer oldProjectStatus) {
        this.oldProjectStatus = oldProjectStatus;
    }

    public Integer getNewUserType() {
        return newUserType;
    }

    public void setNewUserType(Integer newUserType) {
        this.newUserType = newUserType;
    }

    public Integer getNewTaskStatus() {
        return newTaskStatus;
    }

    public void setNewTaskStatus(Integer newTaskStatus) {
        this.newTaskStatus = newTaskStatus;
    }

    public Integer getNewProjectStatus() {
        return newProjectStatus;
    }

    public void setNewProjectStatus(Integer newProjectStatus) {
        this.newProjectStatus = newProjectStatus;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getAffectedUserFirstName() {
        return affectedUserFirstName;
    }

    public void setAffectedUserFirstName(String affectedUserFirstName) {
        this.affectedUserFirstName = affectedUserFirstName;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
