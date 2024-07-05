package aor.project.innovationlab.dto.task;

import aor.project.innovationlab.dto.response.LabelValueDto;

import java.util.List;

public class TaskContributorsDto {
    private List<LabelValueDto> users;
    private List<LabelValueDto> dependentTasks;
    private List<LabelValueDto> executors;

    public TaskContributorsDto() {
    }

    public List<LabelValueDto> getUsers() {
        return users;
    }

    public void setUsers(List<LabelValueDto> users) {
        this.users = users;
    }

    public List<LabelValueDto> getDependentTasks() {
        return dependentTasks;
    }

    public void setDependentTasks(List<LabelValueDto> dependentTasks) {
        this.dependentTasks = dependentTasks;
    }

    public List<LabelValueDto> getExecutors() {
        return executors;
    }

    public void setExecutors(List<LabelValueDto> executors) {
        this.executors = executors;
    }
}
