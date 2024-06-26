package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.SessionEntity;
import aor.project.innovationlab.entity.SkillEntity;
import aor.project.innovationlab.entity.UserEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

import java.util.List;

@Stateless
public class SessionDao extends AbstractDao<SessionEntity> {

    private static final long serialVersionUID = 1L;

    public SessionDao() {
        super(SessionEntity.class);
    }

    public SessionEntity findSessionByToken(String token) {
        try {
            return (SessionEntity) em.createNamedQuery("Session.findSessionByToken").setParameter("token", token)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public SessionEntity findSessionByEmail(String email) {
        try {
            return (SessionEntity) em.createNamedQuery("Session.findSessionByEmail").setParameter("email", email)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public SessionEntity findSessionByUserId(long userId) {
        try {
            return (SessionEntity) em.createNamedQuery("Session.findSessionByUserId").setParameter("userId", userId)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }


    public List<String> findUserTokens(UserEntity user) {
        return em.createNamedQuery("Session.findUserTokens").setParameter("user", user).getResultList();
    }

    /**
     * Find inactive sessions list.
     *
     * @return the list
     */

    public List<SessionEntity> findInactiveSessions() {
        return em.createNamedQuery("Session.findInactiveSessions").getResultList();

    }
}
