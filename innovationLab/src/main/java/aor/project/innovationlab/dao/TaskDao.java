package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.TaskEntity;
import aor.project.innovationlab.enums.TaskStatus;
import aor.project.innovationlab.utils.logs.LoggerUtil;
import jakarta.ejb.Stateless;

import java.util.List;

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

    public List<TaskEntity> findTasksByProjectId(Long projectId) {
        try {
            return em.createNamedQuery("Task.findTasksByProjectId").setParameter("projectId", projectId)
                    .getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public TaskEntity findTaskBySystemTitle(String uniqueSystemTitle) {
        try {
            return (TaskEntity) em.createNamedQuery("Task.findTaskBySystemTitle").setParameter("systemTitle", uniqueSystemTitle)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public TaskEntity findTaskByProjectIdAndStatus(long projectId, TaskStatus taskStatus) {
        try {
            return (TaskEntity) em.createNamedQuery("Task.findTaskByProjectIdAndStatus").setParameter("projectId", projectId)
                    .setParameter("status", taskStatus)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<TaskEntity> findTasksByProjectIdAndTitle(long id, String presentationOfTheProject) {
        try {
            return em.createNamedQuery("Task.findTasksByProjectIdAndTitle").setParameter("projectId", id)
                    .setParameter("title", presentationOfTheProject)
                    .getResultList();
        } catch (Exception e) {
            return null;
        }
    }

//    public TaskEntity findLastTaskByProjectId(long projectId) {
//        try {
//            return (TaskEntity) em.createNamedQuery("Task.findLastTaskByProjectId")
//                    .setParameter("projectId", projectId)
//                    .setMaxResults(1)
//                    .getSingleResult();
//        } catch (Exception e) {
//            return null;
//        }
//    }

    public TaskEntity findLastTaskByProjectIdExcludingPresentation(long projectId) {
        try {
            return (TaskEntity) em.createNamedQuery("Task.findLastTaskByProjectIdExcludingPresentation")
                    .setParameter("projectId", projectId)
                    .setParameter("presentationStatus", TaskStatus.PRESENTATION)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (Exception e) {
            LoggerUtil.logError("Error finding last task by project ID excluding presentation", e.getMessage(), null, null);
            return null;
        }
    }

    public List<TaskEntity> findTasksByProjectIdNoPresentation(Long projectId) {
        try {
            return em.createNamedQuery("Task.findTasksByProjectIdNoPresentation").setParameter("projectId", projectId)
                    .setParameter("presentationStatus", TaskStatus.PRESENTATION)
                    .getResultList();
        } catch (Exception e) {
            LoggerUtil.logError("Error finding tasks by project ID no presentation", e.getMessage(), null, null);
            return null;
        }
    }

    public List<TaskEntity> findTasksByProjectIdAndUserId(Long projectId, Long id) {
        try {
            return em.createNamedQuery("Task.findTasksByProjectIdAndUserId").setParameter("projectId", projectId)
                    .setParameter("userId", id)
                    .getResultList();
        } catch (Exception e) {
            return null;
        }

    }
}
