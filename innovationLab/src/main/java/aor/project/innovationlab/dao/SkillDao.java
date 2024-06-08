package aor.project.innovationlab.dao;

import aor.project.innovationlab.dto.skill.SkillDto;
import aor.project.innovationlab.entity.ProjectEntity;
import aor.project.innovationlab.entity.SkillEntity;
import aor.project.innovationlab.enums.SkillType;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Stateless
public class SkillDao extends AbstractDao<SkillEntity>{

    private static final long serialVersionUID = 1L;

    public SkillDao() {
        super(SkillEntity.class);
    }

    public SkillEntity findSkillByName(String name) {
        try {
            return (SkillEntity) em.createNamedQuery("Skill.findSkillByName").setParameter("name", name)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public List<String> getAllSkillType() {
        return em.createNamedQuery("Skill.getAllSkillType", String.class)
                .getResultList();
    }

    public List<SkillEntity> findSkills(String name, String skillType, String userEmail, String projectName) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SkillEntity> cq = cb.createQuery(SkillEntity.class);
        Root<SkillEntity> skill = cq.from(SkillEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        if(name != null) {
            predicates.add(cb.equal(skill.get("name"), name));
        }
        if(skillType != null) {
            SkillType enumSkillType = SkillType.valueOf(skillType.toUpperCase());
            predicates.add(cb.equal(skill.get("skillType"), enumSkillType));
        }
        if(userEmail != null) {
            predicates.add(cb.equal(skill.get("userSkills").get("user").get("email"), userEmail));
        }
        if(projectName != null) {
            predicates.add(cb.equal(skill.get("projectSkills").get("project").get("name"), projectName));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }


    public List<SkillEntity> getAllSkills() {
        return em.createNamedQuery("Skill.getAllSkills", SkillEntity.class)
                .getResultList();
    }
}
