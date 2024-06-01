package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.ProjectSkillEntity;
import jakarta.ejb.Stateless;

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
}
