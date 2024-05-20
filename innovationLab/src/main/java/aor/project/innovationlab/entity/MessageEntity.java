package aor.project.innovationlab.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "message")
@NamedQuery(name = "Message.findMessageById", query = "SELECT m FROM MessageEntity m WHERE m.id = :id")
@NamedQuery(name = "Message.findMessagesByProjectId", query = "SELECT m FROM MessageEntity m WHERE m.project.id = :project_id")
public class MessageEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private long id;

    @Column(name = "message", nullable = false, unique = false)
    private String message;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "instant", nullable = false, unique = false)
    private Instant instant;

    @Column(name = "active", nullable = false, unique = false)
    private boolean active;

    public MessageEntity() {
    }

    public long getId() {
        return id;
    }

    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @PrePersist
    public void prePersist() {
        instant = Instant.now();
        active = true;
    }
}
