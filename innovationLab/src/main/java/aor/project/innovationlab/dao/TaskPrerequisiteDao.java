package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.TaskPrerequisiteEntity;
import jakarta.ejb.Stateless;

@Stateless
public class TaskPrerequisiteDao extends AbstractDao<TaskPrerequisiteEntity> {

    private static final long serialVersionUID = 1L;

    public TaskPrerequisiteDao() {
        super(TaskPrerequisiteEntity.class);
    }

    public TaskPrerequisiteEntity findPrerequisiteTaskById(long id) {
        try {
            return (TaskPrerequisiteEntity) em.createNamedQuery("PrerequisiteTask.findPrerequisiteTaskById").setParameter("id", id)
                    .getSingleResult();

        } catch (Exception e) {
            return null;
        }
    }

    public TaskPrerequisiteEntity findPrequisiteTaskByTaskId(long taskId) {
        try {
            return (TaskPrerequisiteEntity) em.createNamedQuery("PrerequisiteTask.findPrerequisiteTaskByTaskId").setParameter("taskId", taskId)
                    .getSingleResult();

        } catch (Exception e) {
            return null;
        }
    }
}
