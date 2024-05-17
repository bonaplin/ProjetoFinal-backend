package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.SupplierEntity;
import jakarta.ejb.Stateless;

@Stateless
public class SupplierDao extends AbstractDao<SupplierEntity>{

    private static final long serialVersionUID = 1L;

    public SupplierDao() {
        super(SupplierEntity.class);
    }

    public SupplierEntity findSupplierByName(String name) {
        try {
            return (SupplierEntity) em.createNamedQuery("Supplier.findSupplierByName").setParameter("name", name)
                    .getSingleResult();

        } catch (Exception e) {
            return null;
        }
    }

    public SupplierEntity findSupplierById(int id) {
        try {
            return (SupplierEntity) em.createNamedQuery("Supplier.findSupplierById").setParameter("id", id)
                    .getSingleResult();

        } catch (Exception e) {
            return null;
        }
    }
}
