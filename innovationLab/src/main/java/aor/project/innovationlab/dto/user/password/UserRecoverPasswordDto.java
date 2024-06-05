package aor.project.innovationlab.dto.user.password;

public class UserRecoverPasswordDto {
    private String password;
    private String confirmPassword;
    

    public UserRecoverPasswordDto() {
    }

    public UserRecoverPasswordDto(String password, String confirmPassword) {
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
