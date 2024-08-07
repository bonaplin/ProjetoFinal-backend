package aor.project.innovationlab.dto.skill;

public class SkillDto {
    private int id;
    private String name;
    private String type;

    public SkillDto() {
    }

    public SkillDto(String type, String name) {
        this.name = name;
        this.type = type;
    }

    public SkillDto(int id, String type, String name) {
        this.id = id;
        this.name = name;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SkillDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
