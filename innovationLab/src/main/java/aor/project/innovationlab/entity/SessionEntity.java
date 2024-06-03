package aor.project.innovationlab.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "session")
@NamedQuery(name = "Session.findSessionByToken", query = "SELECT s FROM SessionEntity s WHERE s.token = :token")
@NamedQuery(name = "Session.findSessionByEmail", query = "SELECT s FROM SessionEntity s WHERE s.user.email = :email")
@NamedQuery(name = "Session.findSessionByUserId", query = "SELECT s FROM SessionEntity s WHERE s.user.id = :userId")
@NamedQuery(name = "Session.findUserByToken", query = "SELECT s.user FROM SessionEntity s WHERE s.token = :token")
public class SessionEntity implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private long id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "expiration_date", nullable = false, unique = false)
    private Instant expirationDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column (name = "active", nullable = false, unique = false)
    private boolean active = true;

    @Column (name = "login_date", nullable = false, unique = false)
    private Instant loginDate;

    @Column (name = "logout_date", nullable = true, unique = false)
    private Instant logoutDate;

    public SessionEntity() {
    }

    public long getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Instant expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Instant getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Instant loginDate) {
        this.loginDate = loginDate;
    }

    public Instant getLogoutDate() {
        return logoutDate;
    }

    public void setLogoutDate(Instant logoutDate) {
        this.logoutDate = logoutDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }


    @PrePersist
    public void prePersist() {
        this.active = true;
        this.loginDate = Instant.now();
    }
}
