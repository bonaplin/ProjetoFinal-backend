package aor.project.innovationlab.entity;

import aor.project.innovationlab.enums.TaskStatus;
import jakarta.inject.Named;
import jakarta.persistence.*;
import jdk.jfr.Name;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "task")
@NamedQuery(name = "Task.findTaskByTitle", query = "SELECT t FROM TaskEntity t WHERE t.title = :title")
@NamedQuery(name = "Task.findTaskById", query = "SELECT t FROM TaskEntity t WHERE t.id = :id")
@NamedQuery(name = "Task.findTasksByProjectId", query = "SELECT t FROM TaskEntity t WHERE t.project.id = :projectId AND t.active = true")
@NamedQuery(name = "Task.findTaskBySystemTitle", query = "SELECT t FROM TaskEntity t WHERE t.systemTitle = :systemTitle")
@NamedQuery(name = "Task.findLastTaskByProjectIdExcludingPresentation", query = "SELECT t FROM TaskEntity t WHERE t.project.id = :projectId AND t.status != :presentationStatus AND t.active = true ORDER BY t.finalDate DESC")
@NamedQuery(name = "Task.findTaskByProjectIdAndStatus", query = "SELECT t FROM TaskEntity t WHERE t.project.id = :projectId AND t.status = :status")
@NamedQuery(name = "Task.findTasksByProjectIdAndTitle", query = "SELECT t FROM TaskEntity t WHERE t.project.id = :projectId AND t.title = :title")
@NamedQuery(name = "Task.findTasksByProjectIdNoPresentation", query = "SELECT t FROM TaskEntity t WHERE t.project.id = :projectId AND t.active = true AND t.status != :presentationStatus")
@NamedQuery(name = "Task.findTasksByProjectIdAndUserId", query = "SELECT t FROM TaskEntity t " + "WHERE t.project.id = :projectId " + "AND t.active = true " + "AND (t.creator.id = :userId OR t.responsible.id = :userId)"
)
public class TaskEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private Long id;

    @Column(name = "title", nullable = false, unique = false, updatable = true)
    private String title;

    @Column(name = "system_title", nullable = false, unique = true, updatable = true)
    private String systemTitle;

    @Column(name = "description", nullable = false, unique = false, updatable = true)
    private String description;

    @Column(name = "initial_date", nullable = false, unique = false, updatable = true)
    private LocalDate initialDate;

    @Column(name = "final_date", nullable = false, unique = false, updatable = true)
    private LocalDate finalDate;

    @Column(name = "duration", nullable = false, unique = false, updatable = true)
    private Period duration;

    @ManyToOne
    @JoinColumn(name = "creator", nullable = false, unique = false, updatable = true)
    private UserEntity creator;

    @ManyToOne
    @JoinColumn(name = "responsible", nullable = false, unique = false, updatable = true)
    private UserEntity responsible;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private Set<TaskExecutorEntity> executors = new HashSet<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TaskExecutorAdditionalEntity> additionalExecutors = new HashSet<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TaskPrerequisiteEntity> prerequisites = new HashSet<>();

    @OneToMany(mappedBy = "prerequisite", fetch = FetchType.LAZY)
    private Set<TaskPrerequisiteEntity> prerequisiteForTasks = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, unique = false, updatable = true)
    private TaskStatus status = TaskStatus.PLANNED;

    @Column(name="active", nullable = false)
    private boolean active = true;

    public TaskEntity() {}

    @PrePersist
    public void prePersist() {
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(LocalDate initialDate) {
        this.initialDate = initialDate;
    }

    public Period getDuration() {
        return duration;
    }

    public void setDuration(Period duration) {
        this.duration = duration;
    }

    public UserEntity getResponsible() {
        return responsible;
    }

    public void setResponsible(UserEntity responsible) {
        this.responsible = responsible;
    }

    public Set<TaskExecutorEntity> getExecutors() {
        return executors;
    }

    public void setExecutors(Set<TaskExecutorEntity> executors) {
        this.executors = executors;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<TaskPrerequisiteEntity> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(Set<TaskPrerequisiteEntity> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public Set<TaskPrerequisiteEntity> getPrerequisiteForTasks() {
        return prerequisiteForTasks;
    }

    public void setPrerequisiteForTasks(Set<TaskPrerequisiteEntity> prerequisiteForTasks) {
        this.prerequisiteForTasks = prerequisiteForTasks;
    }

    public UserEntity getCreator() {
        return creator;
    }

    public void setCreator(UserEntity creator) {
        this.creator = creator;
    }

    public Set<TaskExecutorAdditionalEntity> getAdditionalExecutors() {
        return additionalExecutors;
    }

    public void setAdditionalExecutors(Set<TaskExecutorAdditionalEntity> additionalExecutors) {
        this.additionalExecutors = additionalExecutors;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public String getSystemTitle() {
        return systemTitle;
    }

    public void setSystemTitle(String systemTitle) {
        this.systemTitle = systemTitle;
    }

    public LocalDate getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(LocalDate finalDate) {
        this.finalDate = finalDate;
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
                ", active=" + active +
                '}';
    }
}
