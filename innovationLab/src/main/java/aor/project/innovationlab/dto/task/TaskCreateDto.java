package aor.project.innovationlab.dto.task;

import jakarta.ejb.Local;

import java.time.LocalDate;
import java.util.List;

public class TaskCreateDto {
    private String title;
    private String description;
    private LocalDate initialDate;
    private LocalDate finalDate;
    private List<Long> usersIds;
    private List<String> additionalExecutorsNames;
    private List<Long> dependentTasksIds;
    private Long projectId;
    private Long responsibleId;

    public TaskCreateDto() {
    }

    public List<String> getAdditionalExecutorsNames() {
        return additionalExecutorsNames;
    }

    public void setAdditionalExecutorsNames(List<String> additionalExecutorsNames) {
        this.additionalExecutorsNames = additionalExecutorsNames;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Long> getUsersIds() {
        return usersIds;
    }

    public void setUsersIds(List<Long> usersIds) {
        this.usersIds = usersIds;
    }

    public void setDependentTasksIds(List<Long> dependentTasksIds) {
        this.dependentTasksIds = dependentTasksIds;
    }

    public LocalDate getInitialDate() {
        return initialDate;
    }

    public void setInitialDate(LocalDate initialDate) {
        this.initialDate = initialDate;
    }

    public LocalDate getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(LocalDate finalDate) {
        this.finalDate = finalDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getDependentTasksIds() {
        return dependentTasksIds;
    }


    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getResponsibleId() {
        return responsibleId;
    }

    public void setResponsibleId(Long responsibleId) {
        this.responsibleId = responsibleId;
    }
}
