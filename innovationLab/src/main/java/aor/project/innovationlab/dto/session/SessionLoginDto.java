package aor.project.innovationlab.dto.session;

public class SessionLoginDto {
    private String token;
    private String email;
    private long unreadNotifications;
    private long unreadEmails;

    public SessionLoginDto() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public long getUnreadNotifications() {
        return unreadNotifications;
    }

    public void setUnreadNotifications(long unreadNotifications) {
        this.unreadNotifications = unreadNotifications;
    }

    public long getUnreadEmails() {
        return unreadEmails;
    }

    public void setUnreadEmails(long unreadEmails) {
        this.unreadEmails = unreadEmails;
    }
}
