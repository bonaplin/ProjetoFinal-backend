package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.SkillEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.entity.UserSkillEntity;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

@Stateless
public class UserSkillDao extends AbstractDao<UserSkillEntity> {
    private static final long serialVersionUID = 1L;

    public UserSkillDao() {
        super(UserSkillEntity.class);
    }

    // Não sei se é necessário fazer um método para adicionar uma skill ao user
    public UserSkillEntity userHasSkill(UserEntity user, SkillEntity skill) {
        TypedQuery<UserSkillEntity> query = em.createQuery(
                "SELECT us FROM UserSkillEntity us WHERE us.user = :user AND us.skill = :skill",
                UserSkillEntity.class);
        query.setParameter("user", user);
        query.setParameter("skill", skill);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<SkillEntity> getUserSkills(long id) {
        TypedQuery<SkillEntity> query = em.createQuery(
                "SELECT us.skill FROM UserSkillEntity us WHERE us.user.id = :id AND us.active = true",
                SkillEntity.class);
        query.setParameter("id", id);
        return query.getResultList();
    }
}
