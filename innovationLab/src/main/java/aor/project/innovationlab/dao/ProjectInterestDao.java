package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.InterestEntity;
import aor.project.innovationlab.entity.ProjectEntity;
import aor.project.innovationlab.entity.ProjectInterestEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

import java.util.List;

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

    public ProjectInterestEntity findInterestInProject(ProjectEntity project, InterestEntity interest) {
        try {
            return (ProjectInterestEntity) em.createNamedQuery("ProjectInterest.findInterestInProject")
                    .setParameter("project", project)
                    .setParameter("interest", interest)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<ProjectInterestEntity> findProjectInterestByProjectId(long projectId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProjectInterestEntity> cq = cb.createQuery(ProjectInterestEntity.class);
        Root<ProjectInterestEntity> root = cq.from(ProjectInterestEntity.class);

        cq.where(cb.equal(root.get("project").get("id"), projectId));

        cq.select(root);

        List<ProjectInterestEntity> projectInterests = em.createQuery(cq).getResultList();

        return projectInterests;
    }


}
