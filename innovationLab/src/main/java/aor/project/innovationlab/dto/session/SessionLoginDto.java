package aor.project.innovationlab.dto.session;

public class SessionLoginDto {
    private String token;

    public SessionLoginDto() {
    }

    public SessionLoginDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
