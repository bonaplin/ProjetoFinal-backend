package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.LabEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;

import java.util.List;

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

    public List<LabEntity> findAllLabs() {
        try {
            return em.createNamedQuery("Lab.findAllLabs")
                    .getResultList();

        } catch (NoResultException e) {
            return null;
        }
    }

    public LabEntity findLabByName(String name) {
        try{
            return (LabEntity) em.createNamedQuery("Lab.findLabByName").setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
