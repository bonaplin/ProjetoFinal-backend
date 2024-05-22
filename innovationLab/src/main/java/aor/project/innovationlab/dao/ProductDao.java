package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.ProductEntity;
import jakarta.ejb.Stateless;

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

        public ProductEntity findProductById(int id) {
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
}
