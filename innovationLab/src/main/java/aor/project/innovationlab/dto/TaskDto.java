package aor.project.innovationlab.dto;

import aor.project.innovationlab.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private LocalDate initialDate;
    private Duration duration;
    private String responsible;
    private Set<String> additionalExecutors;
    private Set<Long> prerequisiteIds = new HashSet<>();
    private TaskStatus status;

    public TaskDto() {
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
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

    public Set<Long> getPrerequisiteIds() {
        return prerequisiteIds;
    }

    public void setPrerequisiteIds(Set<Long> prerequisiteIds) {
        this.prerequisiteIds = prerequisiteIds;
    }

    public LocalDate getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(LocalDate initialDate) {
        this.initialDate = initialDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Set<String> getAdditionalExecutors() {
        return additionalExecutors;
    }

    public void setAdditionalExecutors(Set<String> additionalExecutors) {
        this.additionalExecutors = additionalExecutors;
    }
}
