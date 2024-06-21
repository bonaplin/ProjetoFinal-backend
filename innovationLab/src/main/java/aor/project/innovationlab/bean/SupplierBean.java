package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.SupplierDao;
import aor.project.innovationlab.dto.supplier.SupplierDto;
import aor.project.innovationlab.entity.SessionEntity;
import aor.project.innovationlab.entity.SupplierEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.enums.UserType;
import aor.project.innovationlab.utils.logs.LoggerUtil;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class SupplierBean {

    @EJB
    SupplierDao supplierDao;

    @Inject
    private SessionBean sessionBean;

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

    public SupplierDto toDto( SupplierEntity supplierEntity) {
        SupplierDto supplierDto = new SupplierDto();
        supplierDto.setId(supplierEntity.getId());
        supplierDto.setName(supplierEntity.getName());
        supplierDto.setPhone(supplierEntity.getPhone());
        return supplierDto;

    }

    public List<SupplierDto> getAllSuppliers(String dtoType, String token) {
        String log = "Attempting to get all suppliers";
        UserEntity userEntity = sessionBean.getUserByToken(token);
        if(userEntity == null) {
            LoggerUtil.logError(log, "User not found",null,token);
            return null;
        }
        String dto ="";
        if(dtoType == null || dtoType.isEmpty()) {
            dto = "SupplierDto";
        }
        else if(dtoType.equalsIgnoreCase("SupplierDto")) {
            dto = "SupplierDto";
        }
//        if(!userEntity.getRole().equals(UserType.ADMIN)) {
//            LoggerUtil.logError(log, "User is not an admin",null,token);
//            throw new RuntimeException("User dont have permission to access this resource");
//        }
        List<SupplierEntity> supplierEntities = supplierDao.findAll();
        return supplierEntities.stream().map(this::toDto).collect(Collectors.toList());
    }
}
