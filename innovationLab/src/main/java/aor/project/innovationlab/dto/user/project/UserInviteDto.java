package aor.project.innovationlab.dto.user.project;

import java.time.LocalDate;

public class UserInviteDto {
    private String email;
    private String firstname;
    private String lastname;
    private LocalDate invitedAt;

    public UserInviteDto() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getInvitedAt() {
        return invitedAt;
    }

    public void setInvitedAt(LocalDate invitedAt) {
        this.invitedAt = invitedAt;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
}
