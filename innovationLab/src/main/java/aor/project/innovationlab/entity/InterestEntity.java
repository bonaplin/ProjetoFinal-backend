package aor.project.innovationlab.entity;

import jakarta.persistence.*;
import jdk.jfr.Name;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="interest")

@NamedQuery(name = "Interest.findInterestByName", query = "SELECT i FROM InterestEntity i WHERE i.name = :name")
public class InterestEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false, unique = true, updatable = false)
    private long id;

    @Column(name="name", nullable = false, unique = true, updatable = true)
    private String name;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "interest", fetch = FetchType.LAZY)
    private Set<UserInterestEntity> userInterestEntities = new HashSet<>();

    @OneToMany(mappedBy = "interest", fetch = FetchType.LAZY)
    private Set<ProjectInterestEntity> projectInterestEntities = new HashSet<>();

    public InterestEntity() {
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<UserInterestEntity> getUserInterests() {
        return userInterestEntities;
    }

    public void setUserInterests(Set<UserInterestEntity> userInterestEntities) {
        this.userInterestEntities = userInterestEntities;
    }

    public Set<UserInterestEntity> getUserInterestEntities() {
        return userInterestEntities;
    }

    public void setUserInterestEntities(Set<UserInterestEntity> userInterestEntities) {
        this.userInterestEntities = userInterestEntities;
    }

    public Set<ProjectInterestEntity> getProjectInterests() {
        return projectInterestEntities;
    }

    public void setProjectInterests(Set<ProjectInterestEntity> projectInterestEntities) {
        this.projectInterestEntities = projectInterestEntities;
    }
}
