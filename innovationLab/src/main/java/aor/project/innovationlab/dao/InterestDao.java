package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.InterestEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

import java.util.List;

@Stateless
public class InterestDao extends AbstractDao<InterestEntity> {

    private static final long serialVersionUID = 1L;


    public InterestDao() {
        super(InterestEntity.class);
    }

    public InterestEntity findInterestById(int id) {
        try {
            return (InterestEntity) em.createNamedQuery("Interest.findInterestByName").setParameter("name", id)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public InterestEntity findInterestByName(String name) {
        try {
            return (InterestEntity) em.createNamedQuery("Interest.findInterestByName").setParameter("name", name)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public List<InterestEntity> getUserInterests(long userId) {
        return em.createNamedQuery("Interest.getUserInterests")
                .setParameter("id", userId)
                .getResultList();
    }

    public List<InterestEntity> getAllInterests() {
        return em.createNamedQuery("Interest.getAllInterests")
                .getResultList();
    }

    public List<InterestEntity> getProjectInterests(long id) {
        return em.createNamedQuery("Interest.getProjectInterests")
                .setParameter("id", id)
                .getResultList();
    }
}
