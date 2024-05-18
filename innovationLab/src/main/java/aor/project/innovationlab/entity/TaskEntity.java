package aor.project.innovationlab.entity;

import aor.project.innovationlab.enums.TaskStatus;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "task")
@NamedQuery(name = "Task.findTaskByTitle", query = "SELECT t FROM TaskEntity t WHERE t.title = :title")
@NamedQuery(name = "Task.findTaskById", query = "SELECT t FROM TaskEntity t WHERE t.id = :id")
public class TaskEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private Long id;

    @Column(name = "title", nullable = false, unique = true, updatable = true)
    private String title;

    @Column(name = "description", nullable = false, unique = false, updatable = true)
    private String description;

    @Column(name = "initial_date", nullable = false, unique = false, updatable = true)
    private LocalDate initialDate;

    @Column(name = "duration", nullable = false, unique = false, updatable = true)
    private Duration duration;

    @ManyToOne
    @JoinColumn(name = "responsible", nullable = false, unique = false, updatable = true)
    private UserEntity responsible;

    @OneToMany(mappedBy = "task")
    @Column(name = "executors")
    private Set<TaskExecutor> executors = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "task_additional_executors")
    private Set<String> additionalExecutors = new HashSet<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "prerequisite_id")
    private Set<PrerequisiteTaskEntity> prerequisites  = new HashSet<>();

    @OneToMany(mappedBy = "prerequisite")
    private Set<PrerequisiteTaskEntity> prerequisiteForTasks = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, unique = false, updatable = true)
    private TaskStatus status = TaskStatus.PLANNED;

    public TaskEntity() {
    }

    public Long getId() {
        return id;
    }

    public Set<String> getAdditionalExecutors() {
        return additionalExecutors;
    }

    public void setAdditionalExecutors(Set<String> additionalExecutors) {
        this.additionalExecutors = additionalExecutors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public UserEntity getResponsible() {
        return responsible;
    }

    public void setResponsible(UserEntity responsible) {
        this.responsible = responsible;
    }

    public Set<TaskEntity> getPrerequisites() {
        Set<TaskEntity> tasks = new HashSet<>();
        for (PrerequisiteTaskEntity prerequisite : prerequisites) {
            tasks.add(prerequisite.getPrerequisite());
        }
        return tasks;
    }

    public Set<TaskEntity> getPrerequisiteForTasks() {
        Set<TaskEntity> tasks = new HashSet<>();
        for (PrerequisiteTaskEntity prerequisite : prerequisiteForTasks) {
            tasks.add(prerequisite.getTask());
        }
        return tasks;
    }

    public void setPrerequisiteForTasks(Set<PrerequisiteTaskEntity> prerequisiteForTasks) {
        this.prerequisiteForTasks = prerequisiteForTasks;
    }

    public void setPrerequisites(Set<PrerequisiteTaskEntity> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public LocalDate getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(LocalDate initialDate) {
        this.initialDate = initialDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<TaskExecutor> getExecutors() {
        return executors;
    }

    public void setExecutors(Set<TaskExecutor> executors) {
        this.executors = executors;
    }

    public void removePrerequisite(TaskEntity prerequisite) {
        prerequisites.remove(prerequisite);
    }

    public void addAdditionalExecutor(String additionalExecutor) {
        additionalExecutors.add(additionalExecutor);
    }

    public void removeAdditionalExecutor(String additionalExecutor) {
        additionalExecutors.remove(additionalExecutor);
    }

    public void addPrerequisite(TaskEntity prerequisite) {
        PrerequisiteTaskEntity prerequisiteTaskEntity = new PrerequisiteTaskEntity();
        prerequisiteTaskEntity.setTask(this);
        prerequisiteTaskEntity.setPrerequisite(prerequisite);
        prerequisites.add(prerequisiteTaskEntity);
    }

    @Override
    public String toString() {
        return "TaskEntity{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", initialDate=" + initialDate +
                ", duration=" + duration +
                ", responsible=" + responsible +
                ", executors=" + executors +
                ", additionalExecutors=" + additionalExecutors +
                ", prerequisites=" + prerequisites +
                ", prerequisiteForTasks=" + prerequisiteForTasks +
                ", status=" + status +
                '}';
    }
}