package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.*;
import jakarta.ejb.Stateless;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

import java.util.List;

@Stateless
public class ProjectSkillDao extends AbstractDao<ProjectSkillEntity>{

    private static final long serialVersionUID = 1L;

    public ProjectSkillDao() {
        super(ProjectSkillEntity.class);
    }


    public ProjectSkillEntity findProjectSkillByProjectIdAndSkillId(long projectId, int skillId) {
        try {
            return (ProjectSkillEntity) em.createNamedQuery("ProjectSkill.findProjectSkillByProjectIdAndSkillId")
                    .setParameter("projectId", projectId)
                    .setParameter("skillId", skillId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public ProjectSkillEntity findSkillInProject(ProjectEntity project, SkillEntity skill) {
        try {
            return (ProjectSkillEntity) em.createNamedQuery("ProjectSkill.findSkillInProject")
                    .setParameter("project", project)
                    .setParameter("skill", skill)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<SkillEntity> findSkillsByProjectId(long projectId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SkillEntity> cq = cb.createQuery(SkillEntity.class);
        Root<ProjectSkillEntity> root = cq.from(ProjectSkillEntity.class);

        Join<ProjectSkillEntity, SkillEntity> userJoin = root.join("skill");

        cq.where(cb.equal(root.get("project").get("id"), projectId));

        cq.select(userJoin);

        List<SkillEntity> skills = em.createQuery(cq).getResultList();

        return skills;
    }

    public List<ProjectSkillEntity> findProjectSkillsByProjectId (long projectId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProjectSkillEntity> cq = cb.createQuery(ProjectSkillEntity.class);
        Root<ProjectSkillEntity> root = cq.from(ProjectSkillEntity.class);

        cq.where(cb.equal(root.get("project").get("id"), projectId));

        cq.select(root);

        List<ProjectSkillEntity> skills = em.createQuery(cq).getResultList();

        return skills;
    }
}
