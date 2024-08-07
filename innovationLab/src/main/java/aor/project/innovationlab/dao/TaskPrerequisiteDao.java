package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.TaskPrerequisiteEntity;
import jakarta.ejb.Stateless;

import java.util.List;

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

    public List<TaskPrerequisiteEntity> findActiveTaskPrerequisiteByTaskId(Long id) {
        return em.createNamedQuery("PrerequisiteTask.findActiveTaskPrerequisiteByTaskId")
                .setParameter("taskId", id)
                .getResultList();
    }

    public List<TaskPrerequisiteEntity> findByTaskId(Long id) {
        try{
            return em.createNamedQuery("PrerequisiteTask.findByTaskId")
                    .setParameter("taskId", id)
                    .getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List<TaskPrerequisiteEntity> findByPrerequisiteTaskId(Long id) {
        try{
            return em.createNamedQuery("PrerequisiteTask.findByPrerequisiteTaskId")
                    .setParameter("prerequisiteId", id)
                    .getResultList();
        } catch (Exception e) {
            return null;
        }
    }
}
