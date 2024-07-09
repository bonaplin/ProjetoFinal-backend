package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.dto.product.ProductsList;
import aor.project.innovationlab.dto.response.IdNameDto;
import aor.project.innovationlab.dto.response.PaginatedResponse;
import aor.project.innovationlab.dto.product.ProductDto;
import aor.project.innovationlab.dto.product.ProductToCreateProjectDto;
import aor.project.innovationlab.dto.product.filter.FilterOptionsProductDto;
import aor.project.innovationlab.entity.ProductEntity;
import aor.project.innovationlab.entity.SessionEntity;
import aor.project.innovationlab.entity.SupplierEntity;
import aor.project.innovationlab.dto.project.filter.FilterOptionsDto;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.ProductStatus;
import aor.project.innovationlab.enums.ProductType;
import aor.project.innovationlab.enums.UserType;
import aor.project.innovationlab.utils.logs.LoggerUtil;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

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
    @EJB
    ProjectDao projectDao;

    @EJB
    ProjectUserDao projectUserDao;

    @EJB
    ProjectProductDao ProjectProductDao;

    @Inject
    InterestBean interestBean;

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
        productDto.setDescription(productEntity.getDescription());
        productDto.setNotes(productEntity.getNotes());
        return productDto;
    }

    public ProductToCreateProjectDto toProjectInfoDto(ProjectProductEntity productEntity) {
        ProductToCreateProjectDto productDto = new ProductToCreateProjectDto();
        productDto.setId(productEntity.getId());
        productDto.setName(productEntity.getProduct().getName());
        productDto.setQuantity(productEntity.getQuantity());
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
        productEntity.setNotes(productDto.getNotes());
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


    public PaginatedResponse<Object> getProducts(String auth, String dtoType, String name, List<String> types, List<String> brands, String supplierName, String identifier, Integer pageNumber, Integer pageSize, String orderField, String orderDirection, Long id){
        String log = "Attempt to get products";
        SessionEntity sessionEntity = sessionDao.findSessionByToken(auth);
        if(sessionEntity == null) {
            LoggerUtil.logError(log, "Unauthorized access", null, auth);
        }

        Long supplierId = null;
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
            for (String type : types) {
                if (ProductType.contains(type.toUpperCase())) {
                    typeEnums.add(ProductType.valueOf(type.toUpperCase()));
                } else {
                    LoggerUtil.logError(log, "Invalid product type", null, auth);
                }
            }
        }

        if(orderDirection != null && orderField !=null && !orderDirection.isEmpty() && !orderField.isEmpty()){
            orderDirection = orderDirection.toLowerCase();
            orderField = orderField.toLowerCase();
            validateOrderParameters(orderField, orderDirection, auth);
        } else {
            orderField = null;
            orderDirection = null;
        }

        if(pageNumber == null || pageNumber < 0){
            pageNumber = 1;
        }
        if(pageSize == null || pageSize < 0){
            pageSize = 10;
        }

        PaginatedResponse<ProductEntity> productResponse = productDao.findProducts(supplierId, brands, null, identifier, name, typeEnums, pageNumber, pageSize, orderField,orderDirection, id);
        List<ProductEntity> products = productResponse.getResults();

        PaginatedResponse<Object> response = new PaginatedResponse<>();
        response.setUserType(sessionEntity.getUser().getRole().getValue());
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
    
    public void addProductsToProject (String token, long projectId, ProductsList products) {

        String log = "Attempting to remove interest from project";

        SessionEntity sessionEntity = sessionDao.findSessionByToken(token);

        if(sessionEntity == null){
            LoggerUtil.logError(log,"Session not found.",null,token);
            throw new IllegalArgumentException("Session not found.");
        }

        ProjectEntity project = projectDao.findProjectById(projectId);

        if(project == null) {
            LoggerUtil.logError(log,"Project not found",null,token);
            throw new IllegalArgumentException("Project not found");
        }

        UserEntity user = sessionEntity.getUser();

        if (interestBean.checkProjectStatus(project))  {
            LoggerUtil.logError(log,"Current project status doesnt allow editions",null,token);
            throw new IllegalArgumentException("This project is in a status that doesnt allow editions");
        }

        ProjectUserEntity projectUser = projectUserDao.findProjectUserByProjectIdAndUserId(projectId, user.getId());
        if(projectUser == null) {
            LoggerUtil.logError(log,"User not part of the project with id number: " + projectId,user.getEmail(),token);
            throw new IllegalArgumentException("User dont have permissions to interact with this project");
        }

        if (projectUser.getRole() != UserType.MANAGER) {
            LoggerUtil.logError(log,"User dont have permissions to interact with project " + projectId,user.getEmail(),token);
            throw new IllegalArgumentException("User dont have permissions to interact with this project");
        }

        for(ProductToCreateProjectDto product : products.getProducts()) {

            ProductEntity productEntity = productDao.findProductById(product.getId());

            ProjectProductEntity projectProduct = ProjectProductDao.findProductInProjectById(project, productEntity);

            if (projectProduct == null) {
                ProjectProductEntity projectProductEntity = new ProjectProductEntity();
                projectProductEntity.setProject(project);
                projectProductEntity.setProduct(productEntity);
                projectProductEntity.setQuantity(product.getQuantity());
                projectProductEntity.setStatus(ProductStatus.STOCK);
                ProjectProductDao.persist(projectProductEntity);
            } else {
                projectProduct.setQuantity(projectProduct.getQuantity() + product.getQuantity());
                ProjectProductDao.merge(projectProduct);
            }
        }
    }

    private ProductEntity convertToProductEntity (ProductToCreateProjectDto product, String log) {
        ProductEntity productEntity = productDao.findProductById(product.getId());
        if(productEntity == null) {
            LoggerUtil.logError(log,"Product not found",null,null);
            throw new IllegalArgumentException("Product not found");
        }
        return productEntity;
    }


    public List<ProductToCreateProjectDto> getProjectProducts (String token, long projectId) {

        String log = "Attempt to get products for project info";
        SessionEntity sessionEntity = sessionDao.findSessionByToken(token);
        if(sessionEntity == null){
            LoggerUtil.logError(log,"Session not found.",null,token);
            throw new IllegalArgumentException("Session not found.");
        }

        List<ProjectProductEntity> products = ProjectProductDao.findProductsByProjectId(projectId);
        if(products == null) {
            return new ArrayList<>();
        }
        return products.stream().filter(ProjectProductEntity::isActive).map(this::toProjectInfoDto).collect(Collectors.toList());
    }

    private void validateOrderParameters(String orderField, String orderDirection, String auth) {
        String log= "Attempt to get products";
        List<String> allowedFields = List.of("name", "brand", "type", "supplier", "identifier" );

        if(orderField != null && !allowedFields.contains(orderField) &&!orderField.isEmpty()){
            LoggerUtil.logError(log, "Invalid order field", null, auth);
            throw new IllegalArgumentException("Invalid order field");
        }

        if(orderDirection != null && !List.of("asc", "desc").contains(orderDirection) && !orderDirection.isEmpty()){
            LoggerUtil.logError(log, "Invalid order direction", null, auth);
            throw new IllegalArgumentException("Invalid order direction");
        }
    }


    public Object updateProduct(Long id, ProductDto dto, String token) {
        String log = "Attempt to update product";
        SessionEntity sessionEntity = sessionDao.findSessionByToken(token);
        if(sessionEntity == null) {
            LoggerUtil.logError(log, "Unauthorized access", null, token);
        }
        sessionBean.isAdmin(token);

        ProductEntity productEntity = productDao.findProductById(id);

        if(productEntity == null) {
            LoggerUtil.logError(log, "Product not found", null, token);
        }
        if(dto.getBrand() != null && !dto.getBrand().isEmpty()) {
            productEntity.setBrand(dto.getBrand());
        }
        if(dto.getDescription() != null && !dto.getDescription().isEmpty()) {
            productEntity.setDescription(dto.getDescription());
        }
        if(dto.getSupplier() != null && !dto.getSupplier().isEmpty()) {
            SupplierEntity se = supplierDao.findSupplierByName(dto.getSupplier());
            if(se == null) {
                LoggerUtil.logError(log, "Supplier not found", null, token);
                throw new IllegalArgumentException("Supplier not found");
            }else{
                productEntity.setSupplier(se);
            }
        }
        if(dto.getNotes() != null && !dto.getNotes().isEmpty()) {
            productEntity.setNotes(dto.getNotes());
        }
        if(dto.getQuantity() != 0 && dto.getQuantity() != productEntity.getQuantity() && dto.getQuantity() > 0){
            productEntity.setQuantity(dto.getQuantity());
        }

        productDao.merge(productEntity);

        return toDto(productEntity);

    }

    public Object disableProduct(Long id, String token) {
        String log = "Attempt to disable product";
        SessionEntity sessionEntity = sessionDao.findSessionByToken(token);
        if(sessionEntity == null) {
            LoggerUtil.logError(log, "Unauthorized access", null, token);
            throw new IllegalArgumentException("Unauthorized access");
        }

        sessionBean.isAdmin(token);

        ProductEntity productEntity = productDao.findProductById(id);

        if(productEntity == null) {
            LoggerUtil.logError(log, "Product not found", null, token);
            throw new IllegalArgumentException("Product not found");
        }

        productEntity.setQuantity(0);
        productDao.merge(productEntity);

        return toDto(productEntity);
    }

    public Object createProduct(ProductDto dto, String token) {
        SessionEntity se = sessionDao.findSessionByToken(token);
        if(se == null) {
            LoggerUtil.logError("Attempt to create product", "Unauthorized access", null, token);
            throw new IllegalArgumentException("Unauthorized access");
        }

        sessionBean.isAdmin(token);
        if(dto == null) {
            LoggerUtil.logError("Attempt to create product", "Product is required", null, token);
            throw new IllegalArgumentException("Product is required");
        }
        if(dto.getName() == null || dto.getName().isEmpty()) {
            LoggerUtil.logError("Attempt to create product", "Name is required", null, token);
            throw new IllegalArgumentException("Name is required");
        }

        if(productDao.findProductByName(dto.getName()) != null) {
            LoggerUtil.logError("Attempt to create product", "Product already exists", null, token);
            throw new IllegalArgumentException("Product already exists");
        }

        if(dto.getBrand() == null || dto.getBrand().isEmpty()) {
            LoggerUtil.logError("Attempt to create product", "Brand is required", null, token);
            throw new IllegalArgumentException("Brand is required");
        }

        if(dto.getDescription() == null || dto.getDescription().isEmpty()) {
            LoggerUtil.logError("Attempt to create product", "Description is required", null, token);
            throw new IllegalArgumentException("Description is required");
        }

        if(dto.getType() == null || dto.getType().isEmpty()) {
            LoggerUtil.logError("Attempt to create product", "Type is required", null, token);
            throw new IllegalArgumentException("Type is required");
        }

        if(dto.getSupplier() == null || dto.getSupplier().isEmpty()) {
            LoggerUtil.logError("Attempt to create product", "Supplier is required", null, token);
            throw new IllegalArgumentException("Supplier is required");
        }

        if(dto.getQuantity() <= 0) {
            LoggerUtil.logError("Attempt to create product", "Quantity is required", null, token);
            throw new IllegalArgumentException("Quantity is required");
        }

        if(dto.getIdentifier() == null || dto.getIdentifier().isEmpty()) {
            LoggerUtil.logError("Attempt to create product", "Identifier is required", null, token);
            throw new IllegalArgumentException("Identifier is required");
        }

        if(productDao.findProductByIdentifier(dto.getIdentifier()) != null) {
            LoggerUtil.logError("Attempt to create product", "Identifier already exists", null, token);
            throw new IllegalArgumentException("Identifier already exists");
        }

        if(dto.getNotes() == null || dto.getNotes().isEmpty()) {
            LoggerUtil.logError("Attempt to create product", "Notes is required", null, token);
            throw new IllegalArgumentException("Notes is required");
        }

        ProductEntity productEntity = toentity(dto);
        productDao.persist(productEntity);
        return toDto(productEntity);
    }
}