package aor.project.innovationlab.bean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import aor.project.innovationlab.dao.SupplierDao;
import aor.project.innovationlab.dto.supplier.SupplierDto;
import aor.project.innovationlab.entity.SessionEntity;
import aor.project.innovationlab.entity.SupplierEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.service.SupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class SupplierBeanTest {

    @Mock
    private SupplierDao supplierDao;

    @InjectMocks
    private SupplierBean supplierService;

    @InjectMocks
    private SessionBean sessionBean;

    @BeforeEach
    void setUp() {
        // Configuração inicial, se necessário
    }

    @Test
    void testCreateSupplierIfNotExists_SupplierDoesNotExist() {
        // Arrange
        String name = "SupplierName";
        String phone = "1234567890";

        when(supplierDao.findSupplierByName(name)).thenReturn(null);

        // Act
        supplierService.createSupplierIfNotExists(name, phone);

        // Assert
        verify(supplierDao, times(1)).persist(any(SupplierEntity.class));
    }

    @Test
    void testCreateSupplierIfNotExists_SupplierExists() {
        // Arrange
        String name = "SupplierName";
        String phone = "1234567890";

        SupplierEntity existingSupplier = new SupplierEntity();
        existingSupplier.setName(name);
        existingSupplier.setPhone(phone);

        when(supplierDao.findSupplierByName(name)).thenReturn(existingSupplier);

        // Act
        supplierService.createSupplierIfNotExists(name, phone);

        // Assert
        verify(supplierDao, never()).persist(any(SupplierEntity.class));
    }

    @Test
    void testCreateSupplierIfNotExists_SupplierDoesNotExists() {
        // Arrange
        String name = "SupplierName";
        String phone = "1234567890";

        when(supplierDao.findSupplierByName(name)).thenReturn(null);

        // Act
        supplierService.createSupplierIfNotExists(name, phone);

        // Assert
        verify(supplierDao, times(1)).persist(any(SupplierEntity.class));
    }

    @Test
    void testCreateSupplierIfNotExists_AndSupplierExists() {
        // Arrange
        String name = "SupplierName";
        String phone = "1234567890";

        SupplierEntity existingSupplier = new SupplierEntity();
        existingSupplier.setName(name);
        existingSupplier.setPhone(phone);

        when(supplierDao.findSupplierByName(name)).thenReturn(existingSupplier);

        // Act
        supplierService.createSupplierIfNotExists(name, phone);

        // Assert
        verify(supplierDao, never()).persist(any(SupplierEntity.class));
    }


}
