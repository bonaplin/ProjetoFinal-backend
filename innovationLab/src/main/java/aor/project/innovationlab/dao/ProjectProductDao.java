package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.*;
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

    public List<ProjectProductEntity> findProductsByProjectId(long projectId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProjectProductEntity> cq = cb.createQuery(ProjectProductEntity.class);
        Root<ProjectProductEntity> root = cq.from(ProjectProductEntity.class);

        // Perform the join operation
        Join<ProjectProductEntity, ProductEntity> productJoin = root.join("product");
        Join<ProjectProductEntity, ProjectEntity> projectJoin = root.join("project");

        // Set the where clause to filter by projectId
        cq.where(cb.equal(projectJoin.get("id"), projectId));

        // Set the select clause to return ProjectProductEntity objects
        cq.select(root);

        // Execute the query and get the result list
        List<ProjectProductEntity> projectProducts = em.createQuery(cq).getResultList();

        return projectProducts;
    }

    public ProjectProductEntity findProductInProjectById (ProjectEntity project, ProductEntity product) {
        try {
            return (ProjectProductEntity) em.createNamedQuery("ProjectProduct.findProductInProjectById")
                    .setParameter("project", project)
                    .setParameter("product", product)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}
