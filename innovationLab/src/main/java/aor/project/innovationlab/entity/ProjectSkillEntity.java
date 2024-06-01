package aor.project.innovationlab.entity;

import jakarta.persistence.*;
import jdk.jfr.Name;

import java.io.Serializable;

@Entity
@Table(name = "project_skill")
@NamedQuery(name = "ProjectSkill.findProjectSkillByProjectIdAndSkillId", query = "SELECT ps FROM ProjectSkillEntity ps WHERE ps.project.id = :projectId AND ps.skill.id = :skillId")
public class ProjectSkillEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

    @ManyToOne
    @JoinColumn(name = "skill_id")
    private SkillEntity skill;

    @Column(name = "active", nullable = false, unique = false)
    private boolean active;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public SkillEntity getSkill() {
        return skill;
    }

    public void setSkill(SkillEntity skill) {
        this.skill = skill;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
