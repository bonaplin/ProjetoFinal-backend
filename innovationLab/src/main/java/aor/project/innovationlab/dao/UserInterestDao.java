package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.UserInterestEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

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
}
