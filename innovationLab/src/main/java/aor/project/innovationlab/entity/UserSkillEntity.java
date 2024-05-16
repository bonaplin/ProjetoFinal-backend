package aor.project.innovationlab.entity;

import jakarta.persistence.*;

@Entity
@Table(name="user_skill")
public class UserSkillEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "skill_id", nullable = false)
    private SkillEntity skill;

    public UserSkillEntity() {
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

    @Override
    public String toString() {
        return "UserSkillEntity{" +
                "id=" + id +
                ", user=" + user +
                ", skill=" + skill +
                '}';
    }
}