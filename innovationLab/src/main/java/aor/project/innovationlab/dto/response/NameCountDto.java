package aor.project.innovationlab.dto.response;

public class NameCountDto {
    private String name;
    private Long count;

    public NameCountDto() {
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
