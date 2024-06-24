package aor.project.innovationlab.entity;

import aor.project.innovationlab.enums.ProjectUserType;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "project_user")
public class ProjectUserEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private ProjectUserType role;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "tokenAuthorization", nullable = true)
    private String tokenAuthorization;

    public ProjectUserEntity() {
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public ProjectUserType getRole() {
        return role;
    }

    public void setRole(ProjectUserType role) {
        this.role = role;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTokenAuthorization() {
        return tokenAuthorization;
    }

    public void setTokenAuthorization(String tokenAuthorization) {
        this.tokenAuthorization = tokenAuthorization;
    }
}
