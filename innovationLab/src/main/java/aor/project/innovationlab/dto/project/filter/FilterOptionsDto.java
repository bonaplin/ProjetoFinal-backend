package aor.project.innovationlab.dto.project.filter;

import aor.project.innovationlab.dto.response.IdNameDto;
import aor.project.innovationlab.dto.interests.InterestDto;
import aor.project.innovationlab.dto.skill.SkillDto;

import java.util.List;

public class FilterOptionsDto {
    private List<InterestDto> interests;
    private List<SkillDto> skills;
    private List<IdNameDto> statuses;
    private List<IdNameDto> labs;

    public FilterOptionsDto() {
    }

    public List<InterestDto> getInterests() {
        return interests;
    }

    public void setInterests(List<InterestDto> interests) {
        this.interests = interests;
    }

    public List<IdNameDto> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<IdNameDto> statuses) {
        this.statuses = statuses;
    }

    public List<SkillDto> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillDto> skills) {
        this.skills = skills;
    }

    public void setLabs(List<IdNameDto> labs) {
        this.labs = labs;
    }

    public List<IdNameDto> getLabs() {
        return labs;
    }
}
