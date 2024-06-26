package aor.project.innovationlab.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name="user_skill")
@NamedQuery(name = "UserSkill.findUserSkillByName", query = "SELECT us FROM UserSkillEntity us JOIN us.skill s WHERE s.name = :name")
@NamedQuery(name = "UserSkill.findUserSkillIds", query = "SELECT us FROM UserSkillEntity us WHERE us.user = :user AND us.skill = :skill")
@NamedQuery(name = "UserSkill.getUserSkills", query = "SELECT us.skill FROM UserSkillEntity us WHERE us.user.id = :id AND us.active = true")
public class UserSkillEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "active", nullable = false, updatable = true)
    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private SkillEntity skill;

    public UserSkillEntity() {
    }

    @PrePersist
    public void prePersist() {
        this.active = true;
    }

    public int getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public SkillEntity getSkill() {
        return skill;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setSkill(SkillEntity skill) {
        this.skill = skill;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "UserSkillEntity{" +
                "id=" + id +
                ", user=" + user +
                ", skill=" + skill +
                '}';
    }
}