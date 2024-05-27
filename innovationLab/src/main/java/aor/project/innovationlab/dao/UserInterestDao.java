package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.InterestEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.entity.UserInterestEntity;
import aor.project.innovationlab.entity.UserSkillEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

@Stateless
public class UserInterestDao extends AbstractDao<UserInterestEntity> {

    private static final long serialVersionUID = 1L;


    public UserInterestDao() {
        super(UserInterestEntity.class);
    }

    public UserInterestEntity findUserInterestById(int id) {
        try {
            return (UserInterestEntity) em.createNamedQuery("UserInterest.findUserInterestByName").setParameter("name", id)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public UserInterestEntity findUserInterestIds(long id, long id1) {
        try {
            return (UserInterestEntity) em.createNamedQuery("UserInterest.findUserInterestIds")
                    .setParameter("user", id)
                    .setParameter("interest", id1)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Verify if user has interest
     * @param user
     * @param interest
     * @return
     */
    public UserInterestEntity userHasInterest(UserEntity user, InterestEntity interest) {
        TypedQuery<UserInterestEntity> query = em.createQuery(
                "SELECT us FROM UserInterestEntity us WHERE us.user = :user AND us.interest = :interest",
                UserInterestEntity.class);
        query.setParameter("user", user);
        query.setParameter("interest", interest);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
