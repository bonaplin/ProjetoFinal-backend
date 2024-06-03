package aor.project.innovationlab.dto.project;

import aor.project.innovationlab.enums.ProjectStatus;

public class ProjectSideBarDto {
    private long id;
    private String name;
    private ProjectStatus status;

    public ProjectSideBarDto() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
