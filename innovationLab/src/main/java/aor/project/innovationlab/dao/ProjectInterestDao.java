package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.ProjectInterestEntity;
import jakarta.ejb.Stateless;

@Stateless
public class ProjectInterestDao extends AbstractDao<ProjectInterestEntity> {

    private static final long serialVersionUID = 1L;

    public ProjectInterestDao() {
        super(ProjectInterestEntity.class);
    }

    public ProjectInterestEntity findProjectInterestIds(long projectId, long interestId) {
        try {
            return (ProjectInterestEntity) em.createNamedQuery("ProjectInterest.findProjectInterestIds")
                    .setParameter("project", projectId)
                    .setParameter("interest", interestId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }


}
