package aor.project.innovationlab.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name="user_interest")
@NamedQuery(name = "UserInterest.findUserInterestByName", query = "SELECT ui FROM UserInterestEntity ui JOIN ui.interest i WHERE i.name = :name")
@NamedQuery(name = "UserInterest.findUserInterestIds", query = "SELECT ui FROM UserInterestEntity ui WHERE ui.user = :user AND ui.interest = :interest")
public class UserInterestEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false, unique = true, updatable = false)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "interest_id", nullable = false)
    private InterestEntity interest;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    public UserInterestEntity() {
    }

    public long getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public InterestEntity getInterest() {
        return interest;
    }

    public void setInterest(InterestEntity interest) {
        this.interest = interest;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
