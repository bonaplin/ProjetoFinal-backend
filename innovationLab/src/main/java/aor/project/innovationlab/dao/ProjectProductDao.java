package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.InterestEntity;
import aor.project.innovationlab.entity.ProductEntity;
import aor.project.innovationlab.entity.ProjectInterestEntity;
import aor.project.innovationlab.entity.ProjectProductEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

import java.util.List;

@Stateless
public class ProjectProductDao extends AbstractDao<ProjectProductEntity> {

    private static final long serialVersionUID = 1L;

    public ProjectProductDao() {
        super(ProjectProductEntity.class);
    }

    public ProjectProductEntity findProjectProductIds(long projectid, long productid) {
        try {
            return (ProjectProductEntity) em.createNamedQuery("ProjectProduct.findProjectProductIds").setParameter("projectid", projectid).setParameter("productid", productid)
                    .getSingleResult();

        } catch (Exception e) {
            return null;
        }
    }

    public List<ProductEntity> findProductsByProjectId(long projectId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProductEntity> cq = cb.createQuery(ProductEntity.class);
        Root<ProjectProductEntity> root = cq.from(ProjectProductEntity.class);

        Join<ProjectProductEntity, ProductEntity> userJoin = root.join("product");

        cq.where(cb.equal(root.get("project").get("id"), projectId));

        cq.select(userJoin);

        List<ProductEntity> products = em.createQuery(cq).getResultList();

        return products;
    }
}
