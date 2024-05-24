package aor.project.innovationlab.dto.user;

public class UserConfirmAccountDto {
    private String firstName;
    private String lastName;
    private String username;
    private int labId;
    private String about;


    public UserConfirmAccountDto() {
    }

    public String getFirstName() {
        return firstName;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getLabId() {
        return labId;
    }

    public void setLabId(int labId) {
        this.labId = labId;
    }

    @Override
    public String toString() {
        return "UserConfirmAccountDto{" +
                "about='" + about + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", username='" + username + '\'' +
                ", labId=" + labId +
                '}';
    }
}
