package aor.project.innovationlab.entity;

import jakarta.inject.Named;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "executor")
@NamedQuery(name = "Executor.findExecutorByName", query = "SELECT e FROM ExecutorEntity e WHERE e.name = :name")
@NamedQuery(name = "Executor.findAllExecutors", query = "SELECT e FROM ExecutorEntity e WHERE e.active = true")
public class ExecutorEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private Long id;

    @Column(name="name", nullable = false, unique = true, updatable = true)
    private String name;

    @Column(name = "active")
    private boolean active = true;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
