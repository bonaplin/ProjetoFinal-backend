package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.TaskExecutorAdditionalEntity;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class TaskExecutorAdditionalDao extends AbstractDao<TaskExecutorAdditionalEntity>{

    private static final long serialVersionUID = 1L;

    public TaskExecutorAdditionalDao() {
        super(TaskExecutorAdditionalEntity.class);
    }

    public TaskExecutorAdditionalEntity findTaskExecutorAdditionalByTaskIdAndExecutorNAme(Long taskId, String executorName) {
        try {
            return (TaskExecutorAdditionalEntity) em.createNamedQuery("TaskExecutorAdditional.findTaskExecutorAdditionalByTaskIdAndExecutorName")
                    .setParameter("taskId", taskId)
                    .setParameter("executorName", executorName)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }


    public List<TaskExecutorAdditionalEntity> findTaskExecutorAdditionalByTaskId(Long id){
        return em.createNamedQuery("TaskExecutorAdditional.findTaskExecutorAdditionalByTaskId")
                .setParameter("taskId", id)
                .getResultList();
    }

    public List<TaskExecutorAdditionalEntity> findActiveTaskExecutorAdditionalByTaskId(Long id) {
        return em.createNamedQuery("TaskExecutorAdditional.findActiveTaskExecutorAdditionalByTaskId")
                .setParameter("taskId", id)
                .getResultList();
    }
}
