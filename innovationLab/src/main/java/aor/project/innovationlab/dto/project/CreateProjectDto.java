package aor.project.innovationlab.dto.project;

import aor.project.innovationlab.dto.interests.InterestDto;
import aor.project.innovationlab.dto.product.ProductDto;
import aor.project.innovationlab.dto.skill.SkillDto;
import aor.project.innovationlab.dto.user.UserAddToProjectDto;
import aor.project.innovationlab.dto.user.UserDto;

import java.time.LocalDate;
import java.util.List;

public class CreateProjectDto {

    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private long lab_id;
    private List<UserAddToProjectDto> users;
    private List<ProductDto> resources;
    private List<InterestDto> keywords;
    private List<SkillDto> skills;

    public CreateProjectDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public long getLab_id() {
        return lab_id;
    }

    public void setLab_id(long lab_id) {
        this.lab_id = lab_id;
    }

    public List<UserAddToProjectDto> getUsers() {
        return users;
    }

    public void setUsers(List<UserAddToProjectDto> users) {
        this.users = users;
    }


    public List<ProductDto> getResources() {
        return resources;
    }

    public void setResources(List<ProductDto> resources) {
        this.resources = resources;
    }

    public List<InterestDto> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<InterestDto> keywords) {
        this.keywords = keywords;
    }

    public List<SkillDto> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillDto> skills) {
        this.skills = skills;
    }
}
