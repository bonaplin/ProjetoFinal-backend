package aor.project.innovationlab.dto.response;

public class IdNameDto {
    private long id;
    private String name;

    public IdNameDto() {
    }

    public IdNameDto(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
