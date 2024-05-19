package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.ProjectProductEntity;
import jakarta.ejb.Stateless;

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
}
