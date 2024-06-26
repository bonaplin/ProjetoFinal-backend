package aor.project.innovationlab.dao;

import aor.project.innovationlab.dto.response.PaginatedResponse;
import aor.project.innovationlab.entity.ProductEntity;
import aor.project.innovationlab.enums.ProductType;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Stateless
public class ProductDao extends AbstractDao<ProductEntity> {

        private static final long serialVersionUID = 1L;

        public ProductDao() {
            super(ProductEntity.class);
        }

        public ProductEntity findProductByName(String name) {
            try {
                return (ProductEntity) em.createNamedQuery("Product.findProductByName").setParameter("name", name)
                        .getSingleResult();

            } catch (Exception e) {
                return null;
            }
        }

        public ProductEntity findProductById(long id) {
            try {
                return (ProductEntity) em.createNamedQuery("Product.findProductById").setParameter("id", id)
                        .getSingleResult();

            } catch (Exception e) {
                return null;
            }
        }

    public ProductEntity findProductByIdentifier(String productIdentifier) {
        try {
            return (ProductEntity) em.createNamedQuery("Product.findProductByIdentifier").setParameter("identifier", productIdentifier)
                    .getSingleResult();

        } catch (Exception e) {
            return null;
        }
    }

    public PaginatedResponse<ProductEntity> findProducts(Long supplierId,
                                                         List<String> brands,
                                                         String description,
                                                         String identifier,
                                                         String name,
                                                         List<ProductType> types,
                                                         Integer pageNumber,
                                                         Integer pageSize,
                                                         String orderField,
                                                         String orderDirection,
                                                         Long id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProductEntity> cq = cb.createQuery(ProductEntity.class);
        Root<ProductEntity> product = cq.from(ProductEntity.class);

        List<Predicate> predicates = createPredicates(cb, product, supplierId, brands, description, identifier, name, types, id);
        cq.where(predicates.toArray(new Predicate[0]));

        Order order = getOrder(cb, product, orderField, orderDirection);
        // Adicione a ordem à consulta
        if (order != null) {
            cq.orderBy(order);
        }

        TypedQuery<ProductEntity> query = em.createQuery(cq);
        query.setFirstResult((pageNumber - 1) * pageSize);
        query.setMaxResults(pageSize);

        List<ProductEntity> products = query.getResultList();

        // Construct the count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ProductEntity> countRoot = countQuery.from(ProductEntity.class);
        List<Predicate> countPredicates = createPredicates(cb, countRoot, supplierId, brands, description, identifier, name, types, id);

        countQuery.select(cb.count(countRoot));
        countQuery.where(countPredicates.toArray(new Predicate[0]));
        Long count = em.createQuery(countQuery).getSingleResult();
        int totalPages = (int) Math.ceil((double) count / pageSize);

        PaginatedResponse<ProductEntity> response = new PaginatedResponse<>();
        response.setResults(products);
        response.setTotalPages(totalPages);

        return response;
    }

    private List<Predicate> createPredicates(CriteriaBuilder cb, Root<ProductEntity> product, Long supplierId,
                                             List<String> brands, String description, String identifier,
                                             String name, List<ProductType> types, Long id) {
        List<Predicate> predicates = new ArrayList<>();

        if (supplierId != null) {
            predicates.add(cb.equal(product.get("supplier").get("id"), supplierId));
        }
        if (brands != null && !brands.isEmpty()) {
            predicates.add(product.get("brand").in(brands));
        }
        if (description != null) {
            predicates.add(cb.equal(product.get("description"), description));
        }
        if (identifier != null) {
            predicates.add(cb.equal(product.get("identifier"), identifier));
        }
        if (name != null) {
            predicates.add(cb.equal(product.get("name"), name));
        }
        if (types != null && !types.isEmpty()) {
            predicates.add(product.get("type").in(types));
        }
        if(id != null) {
            predicates.add(cb.equal(product.get("id"), id));
        }

        return predicates;
    }

    private Order getOrder(CriteriaBuilder cb, Root<ProductEntity> product, String orderField, String orderDirection) {
        if (orderField == null || orderDirection == null) {
            return null;
        }

        Path<Object> field = product.get(orderField);
        if (orderDirection.equalsIgnoreCase("ASC")) {
            return cb.asc(field);
        } else {
            return cb.desc(field);
        }

    }

    public List<ProductType> findProductTypes() {
        return Arrays.asList(ProductType.values());
    }

    public List<String> findProductBrands() {
        return em.createNamedQuery("Product.findAllBrands").getResultList();
    }
}
