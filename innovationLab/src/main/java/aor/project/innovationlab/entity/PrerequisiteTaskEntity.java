package aor.project.innovationlab.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "prerequisite_task")
public class PrerequisiteTaskEntity implements Serializable {

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

    public PrerequisiteTaskEntity() {
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

    @Override
    public String toString() {
        return "PrerequisiteTaskEntity{" +
                "id=" + id +
                ", task=" + task +
                ", prerequisite=" + prerequisite +
                '}';
    }

}
