package aor.project.innovationlab.dto;

public class IdNameDto {
    private int id;
    private String name;

    public IdNameDto() {
    }

    public IdNameDto(int id, String name) {
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
}
