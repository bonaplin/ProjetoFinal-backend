package aor.project.innovationlab.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "project")
@NamedQuery(name = "Project.findProjectByName", query = "SELECT p FROM ProjectEntity p WHERE p.name = :name")
public class ProjectEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private long id;

    @Column(name = "name", nullable = false, unique = false)
    private String name;

    @Column(name = "description", nullable = false, unique = false)
    private String description;

    @Column(name = "created_date", nullable = false, unique = false)
    private LocalDate createdDate;

    @Column(name = "start_date", nullable = false, unique = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false, unique = false)
    private LocalDate endDate;

    @Column(name = "finish_date", nullable = false, unique = false)
    private LocalDate finishDate;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private  UserEntity creator;

    @OneToMany(mappedBy = "project")
    private Set<ProjectInterestEntity> interests = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "lab_id", nullable = false)
    private LabEntity lab;
//
//    @OneToMany(mappedBy = "project")
//    private Set<TaskEntity> tasks = new HashSet<>();

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

//    public Set<TaskEntity> getTasks() {
//        return tasks;
//    }
//
//    public void setTasks(Set<TaskEntity> tasks) {
//        this.tasks = tasks;
//    }

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
        return interests;
    }

    public void setInterests(Set<ProjectInterestEntity> interests) {
        this.interests = interests;
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
}
