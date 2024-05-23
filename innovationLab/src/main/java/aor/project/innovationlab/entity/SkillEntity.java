package aor.project.innovationlab.entity;

import aor.project.innovationlab.enums.SkillType;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="skill")
@NamedQuery(name = "Skill.findSkillByName", query = "SELECT s FROM SkillEntity s WHERE s.name = :name")
@NamedQuery(name = "Skill.findSkillById", query = "SELECT s FROM SkillEntity s WHERE s.id = :id")
@NamedQuery(name = "Skill.getUserSkills", query = "SELECT s FROM SkillEntity s JOIN s.userSkills us WHERE us.user.id = :id AND s.active = true")

public class SkillEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    //ADD_SKILL_TO_USER
    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserSkillEntity> userSkills = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_type", nullable = false)
    private SkillType skillType;

    @Column(name = "active", nullable = false, unique = false)
    private boolean active = true;

    public SkillEntity() {
    }

    @PrePersist
    public void prePersist() {
        this.active = true;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setUserSkills(List<UserSkillEntity> userSkills) {
        this.userSkills = userSkills;
    }

    public int getId() {
            return id;
        }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public void setSkillType(SkillType skillType) {
        this.skillType = skillType;
    }

    //ADD_SKILL_TO_USER
    public List<UserSkillEntity> getUserSkills() {
        return userSkills;
    }

    @Override
    public String toString() {
        return "SkillEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
