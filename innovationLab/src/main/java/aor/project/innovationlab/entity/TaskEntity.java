package aor.project.innovationlab.entity;

import aor.project.innovationlab.enums.TaskStatus;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private List<TaskExecutor> executors = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "task_additional_executors")
    private List<String> additionalExecutors = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "prerequisite_id")
    private TaskEntity prerequisite;

    @OneToMany(mappedBy = "prerequisite")
    private List<TaskEntity> prerequisiteForTasks = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, unique = false, updatable = true)
    private TaskStatus status = TaskStatus.PLANNED;

    public TaskEntity() {
    }

    public List<String> getAdditionalExecutors() {
        return additionalExecutors;
    }

    public void setAdditionalExecutors(List<String> additionalExecutors) {
        this.additionalExecutors = additionalExecutors;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TaskExecutor> getExecutors() {
        return executors;
    }

    public Long getId() {
        return id;
    }

    public TaskEntity getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(TaskEntity prerequisite) {
        this.prerequisite = prerequisite;
    }

    public UserEntity getResponsible() {
        return responsible;
    }

    public void setResponsible(UserEntity responsible) {
        this.responsible = responsible;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public LocalDate getInitialDate() {
        return initialDate;
    }

    public Duration getDuration() {
        return duration;
    }

    public List<TaskEntity> getPrerequisiteForTasks() {
        return prerequisiteForTasks;
    }

    public void setPrerequisiteForTasks(List<TaskEntity> prerequisiteForTasks) {
        this.prerequisiteForTasks = prerequisiteForTasks;
    }

    public void setInitialDate(LocalDate initialDate) {
        this.initialDate = initialDate;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = TaskStatus.valueOf(status);
    }

    public String getStatusString() {
        return status.toString();
    }

    public void setStatusString(String status) {
        this.status = TaskStatus.valueOf(status);
    }

    public void setResponsible(String responsible) {
        this.responsible = new UserEntity();
        this.responsible.setUsername(responsible);
    }

    public void setExecutors(List<TaskExecutor> executors) {
        this.executors = executors;
    }

    //ADD_EXECUTOR ADD_ADDITIONAL_EXECUTOR
    public void addExecutor(TaskExecutor executor) {
        executors.add(executor);
    }

    public void addAdditionalExecutor(String executor) {
        additionalExecutors.add(executor);
    }

    public void removeAdditionalExecutor(String executor) {
        additionalExecutors.remove(executor);
    }

    public void addExecutor(UserEntity executor) {
        additionalExecutors.add(executor.getUsername());
    }

    public void removeExecutor(UserEntity executor) {
        executors.removeIf(taskExecutor -> taskExecutor.getExecutor().equals(executor));
    }

    public Long getPrerequisiteId() {
        return prerequisite.getId();
    }
}