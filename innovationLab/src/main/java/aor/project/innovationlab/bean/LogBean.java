package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.LogDao;
import aor.project.innovationlab.dao.ProjectDao;
import aor.project.innovationlab.dao.TaskDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.entity.LogEntity;
import aor.project.innovationlab.entity.ProjectEntity;
import aor.project.innovationlab.entity.TaskEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.enums.LogType;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class LogBean {

    @EJB
    private LogDao logDao;

    @EJB
    private ProjectDao projectDao;

    @EJB
    private UserDao userDao;

    @EJB
    private TaskDao taskDao;

    public LogBean() {
    }

    //TODO - Implementar métodos de log. Ex: changeTaskStatus, changeProjectStatus, etc.

    public LogEntity addNewUser(Long projectId, Long userId, Long affectedUserId, LogType type){
        LogEntity log = new LogEntity();
        ProjectEntity project = projectDao.findProjectById(projectId);
        UserEntity affectedUser = userDao.findUserById(affectedUserId);
        UserEntity user = userDao.findUserById(userId);

        if(project == null || affectedUser == null || user == null) {
            return null;
        }

        log.setType(type);
        log.setUser(user);
        log.setProject(project);

        log.setAffectedUser(affectedUser);
        logDao.persist(log);
        return log;
    }

    public LogEntity addNewTask(Long projectId, Long userId, Long taskId, LogType type){
        LogEntity log = new LogEntity();
        ProjectEntity project = projectDao.findProjectById(projectId);
        TaskEntity task = taskDao.findTaskById(taskId);
        UserEntity user = userDao.findUserById(userId);

        if(project == null || task == null || user == null) {
            return null;
        }

        log.setType(type);
        log.setUser(user);
        log.setProject(project);

        log.setTask(task);
        logDao.persist(log);
        return log;
    }

}
