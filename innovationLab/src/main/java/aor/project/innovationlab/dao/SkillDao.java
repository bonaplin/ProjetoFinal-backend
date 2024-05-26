package aor.project.innovationlab.dao;

import aor.project.innovationlab.dto.skill.SkillDto;
import aor.project.innovationlab.entity.SkillEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

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

//    public List<SkillEntity> getUserSkills(long userId) {
//        return em.createNamedQuery("Skill.getUserSkills", SkillEntity.class)
//                .setParameter("id", userId)
//                .getResultList();
//    }

    public List<String> getAllSkillType() {
        return em.createNamedQuery("Skill.getAllSkillType", String.class)
                .getResultList();
    }

//    public SkillEntity findSkillById(int id) {
//        try {
//            return (SkillEntity) em.createNamedQuery("Skill.findSkillById").setParameter("id", id)
//                    .getSingleResult();
//
//        } catch (NoResultException e) {
//            return null;
//        }
//    }
}
