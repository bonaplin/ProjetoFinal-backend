package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.ProductDao;
import aor.project.innovationlab.dao.SessionDao;
import aor.project.innovationlab.dao.SupplierDao;
import aor.project.innovationlab.dto.IdNameDto;
import aor.project.innovationlab.dto.PaginatedResponse;
import aor.project.innovationlab.dto.product.ProductDto;
import aor.project.innovationlab.dto.product.filter.FilterOptionsProductDto;
import aor.project.innovationlab.dto.project.filter.FilterOptionsDto;
import aor.project.innovationlab.entity.ProductEntity;
import aor.project.innovationlab.entity.SessionEntity;
import aor.project.innovationlab.entity.SupplierEntity;
import aor.project.innovationlab.enums.ProductType;
import aor.project.innovationlab.enums.ProjectStatus;
import aor.project.innovationlab.enums.ProjectUserType;
import aor.project.innovationlab.utils.logs.LoggerUtil;
import com.sun.tools.jconsole.JConsoleContext;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Stateless
public class ProductBean {

    @EJB
    ProductDao productDao;

    @EJB
    SupplierDao supplierDao;

    @EJB
    SessionDao sessionDao;

    @Inject
    SessionBean sessionBean;

    public ProductDto toDto(ProductEntity productEntity) {
        ProductDto productDto = new ProductDto();
        productDto.setId(productEntity.getId());
        productDto.setName(productEntity.getName());
        productDto.setBrand(productEntity.getBrand());
        productDto.setDescription(productEntity.getDescription());
        productDto.setType(productEntity.getType().name());
        productDto.setSupplier(productEntity.getSupplier().getName());
        productDto.setSupplierPhone(productEntity.getSupplier().getPhone());
        productDto.setQuantity(productEntity.getQuantity());
        productDto.setIdentifier(productEntity.getIdentifier());
        return productDto;
    }

    public ProductEntity toentity(ProductDto productDto) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setName(productDto.getName());
        productEntity.setBrand(productDto.getBrand());
        productEntity.setDescription(productDto.getDescription());
        productEntity.setType(ProductType.valueOf(productDto.getType()));
        productEntity.setSupplier(supplierDao.findSupplierByName(productDto.getSupplier()));
        productEntity.setQuantity(productDto.getQuantity());
        productEntity.setIdentifier(productDto.getIdentifier());
        return productEntity;
    }

    public void createInitialData() {
        createProductIfNotExists("Product 1", ProductType.COMPONENT, "Supplier 1","123456789");
        createProductIfNotExists("Product 2", ProductType.COMPONENT, "Supplier 2","987654321");
        createProductIfNotExists("Product 3", ProductType.RESOURCE, "Supplier 1","123456788");
        createProductIfNotExists("Product 4", ProductType.RESOURCE, "Supplier 3", "123456787");
        createProductIfNotExists("Product 5", ProductType.RESOURCE, "Supplier 1", "123456786");
        createProductIfNotExists("Product 6", ProductType.COMPONENT, "Supplier 5", "123456785");
        createProductIfNotExists("Product 7", ProductType.COMPONENT, "Supplier 6", "123456784");
    }

    public void createProductIfNotExists(String name, ProductType type, String supplierName, String identifier) {
        if(productDao.findProductByName(name) == null) {
            ProductEntity productEntity = new ProductEntity();
            productEntity.setName(name);
            productEntity.setDescription("Description 1");
            productEntity.setType(type);
            productEntity.setSupplier(supplierDao.findSupplierByName(supplierName));
            productEntity.setQuantity(10);
            productEntity.setBrand("Brand 1");
            productEntity.setIdentifier(identifier);
            productDao.persist(productEntity);
        }

    }

    public FilterOptionsProductDto filterOptions(String token) {
        sessionBean.validateUserToken(token);
        FilterOptionsProductDto filterOptionsDto = new FilterOptionsProductDto();

        List<IdNameDto> types = productDao.findProductTypes().stream()
                .map(type -> new IdNameDto(type.getId(), type.name()))
                .collect(Collectors.toList());

        // Brand ids are generated in order to be used in the frontend
        AtomicInteger brandId = new AtomicInteger(1);
        List<IdNameDto> brands = productDao.findProductBrands().stream()
                .map(brand -> new IdNameDto(brandId.getAndIncrement(), brand))
                .collect(Collectors.toList());

        filterOptionsDto.setTypes(types);
        filterOptionsDto.setBrands(brands);
        return filterOptionsDto;
    }


    public PaginatedResponse<Object> getProducts(String auth, String dtoType, String name, List<String> types, List<String> brands, String supplierName, String identifier, Integer pageNumber, Integer pageSize) {
        String log = "Attempt to get products";
        SessionEntity sessionEntity = sessionDao.findSessionByToken(auth);
        if(sessionEntity == null) {
            LoggerUtil.logError(log, "Unauthorized access", null, auth);
        }

        Long supplierId = null;
        System.out.println(supplierName);
        if(supplierName != null) {
            SupplierEntity supplier = supplierDao.findSupplierByName(supplierName.toUpperCase());
            if(supplier == null) {
                LoggerUtil.logError(log, "Supplier not found", null, auth);
            } else {
                supplierId = supplier.getId();
            }
        }

        List<ProductType> typeEnums = new ArrayList<>();
        if (types != null) {
            System.out.println("os types são: " + types);
            for (String type : types) {
                System.out.println("type:" + type);
                if (ProductType.contains(type.toUpperCase())) {
                    System.out.println("tem este:" + type);
                    typeEnums.add(ProductType.valueOf(type.toUpperCase()));
                } else {
                    LoggerUtil.logError(log, "Invalid product type", null, auth);
                }
            }
        }

        if(pageNumber == null || pageNumber < 0){
            pageNumber = 1;
        }
        if(pageSize == null || pageSize < 0){
            pageSize = 10;
        }

        PaginatedResponse<ProductEntity> productResponse = productDao.findProducts(supplierId, brands, null, identifier, name, typeEnums, pageNumber, pageSize);
        List<ProductEntity> products = productResponse.getResults();

        PaginatedResponse<Object> response = new PaginatedResponse<>();
        response.setTotalPages(productResponse.getTotalPages());

        if(dtoType == null || dtoType.isEmpty()){
            dtoType = "ProductDto";
        }

        switch (dtoType) {
            case "ProductDto":
                response.setResults(products.stream().map(this::toDto).collect(Collectors.toList()));
                break;
            default:
                response.setResults(new ArrayList<>());
                break;
        }
        return response;

    }


}