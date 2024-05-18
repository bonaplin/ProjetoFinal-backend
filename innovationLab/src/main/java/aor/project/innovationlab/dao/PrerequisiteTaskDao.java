package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.PrerequisiteTaskEntity;
import jakarta.ejb.Stateless;

@Stateless
public class PrerequisiteTaskDao extends AbstractDao<PrerequisiteTaskEntity> {

    private static final long serialVersionUID = 1L;

    public PrerequisiteTaskDao() {
        super(PrerequisiteTaskEntity.class);
    }

    public PrerequisiteTaskEntity findPrerequisiteTaskById(long id) {
        try {
            return (PrerequisiteTaskEntity) em.createNamedQuery("PrerequisiteTask.findPrerequisiteTaskById").setParameter("id", id)
                    .getSingleResult();

        } catch (Exception e) {
            return null;
        }
    }
}
