package aor.project.innovationlab.entity;

import aor.project.innovationlab.enums.TaskStatus;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
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
    @JoinColumn(name = "creator", nullable = false, unique = false, updatable = true)
    private UserEntity creator;

    @ManyToOne
    @JoinColumn(name = "responsible", nullable = false, unique = false, updatable = true)
    private UserEntity responsible;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TaskExecutorEntity> executors = new HashSet<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TaskExecutorAdditionalEntity> additionalExecutors = new HashSet<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TaskPrerequisiteEntity> prerequisites = new HashSet<>();

    @OneToMany(mappedBy = "prerequisite")
    private Set<TaskPrerequisiteEntity> prerequisiteForTasks = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, unique = false, updatable = true)
    private TaskStatus status = TaskStatus.PLANNED;

    @Column(name="active", nullable = false)
    private boolean active = true;

    // Construtor padrão
    public TaskEntity() {}

    // Getters e Setters

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

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
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

    public Set<TaskEntity> getPrerequisites() {
        Set<TaskEntity> tasks = new HashSet<>();
        for (TaskPrerequisiteEntity prerequisite : prerequisites) {
            tasks.add(prerequisite.getPrerequisite());
        }
        return tasks;
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

    // METODOS EXTRA

    /**
     * Adiciona um pré-requisito associado a esta tarefa.
     * @param prerequisite - Tarefa que será adicionada como pré-requisito
     */
    public void addPrerequisite(TaskEntity prerequisite) {
        for (TaskPrerequisiteEntity existingPrerequisite : prerequisites) {
            if (existingPrerequisite.getPrerequisite().equals(prerequisite)) {
                return; // O pré-requisito já está no array, então retorna imeadiatamente
            }
        }
        TaskPrerequisiteEntity taskPrerequisiteEntity = new TaskPrerequisiteEntity();
        taskPrerequisiteEntity.setTask(this);
        taskPrerequisiteEntity.setPrerequisite(prerequisite);
        taskPrerequisiteEntity.setActive(true);
        prerequisites.add(taskPrerequisiteEntity);
        prerequisite.getPrerequisiteForTasks().add(taskPrerequisiteEntity);
    }

    /**
     * Remove um pré-requisito associado a esta tarefa. Com ajuda do método findPrerequisiteTaskEntityByPrerequisite,
     * encontramos a entidade de pré-requisito associada a um pré-requisito específico e desativamos a mesma.
     * Tudo isto será feito em Taskentity.
     * @param prerequisite - Tarefa que será removida como pré-requisito
     */
    public void removePrerequisite(TaskEntity prerequisite) {
        TaskPrerequisiteEntity taskPrerequisiteEntity = findPrerequisiteTaskEntityByPrerequisite(prerequisite);
        if (taskPrerequisiteEntity != null) {
            taskPrerequisiteEntity.setActive(false);
        }
    }

    /**
     * Encontra a entidade de pré-requisito associada a um pré-requisito específico.
     * @param prerequisite - Tarefa que será usada para encontrar a entidade de pré-requisito
     * @return
     */
    private TaskPrerequisiteEntity findPrerequisiteTaskEntityByPrerequisite(TaskEntity prerequisite) {
        for (TaskPrerequisiteEntity taskPrerequisiteEntity : prerequisites) {
            if (taskPrerequisiteEntity.getPrerequisite().equals(prerequisite) && taskPrerequisiteEntity.isActive()) {
                return taskPrerequisiteEntity;
            }
        }
        return null;
    }


    /**
     * Adiciona um executor a esta tarefa.
     * @param executor - Executor que será adicionado a esta tarefa
     */
    public void addExecutor(UserEntity executor) {
        TaskExecutorEntity taskExecutorEntity = new TaskExecutorEntity();
        taskExecutorEntity.setTask(this);
        taskExecutorEntity.setExecutor(executor);
        taskExecutorEntity.setActive(true);
        this.getExecutors().add(taskExecutorEntity);
    }

    /**
     * Remove um executor desta tarefa.
     * @param executor - Executor que será removido desta tarefa
     */
    public void removeExecutor(TaskExecutorEntity executor) {
        this.executors.remove(executor);
        executor.setTask(null);
        executor.setActive(false);
    }


    public void removeExecutor(UserEntity executor) {
        TaskExecutorEntity taskExecutorEntity = findTaskExecutorByExecutor(executor);
        if (taskExecutorEntity != null) {
            taskExecutorEntity.setActive(false);
        }
    }

    private TaskExecutorEntity findTaskExecutorByExecutor(UserEntity executor) {
        for (TaskExecutorEntity taskExecutorEntity : this.getExecutors()) {
            if (taskExecutorEntity.getExecutor().equals(executor) && taskExecutorEntity.isActive()) {
                return taskExecutorEntity;
            }
        }
        return null;
    }

    public void addAdditionalExecutor(String additionalExecutorName) {
        TaskExecutorAdditionalEntity additionalExecutor = new TaskExecutorAdditionalEntity();
        additionalExecutor.setName(additionalExecutorName);
        additionalExecutor.setTask(this);
        additionalExecutor.setActive(true);
        this.additionalExecutors.add(additionalExecutor);
    }

    public void removeAdditionalExecutor(String additionalExecutorName) {
        TaskExecutorAdditionalEntity additionalExecutor = findAdditionalExecutorByName(additionalExecutorName);
        if (additionalExecutor != null) {
            additionalExecutor.setActive(false);
        }
    }

    private TaskExecutorAdditionalEntity findAdditionalExecutorByName(String name) {
        for (TaskExecutorAdditionalEntity additionalExecutor : this.additionalExecutors) {
            if (additionalExecutor.getName().equals(name) && additionalExecutor.isActive()) {
                return additionalExecutor;
            }
        }
        return null;
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
