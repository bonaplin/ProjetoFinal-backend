package aor.project.innovationlab.dto.project;

import aor.project.innovationlab.dto.user.UserImgCardDto;
import aor.project.innovationlab.enums.ProjectStatus;

import java.time.LocalDate;
import java.util.List;

public class ProjectCardDto {
    long id;
    String title;
    String description;

    LocalDate startDate;
    ProjectStatus status;
    List<String> keywords;
    List<String> skill;

    List<UserImgCardDto> projectUsers;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getSkills() {
        return skill;
    }

    public void setSkills(List<String> skill) {
        this.skill = skill;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public List<UserImgCardDto> getProjectUsers() {
        return projectUsers;
    }

    public void setProjectUsers(List<UserImgCardDto> projectUsers) {
        this.projectUsers = projectUsers;
    }

    //
//    public String getImageUrl() {
//        return imageUrl;
//public void setImageUrl(String imageUrl) {
//    this.imageUrl = imageUrl;
//}

//    }
}
