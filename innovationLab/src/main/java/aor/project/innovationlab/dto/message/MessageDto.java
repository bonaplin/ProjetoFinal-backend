package aor.project.innovationlab.dto.message;

import java.time.Instant;

public class MessageDto {
    private long id;
    private String userEmail;
    private String userFirstName;
    private long projectId;
    private String message;
    private Instant createdAt;

    public MessageDto() {
    }

    public MessageDto(long id, String userEmail, long projectId, String message) {
        this.id = id;
        this.userEmail = userEmail;
        this.projectId = projectId;
        this.message = message;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
