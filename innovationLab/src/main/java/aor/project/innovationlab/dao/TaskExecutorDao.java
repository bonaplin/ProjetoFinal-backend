package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.TaskExecutorEntity;
import jakarta.ejb.Stateless;

import java.util.List;

@Stateless
public class TaskExecutorDao extends AbstractDao<TaskExecutorEntity> {

        private static final long serialVersionUID = 1L;

        public TaskExecutorDao() {
            super(TaskExecutorEntity.class);
        }


    public TaskExecutorEntity findTaskExecutorByTaskIdAndExecutorId(Long id, long id1) {
            try{
                return (TaskExecutorEntity) em.createNamedQuery("TaskExecutor.findTaskExecutorByTaskIdAndExecutorId")
                        .setParameter("taskId", id)
                        .setParameter("executorId", id1)
                        .getSingleResult();
            }
            catch (Exception e){
                return null;
            }
    }

    public List<TaskExecutorEntity> findActiveTaskExecutorByTaskId(Long id) {
            try{
                return em.createNamedQuery("TaskExecutor.findActiveTaskExecutorByTaskId")
                        .setParameter("taskId", id)
                        .getResultList();
            }
            catch (Exception e){
                return null;
            }

    }

    public List<TaskExecutorEntity> findTaskExecutorsByTaskId(Long id) {
            try{
                return em.createNamedQuery("TaskExecutor.findTaskExecutorsByTaskId")
                        .setParameter("taskId", id)
                        .getResultList();
            }
            catch (Exception e){
                return null;
            }
    }

    public List<TaskExecutorEntity> findTaskExecutorByProjectIdAndUserId(Long projectId, Long id) {
            try{
                return em.createNamedQuery("TaskExecutor.findTaskExecutorByProjectIdAndUserId")
                        .setParameter("projectId", projectId)
                        .setParameter("userId", id)
                        .getResultList();
            }
            catch (Exception e){
                return null;
            }
    }

    public boolean isActiveInAnyTask(long id) {
        try {
            Long count = em.createQuery("SELECT COUNT(te) FROM TaskExecutorEntity te WHERE te.executor.id = :userId AND te.active = true", Long.class)
                    .setParameter("userId", id)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }

}
