package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.ProjectStatus;
import jakarta.ejb.Stateless;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class ProjectDao extends AbstractDao<ProjectEntity> {

        private static final long serialVersionUID = 1L;

        public ProjectDao() {
            super(ProjectEntity.class);
        }

        public ProjectEntity findProjectByName(String name) {
            try {
                return (ProjectEntity) em.createNamedQuery("Project.findProjectByName").setParameter("name", name)
                        .getSingleResult();

            } catch (Exception e) {
                return null;
            }
        }

    public ProjectEntity findProjectById(long projectId) {
        try {
            return (ProjectEntity) em.createNamedQuery("Project.findProjectById").setParameter("projectId", projectId)
                    .getSingleResult();

        } catch (Exception e) {
            return null;
        }
    }

    public List<ProjectEntity> findProjects(String name, ProjectStatus status,
                                            Long labId, String creatorEmail,
                                            String skill, String interest, String participantEmail) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProjectEntity> cq = cb.createQuery(ProjectEntity.class);
        Root<ProjectEntity> project = cq.from(ProjectEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        if (name != null) {
            predicates.add(cb.equal(project.get("name"), name));
        }
        if (status != null) {
            predicates.add(cb.equal(project.get("status"), status));
        }
        if (labId != null) {
            predicates.add(cb.equal(project.get("lab").get("id"), labId));
        }
        if (creatorEmail != null) {
            predicates.add(cb.equal(project.get("creator").get("email"), creatorEmail));
        }
        if (skill != null) {
            Join<ProjectEntity, ProjectSkillEntity> skillJoin = project.join("projectSkills");
            predicates.add(cb.equal(skillJoin.get("skill").get("name"), skill));
        }
        if (interest != null) {
            Join<ProjectEntity, ProjectInterestEntity> interestJoin = project.join("projectInterests");
            predicates.add(cb.equal(interestJoin.get("interest").get("name"), interest));
        }
        if (participantEmail != null) {
            Join<ProjectEntity, ProjectUserEntity> userJoin = project.join("projectUsers");
            predicates.add(cb.equal(userJoin.get("user").get("email"), participantEmail));
        }
        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }

}
