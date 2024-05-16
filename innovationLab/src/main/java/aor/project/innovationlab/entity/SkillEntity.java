package aor.project.innovationlab.entity;

import aor.project.innovationlab.enums.SkillType;
import jakarta.persistence.*;

import java.io.Serializable;
@Entity
@Table(name="skill")
@NamedQuery(name = "Skill.findSkillByName", query = "SELECT s FROM SkillEntity s WHERE s.name = :name")
@NamedQuery(name = "Skill.findSkillById", query = "SELECT s FROM SkillEntity s WHERE s.id = :id")
public class SkillEntity implements Serializable {

    private static final long serialVersionUID = 1L;

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", nullable = false, unique = true, updatable = false)
        private int id;

        @Column(name = "name", nullable = false, unique = true, updatable = true)
        private String name;

        @Column(name = "description", nullable = true, unique = false, updatable = true)
        private String description;

        @Enumerated(EnumType.STRING)
        @Column(name = "skill_type", nullable = false, unique = false, updatable = true)
        private SkillType skillType;

        public SkillEntity() {
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public SkillType getSkillType() {
            return skillType;
        }

        public void setSkillType(SkillType skillType) {
            this.skillType = skillType;
        }

        @Override
        public String toString() {
            return "SkillEntity{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
}
