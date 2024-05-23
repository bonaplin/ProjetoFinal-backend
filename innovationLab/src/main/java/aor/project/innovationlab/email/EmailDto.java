package aor.project.innovationlab.email;

public class EmailDto {
    private String to;
    private String username;
    private String token;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String link) {
        this.token = link;
    }
}