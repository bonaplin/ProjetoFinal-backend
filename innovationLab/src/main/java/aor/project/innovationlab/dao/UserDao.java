package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.entity.UserSkillEntity;
import jakarta.ejb.Schedule;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Stateless
public class UserDao extends AbstractDao<UserEntity> {

    private static final long serialVersionUID = 1L;

    public UserDao() {
        super(UserEntity.class);
    }

    public UserEntity findUserByEmail(String email) {
        try {
            return (UserEntity) em.createNamedQuery("User.findUserByEmail").setParameter("email", email)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public UserEntity findUserById(Long additionalExecutorId) {
        try {
            return (UserEntity) em.createNamedQuery("User.findUserById").setParameter("id", additionalExecutorId)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public UserSkillEntity findUserSkillIds(long id, int id1) {
        try {
            return (UserSkillEntity) em.createNamedQuery("UserSkill.findUserSkillIds")
                    .setParameter("user", id)
                    .setParameter("skill", id1)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}

