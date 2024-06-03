package aor.project.innovationlab.dto.project;

import io.jsonwebtoken.lang.Strings;

import java.util.List;

public class ProjectCardDto {
    long id;
    String title;
    String description;
//    String imageUrl;
    List<String> keywords;
    List<String> skill;

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
//
//    public String getImageUrl() {
//        return imageUrl;
//public void setImageUrl(String imageUrl) {
//    this.imageUrl = imageUrl;
//}

//    }
}
