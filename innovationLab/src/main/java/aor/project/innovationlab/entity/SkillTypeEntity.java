package aor.project.innovationlab.entity;

import aor.project.innovationlab.enums.SkillType;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="skill_type")
@NamedQuery(name="SkillType.findByName", query="SELECT s FROM SkillTypeEntity s WHERE s.name = :name")

public class SkillTypeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, unique = true, updatable = true)
    private SkillType name;

    public SkillTypeEntity() {
    }

    public int getId() {
        return id;
    }

    public SkillType getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(SkillType name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SkillTypeEntity{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }

}




