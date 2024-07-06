package aor.project.innovationlab.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "task_executor")
@NamedQuery(name = "TaskExecutor.findTaskExecutorByTaskIdAndExecutorId",
        query = "SELECT t FROM TaskExecutorEntity t WHERE t.task.id = :taskId AND t.executor.id = :executorId")
@NamedQuery(name = "TaskExecutor.findActiveTaskExecutorByTaskId", query = "SELECT t FROM TaskExecutorEntity t WHERE t.task.id = :taskId AND t.active = true")
@NamedQuery(name = "TaskExecutor.findTaskExecutorsByTaskId", query = "SELECT t FROM TaskExecutorEntity t WHERE t.task.id = :taskId")
public class TaskExecutorEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private Long id;

    @ManyToOne
    private TaskEntity task;

    @ManyToOne
    private UserEntity executor;

    @Column(name = "active", nullable = false)
    private boolean active;


    public TaskExecutorEntity() {
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
