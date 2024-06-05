package aor.project.innovationlab.dto.user;

public class UserCardDto {

    private long id;
    private String imagePath;

    public UserCardDto() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
