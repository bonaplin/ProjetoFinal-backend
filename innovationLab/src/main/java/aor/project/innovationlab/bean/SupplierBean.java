package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.SupplierDao;
import aor.project.innovationlab.entity.SupplierEntity;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;

@Stateless
public class SupplierBean {

    @EJB
    SupplierDao supplierDao;

    public void createInitialData() {
        createSupplierIfNotExists("Supplier 1", "123456789");
        createSupplierIfNotExists("Supplier 2", "987654321");
        createSupplierIfNotExists("Supplier 3", "123456789");
        createSupplierIfNotExists("Supplier 4", "987654321");
        createSupplierIfNotExists("Supplier 5", "123456789");
        createSupplierIfNotExists("Supplier 6", "987654321");
        createSupplierIfNotExists("Supplier 7", "123456789");
        createSupplierIfNotExists("Supplier 8", "987654321");
        createSupplierIfNotExists("Supplier 9", "123456789");
        createSupplierIfNotExists("Supplier 10", "987654321");
        System.out.println("Initial suppliers created");
    }

    public void createSupplierIfNotExists(String name, String phone) {
        if(supplierDao.findSupplierByName(name) == null) {
            SupplierEntity supplierEntity = new SupplierEntity();
            supplierEntity.setName(name);
            supplierEntity.setPhone(phone);
            supplierDao.persist(supplierEntity);
        }
    }
}
