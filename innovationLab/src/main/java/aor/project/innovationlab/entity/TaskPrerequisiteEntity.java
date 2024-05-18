package aor.project.innovationlab.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "task_prerequisite")
@NamedQuery(name = "PrerequisiteTask.findPrerequisiteTaskById", query = "SELECT pt FROM TaskPrerequisiteEntity pt WHERE pt.id = :id")
public class TaskPrerequisiteEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private TaskEntity task;

    @ManyToOne
    @JoinColumn(name = "prerequisite_id", nullable = false)
    private TaskEntity prerequisite;

    @Column(name = "active", nullable = false)
    private boolean active;

    public TaskPrerequisiteEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TaskEntity getTask() {
        return task;
    }

    public void setTask(TaskEntity task) {
        this.task = task;
    }

    public TaskEntity getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(TaskEntity prerequisite) {
        this.prerequisite = prerequisite;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "PrerequisiteTaskEntity{" +
                "id=" + id +
                ", task=" + task +
                ", prerequisite=" + prerequisite +
                ", active=" + active +
                '}';
    }

}
