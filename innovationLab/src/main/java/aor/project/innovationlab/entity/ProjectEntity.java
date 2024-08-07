package aor.project.innovationlab.entity;

import aor.project.innovationlab.enums.ProjectStatus;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "project")
@NamedQuery(name = "Project.findProjectByName", query = "SELECT p FROM ProjectEntity p WHERE p.name = :name")
@NamedQuery(name = "Project.findProjectById", query = "SELECT p FROM ProjectEntity p WHERE p.id = :projectId")
@NamedQuery(name = "Project.findProjectTasks", query = "SELECT t FROM TaskEntity t WHERE t.project.id = :projectId AND t.active = true")
@NamedQuery(name = "Project.getAllProjects", query = "SELECT p FROM ProjectEntity p")
@NamedQuery(name = "Project.findUsersByProjectId", query = "SELECT pu.user FROM ProjectUserEntity pu WHERE pu.project.id = :projectId")
public class ProjectEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private long id;

    @Column(name = "name", nullable = false, unique = false)
    private String name;

    @Column(name = "system_name", nullable = false, unique = false)
    private String systemName;

    @Column(name = "description", nullable = false, unique = false)
    private String description;

    @Column(name = "created_date", nullable = false, unique = false)
    private LocalDate createdDate;

    @Column(name = "start_date", nullable = false, unique = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false, unique = false)
    private LocalDate endDate;

    @Column(name = "finish_date", nullable = true, unique = false)
    private LocalDate finishDate;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private  UserEntity creator;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<ProjectUserEntity> projectUsers = new HashSet<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<ProjectInterestEntity> projectInterests = new HashSet<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MessageEntity> messages = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "lab_id", nullable = false)
    private LabEntity lab;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TaskEntity> tasks = new HashSet<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<ProjectSkillEntity> projectSkills = new HashSet<>();

    @Column(name = "active", nullable = false, unique = false)
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, unique = false)
    private ProjectStatus status;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ProjectProductEntity> projectProducts = new HashSet<>();

    @Column(name = "max_participants", nullable = false, unique = false)
    private int maxParticipants;

    public ProjectEntity() {
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDate.now();
        status = ProjectStatus.PLANNING;
        this.maxParticipants = 4;
    }

//    public Set<TaskEntity> getTasks() {
//        return tasks;
//    }
//
//    public void setTasks(Set<TaskEntity> tasks) {
//        this.tasks = tasks;
//    }


    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LabEntity getLab() {
        return lab;
    }

    public void setLab(LabEntity lab) {
        this.lab = lab;
    }

    public Set<ProjectInterestEntity> getInterests() {
        return projectInterests;
    }

    public void setInterests(Set<ProjectInterestEntity> interests) {
        this.projectInterests = interests;
    }

    public long getId() {
        return id;
    }

    public LocalDate getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDate finishDate) {
        this.finishDate = finishDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserEntity getCreator() {
        return creator;
    }

    public void setCreator(UserEntity creator) {
        this.creator = creator;
    }

    public Set<TaskEntity> getTasks() {
        return tasks;
    }

    public void setTasks(Set<TaskEntity> tasks) {
        this.tasks = tasks;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public Set<ProjectProductEntity> getProjectProducts() {
        return projectProducts;
    }

    public void setProjectProducts(Set<ProjectProductEntity> projectProducts) {
        this.projectProducts = projectProducts;
    }

    public Set<ProjectUserEntity> getProjectUsers() {
        return projectUsers;
    }

    public void setProjectUsers(Set<ProjectUserEntity> projectUsers) {
        this.projectUsers = projectUsers;
    }

    public Set<MessageEntity> getMessages() {
        return messages;
    }

    public void setMessages(Set<MessageEntity> messages) {
        this.messages = messages;
    }

    public Set<ProjectSkillEntity> getProjectSkills() {
        return projectSkills;
    }

    public void setProjectSkills(Set<ProjectSkillEntity> projectSkills) {
        this.projectSkills = projectSkills;
    }

    public Set<ProjectInterestEntity> getProjectInterests() {
        return projectInterests;
    }

    public void setProjectInterests(Set<ProjectInterestEntity> projectInterests) {
        this.projectInterests = projectInterests;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }
}
