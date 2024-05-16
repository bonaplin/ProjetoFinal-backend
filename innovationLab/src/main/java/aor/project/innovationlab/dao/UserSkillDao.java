package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.SkillEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.entity.UserSkillEntity;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

@Stateless
public class UserSkillDao extends AbstractDao<UserSkillEntity> {
    private static final long serialVersionUID = 1L;

    public UserSkillDao() {
        super(UserSkillEntity.class);
    }

    public boolean userHasSkill(UserEntity user, SkillEntity skill) {
        TypedQuery<UserSkillEntity> query = em.createQuery(
                "SELECT us FROM UserSkillEntity us WHERE us.user = :user AND us.skill = :skill",
                UserSkillEntity.class);
        query.setParameter("user", user);
        query.setParameter("skill", skill);
        try {
            query.getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }
}
