package aor.project.innovationlab.dto.user;

public class UserDto {
    private long id;
    private String username;
//    private String password;
    private String email;
    private String firstname;
    private String lastname;
//    private String phone;
    private String active;
    private String confirmed;
    private String role;
    private String lablocation;

    public UserDto() {
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

//    public String getPassword() {
//        return password;
//    }
//
//    public String getPhone() {
//        return phone;
//    }

    public String getUsername() {
        return username;
    }

    public long getId() {
        return id;
    }

    public String getActive() {
        return active;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public String getRole() {
        return role;
    }

    public String getLablocation() {
        return lablocation;
    }

    public void setLablocation(String lablocation) {
        this.lablocation = lablocation;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public void setPhone(String phone) {
//        this.phone = phone;
//    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", username='" + username + '\'' +

                ", email='" + email + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +

                ", active='" + active + '\'' +
                ", confirmed='" + confirmed + '\'' +
                ", userType='" + role + '\'' +
                ", lablocation='" + lablocation + '\'' +
                '}';
    }
}
