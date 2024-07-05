package aor.project.innovationlab.dto.task;

import java.time.LocalDate;
import java.util.List;

public class TaskGanttDto {
    private Long id;
    private String title;
    private String systemTitle;
    private String description;
    private String status; // Status da tarefa, ex: "DONE", "IN_PROGRESS", "NOT_STARTED"
    private LocalDate initialDate; // Data de início da tarefa
    private LocalDate finalDate; // Data final da tarefa
    private List<DependentTaskDto> dependentTasks; // Lista de tarefas dependentes
    private List<MemberDto> membersOfTask; // Lista de membros atribuídos à tarefa
    private List<MemberDto> additionalMembersOfTask; // Lista de membros adicionais atribuídos à tarefa
    private TaskGanttDto projectTask;

    public TaskGanttDto() {
    }

    public List<DependentTaskDto> getDependentTasks() {
        return dependentTasks;
    }

    public void setDependentTasks(List<DependentTaskDto> dependentTasks) {
        this.dependentTasks = dependentTasks;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSystemTitle() {
        return systemTitle;
    }

    public void setSystemTitle(String systemTitle) {
        this.systemTitle = systemTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<MemberDto> getMembersOfTask() {
        return membersOfTask;
    }

    public void setMembersOfTask(List<MemberDto> membersOfTask) {
        this.membersOfTask = membersOfTask;
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

    public TaskGanttDto getProjectTask() {
        return projectTask;
    }

    public void setProjectTask(TaskGanttDto projectTask) {
        this.projectTask = projectTask;
    }

    public void setAdditionalMembersOfTask(List<MemberDto> additionalMembersOfTask) {
        this.additionalMembersOfTask = additionalMembersOfTask;
    }

    public List<MemberDto> getAdditionalMembersOfTask() {
        return additionalMembersOfTask;
    }
}
