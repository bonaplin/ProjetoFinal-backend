package aor.project.innovationlab.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "task_additional_executor")
@NamedQueries({
        @NamedQuery(name = "TaskExecutorAdditional.findTaskExecutorAdditionalByTaskIdAndExecutorName",
                query = "SELECT t FROM TaskExecutorAdditionalEntity t WHERE t.task.id = :taskId AND t.executor.name = :executorName"),
        @NamedQuery(name = "TaskExecutorAdditional.findTaskExecutorAdditionalByTaskId",
                query = "SELECT t FROM TaskExecutorAdditionalEntity t WHERE t.task.id = :taskId"),
        @NamedQuery(name = "TaskExecutorAdditional.findActiveTaskExecutorAdditionalByTaskId",
                query = "SELECT t FROM TaskExecutorAdditionalEntity t WHERE t.task.id = :taskId AND t.active = true")
})

public class TaskExecutorAdditionalEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "executor_id", nullable = false)
    private ExecutorEntity executor;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private TaskEntity task;

    @Column(name = "active", nullable = false)
    private boolean active;

    public TaskExecutorAdditionalEntity() {
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ExecutorEntity getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorEntity executor) {
        this.executor = executor;
    }
}
