package aor.project.innovationlab.entity;

import jakarta.persistence.*;
import jdk.jfr.Name;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name ="app_config")
@NamedQuery(name = "AppConfig.findLastConfig", query = "SELECT a FROM AppConfigEntity a ORDER BY a.id DESC")
@NamedQuery(name = "AppConfig.getMaxUsersAllowed", query = "SELECT a FROM AppConfigEntity a")
public class AppConfigEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private long id;

    @Column(name = "max_users", nullable = false, updatable = false)
    private int maxUsers;

    @Column(name ="time_out_normal", nullable = false, updatable = false)
    private int timeOut=10;

    @Column(name = "time_out_admin", nullable = false, updatable = false)
    private int timeOutAdmin=60;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserEntity user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public AppConfigEntity() {
    }

    public long getId() {
        return id;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public int getTimeOutAdmin() {
        return timeOutAdmin;
    }

    public void setTimeOutAdmin(int timeOutAdmin) {
        this.timeOutAdmin = timeOutAdmin;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
