package aor.project.innovationlab.dto.user.password;

public class UserVerifyTokenDto {
    private String token;

    public UserVerifyTokenDto() {
    }

    public UserVerifyTokenDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
