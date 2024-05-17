package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.TaskEntity;
import jakarta.ejb.Stateless;

@Stateless
public class TaskDao extends AbstractDao<TaskEntity> {

    private static final long serialVersionUID = 1L;

    public TaskDao() {
        super(TaskEntity.class);
    }

    public TaskEntity findTaskByTitle(String title) {
        try {
            return (TaskEntity) em.createNamedQuery("Task.findTaskByTitle").setParameter("title", title)
                    .getSingleResult();

        } catch (Exception e) {
            return null;
        }
    }

    public TaskEntity findTaskById(long id) {
        try {
            return (TaskEntity) em.createNamedQuery("Task.findTaskById").setParameter("id", id)
                    .getSingleResult();

        } catch (Exception e) {
            return null;
        }
    }
}
