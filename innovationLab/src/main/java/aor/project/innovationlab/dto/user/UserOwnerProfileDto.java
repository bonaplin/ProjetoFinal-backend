package aor.project.innovationlab.dto.user;

import aor.project.innovationlab.dto.interests.InterestDto;
import aor.project.innovationlab.dto.lab.LabDto;
import aor.project.innovationlab.dto.skill.SkillDto;

import java.util.List;

public class UserOwnerProfileDto {
    private String username;
    private String firstname;
    private String lastname;
    private int lab;
    private boolean privateProfile;
    private int role;
    private String imagePath;
    private String about;


    public UserOwnerProfileDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isPrivateProfile() {
        return privateProfile;
    }

    public void setPrivateProfile(boolean privateProfile) {
        this.privateProfile = privateProfile;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public int getLab() {
        return lab;
    }

    public void setLab(int lab) {
        this.lab = lab;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    @Override
    public String toString() {
        return "UserOwnerProfileDto{" +
                "about='" + about + '\'' +
                ", username='" + username + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", lab=" + lab +
                ", privateProfile=" + privateProfile +
                ", role=" + role +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
