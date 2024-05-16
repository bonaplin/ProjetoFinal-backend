package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.LabEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

@Stateless
public class LabDao extends AbstractDao<LabEntity>{

    private static final long serialVersionUID = 1L;

    public LabDao() {
        super(LabEntity.class);
    }

    public LabEntity findLabById(int id) {
        try {
            return (LabEntity) em.createNamedQuery("Lab.findLabById").setParameter("id", id)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public LabEntity findLabByLocation(String location) {
        try {
            return (LabEntity) em.createNamedQuery("Lab.findLabByLocation").setParameter("location", location)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

}
