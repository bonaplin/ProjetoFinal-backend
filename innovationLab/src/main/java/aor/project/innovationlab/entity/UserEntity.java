package aor.project.innovationlab.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name="user")
@NamedQuery(name = "User.findUserByEmail", query = "SELECT u FROM UserEntity u WHERE u.email = :email")
@NamedQuery(name = "User.findUserById", query = "SELECT u FROM UserEntity u WHERE u.id = :id")
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false, unique = true, updatable = false)
    private long id;

    @Column(name="username", nullable = false, unique = true, updatable = false)
    private String username;

    @Column(name="password", nullable = false)
    private String password;

    @Column(name="email", nullable = false, unique = true)
    private String email;

    @Column(name="firstname", nullable = false, unique = false, updatable = true)
    private String firstname;

    @Column(name="lastname", nullable = false, unique = false, updatable = true)
    private String lastname;

    @Column(name="phone", nullable = false, unique = false, updatable = true)
    private String phone;

    @Column(name="role", nullable = false, unique = false, updatable = true)
    private String role;

    @Column(name="token_verification", nullable = true, unique = true, updatable = true)
    private String tokenVerification;

    @Column(name="token_verification_expiration", nullable = true, unique = false, updatable = true)
    private Instant tokenExpiration;

    @Column(name="active", nullable = false, unique = false, updatable = true)
    private Boolean active;

    @Column(name="created", nullable = false, unique = false, updatable = true)
    private Instant created;

    @Column(name="confirmed", nullable = false, unique = false, updatable = true)
    private Boolean confirmed = false;

    @Column(name = "profile_image_type", nullable = true, updatable = true)
    private String profileImageType;

    @Column(name="profile_image_path", nullable = true, updatable = true)
    private String profileImagePath;

    //ADD_SKILL_TO_USER
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserSkillEntity> userSkills = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<UserInterestEntity> interests = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MessageEntity> messages = new HashSet<>();

    @ManyToOne
    @JoinColumn(name="lab_id", nullable = false, updatable = true)
    private LabEntity lab;

    public UserEntity() {
    }

    @PrePersist
    protected void onCreate(){
        created = Instant.now();
    }

    public String getProfileImageType() {
        return profileImageType;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public void setProfileImageType(String profileImageType) {
        this.profileImageType = profileImageType;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getPhone() {
        return phone;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPassword() {
        return password;
    }

    public long getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getEmail() {
        return email;
    }

    public Instant getCreated() {
        return created;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public Boolean getActive() {
        return active;
    }

    public LabEntity getLab() {
        return lab;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLab(LabEntity lab) {
        this.lab = lab;
    }

    public Set<UserInterestEntity> getInterests() {
        return interests;
    }

    public void setInterests(Set<UserInterestEntity> interests) {
        this.interests = interests;
    }

    public Set<UserSkillEntity> getUserSkills() {
        return userSkills;
    }

    public void setUserSkills(Set<UserSkillEntity> userSkills) {
        this.userSkills = userSkills;
    }

    public Set<MessageEntity> getMessages() {
        return messages;
    }

    public void setMessages(Set<MessageEntity> messages) {
        this.messages = messages;
    }

    public Instant getTokenExpiration() {
        return tokenExpiration;
    }

    public void setTokenExpiration(Instant tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
    }

    public String getTokenVerification() {
        return tokenVerification;
    }

    public void setTokenVerification(String tokenVerification) {
        this.tokenVerification = tokenVerification;
    }
}
