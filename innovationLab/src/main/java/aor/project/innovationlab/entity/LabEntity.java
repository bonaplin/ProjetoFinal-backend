package aor.project.innovationlab.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name="lab")
@NamedQuery(name = "Lab.findAllLabs", query = "SELECT l FROM LabEntity l WHERE l.active = true")
@NamedQuery(name = "Lab.findLabById", query = "SELECT l FROM LabEntity l WHERE l.id = :id")
@NamedQuery(name = "Lab.findLabByLocation", query = "SELECT l FROM LabEntity l WHERE l.location = :location")
@NamedQuery(name = "Lab.findLabByName", query = "SELECT l FROM LabEntity l WHERE l.location = :name")
public class LabEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false, unique = true, updatable = false)
    private int id;
//
//    @Column(name="name", nullable = false, unique = true, updatable = true)
//    private String name;

    @Column(name="location", nullable = false, unique = true, updatable = true)
    private String location;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    public LabEntity() {
    }

    @PrePersist
    public void prePersist() {
        this.active = true;
    }

    public int getId() {
        return id;
    }

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }


    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
