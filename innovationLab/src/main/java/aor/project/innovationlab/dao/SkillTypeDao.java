package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.SkillEntity;
import aor.project.innovationlab.entity.SkillTypeEntity;
import aor.project.innovationlab.enums.SkillType;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

@Stateless
public class SkillTypeDao extends AbstractDao<SkillTypeEntity>{

    private static final long serialVersionUID = 1L;

    public SkillTypeDao() {
        super(SkillTypeEntity.class);
    }

    public SkillTypeEntity findSkillTypeByName(SkillType name) {
        try {
            return em.createNamedQuery("SkillType.findByName", SkillTypeEntity.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
