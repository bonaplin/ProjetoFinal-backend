package aor.project.innovationlab.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "project_interest")
@NamedQuery(name = "ProjectInterest.findProjectInterestIds", query = "SELECT pi.project.id, pi.interest.id FROM ProjectInterestEntity pi WHERE pi.project = :project AND pi.interest = :interest")
@NamedQuery(name = "ProjectInterest.findInterestInProject", query = "SELECT pi FROM ProjectInterestEntity pi WHERE pi.project = :project AND pi.interest = :interest")
public class ProjectInterestEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    @ManyToOne
    @JoinColumn(name = "interest_id", nullable = false)
    private InterestEntity interest;

    @Column(name = "active", nullable = false, unique = false)
    private boolean active;

    public ProjectEntity getProject() {
        return project;
    }

    @PrePersist
    public void prePersist() {
        active = true;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public InterestEntity getInterest() {
        return interest;
    }

    public void setInterest(InterestEntity interest) {
        this.interest = interest;
    }

    public long getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
