package aor.project.innovationlab.entity;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="skill_type")
public class SkillTypeEntity implements Serializable {

    private static final long serialVersionUID = 1L;

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", nullable = false, unique = true, updatable = false)
        private int id;

        @Column(name = "name", nullable = false, unique = true, updatable = true)
        private String name;

        public SkillTypeEntity() {
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "SkillTypeEntity{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }

}




