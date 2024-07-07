package aor.project.innovationlab.dto.user.project;

import java.time.LocalDate;

public class UserInviteDto {
    private Long id;
    private String email;
    private String firstname;
    private String lastname;
    private String lab;
    private String img;
    private int role;
//    private LocalDate invitedAt;

    public UserInviteDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    public LocalDate getInvitedAt() {
//        return invitedAt;
//    }
//
//    public void setInvitedAt(LocalDate invitedAt) {
//        this.invitedAt = invitedAt;
//    }


    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLab() {
        return lab;
    }

    public void setLab(String lab) {
        this.lab = lab;
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

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
