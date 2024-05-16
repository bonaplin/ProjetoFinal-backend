package aor.project.innovationlab.entity;

import jakarta.persistence.*;

import java.io.Serializable;
@Entity
@Table(name="skill")
@NamedQuery(name = "Skill.findSkillByName", query = "SELECT s FROM SkillEntity s WHERE s.name = :name")
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

        @ManyToOne
        @JoinColumn(name = "skill_type_id", nullable = false)
        private SkillTypeEntity skillType;

//        @ManyToOne
//        @JoinColumn(name = "user_id", nullable = false)
//        private UserEntity user;

        public SkillEntity() {
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public SkillTypeEntity getSkillType() {
            return skillType;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setSkillType(SkillTypeEntity skillType) {
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
