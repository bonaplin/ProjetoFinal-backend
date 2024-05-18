package aor.project.innovationlab.entity;

import jakarta.persistence.*;
import jdk.jfr.Name;

import java.io.Serializable;

@Entity
@Table(name="interest")
@NamedQueries({
        @NamedQuery(name = "Interest.findInterestByName", query = "SELECT i FROM InterestEntity i WHERE i.name = :name")
})
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

}
