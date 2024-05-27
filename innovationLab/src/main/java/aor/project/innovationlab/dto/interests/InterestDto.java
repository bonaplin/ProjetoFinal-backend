package aor.project.innovationlab.dto.interests;

public class InterestDto {
    private long id;
    private String name;

    public InterestDto() {
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

    @Override
    public String toString() {
        return "InterestDto{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
