package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.NotificationDao;
import aor.project.innovationlab.dao.ProjectDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.entity.NotificationEntity;
import aor.project.innovationlab.entity.ProjectEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.enums.NotificationType;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;

@Stateless
public class NotificationBean {

    @EJB
    NotificationDao notificationDao;

    @EJB
    UserDao userDao;

    @EJB
    ProjectDao projectDao;

    public NotificationBean() {
    }

    public void sendNotification(String senderEmail, String receiverEmail, String content,NotificationType type, String projectName) {
        UserEntity sender = userDao.findUserByEmail(senderEmail);
        UserEntity receiver = userDao.findUserByEmail(receiverEmail);

        if(sender == null || receiver == null) {
            return;
        }

        NotificationEntity notification = new NotificationEntity();
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setContent(content);
        notification.setType(type);
        if(projectName != null) {
            ProjectEntity project = projectDao.findProjectByName(projectName);

            if(project == null) {
                return;
            }

            notification.setProject(project);
        }
        notificationDao.persist(notification);
    }
}
