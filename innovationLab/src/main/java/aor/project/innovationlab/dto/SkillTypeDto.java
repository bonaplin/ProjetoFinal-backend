package aor.project.innovationlab.dto;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement

public class SkillTypeDto {
    private int id;
    private String name;

    public SkillTypeDto() {
    }

    public SkillTypeDto(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SkillTypeDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
