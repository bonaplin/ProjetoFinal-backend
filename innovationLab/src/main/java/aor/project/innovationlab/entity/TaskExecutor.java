package aor.project.innovationlab.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "task_executor")
public class TaskExecutor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private Long id;

    @ManyToOne
    private TaskEntity task;

    @ManyToOne
    private UserEntity executor;

    public TaskExecutor() {
    }

    public UserEntity getExecutor() {
        return executor;
    }

    public TaskEntity getTask() {
        return task;
    }

    public Long getId() {
        return id;
    }

    public void setTask(TaskEntity task) {
        this.task = task;
    }

    public void setExecutor(UserEntity executor) {
        this.executor = executor;
    }
}
