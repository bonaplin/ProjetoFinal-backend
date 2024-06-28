package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.NotificationDao;
import aor.project.innovationlab.dao.ProjectDao;
import aor.project.innovationlab.dao.SessionDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.dto.PagAndUnreadResponse;
import aor.project.innovationlab.dto.notification.NotificationDto;
import aor.project.innovationlab.entity.NotificationEntity;
import aor.project.innovationlab.entity.ProjectEntity;
import aor.project.innovationlab.entity.SessionEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.enums.NotificationType;
import aor.project.innovationlab.utils.ws.MessageType;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Bean for sending notifications
 * @see NotificationEntity
 */
@Stateless
public class NotificationBean {

    @EJB
    NotificationDao notificationDao;

    @EJB
    UserDao userDao;

    @EJB
    ProjectDao projectDao;

    @EJB
    SessionDao sessionDao;

    @EJB
    private WebSocketBean webSocketBean;

    public NotificationBean() {
    }

    /**
     * Convert NotificationEntity to NotificationDto
     * @param ne
     * @return
     */
    private NotificationDto toDto ( NotificationEntity ne){
        NotificationDto dto = new NotificationDto();
        dto.setId(ne.getId());
        dto.setSenderEmail(ne.getSender().getEmail());
        dto.setReceiverEmail(ne.getReceiver().getEmail());
        dto.setContent(ne.getContent());
        dto.setInstant(ne.getInstant());
        dto.setRead(ne.isRead());
        dto.setNotificationType(ne.getNotificationType().getValue());
        dto.setSenderName(ne.getSender().getFirstname());
        dto.setSenderImg(ne.getSender().getProfileImagePath());

        if(ne.getProject() != null) {
            dto.setProjectId(ne.getProject().getId());
        }
        return dto;
    }

    /**
     * Send notification to user
     * @param senderEmail - email of sender
     * @param receiverEmail - email of receiver
     * @param content - content of notification
     * @param type - type of notification
     */
    public void sendNotification(String senderEmail, String receiverEmail, String content, NotificationType type, Long projectId) {
        UserEntity sender = userDao.findUserByEmail(senderEmail);
        UserEntity receiver = userDao.findUserByEmail(receiverEmail);

        if(sender == null || receiver == null) {
            return;
        }

        NotificationEntity notification = new NotificationEntity();
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setContent(content);
        notification.setNotificationType(type);
        if(projectId != null) {
            ProjectEntity project = projectDao.findProjectById(projectId);
            if(project == null) {
                return;
            }
            notification.setProject(project);
        }
        notificationDao.persist(notification);
        NotificationDto dto = toDto(notification);

        String notificationJson = webSocketBean.addTypeToDtoJson(dto, NotificationType.NOTIFICATION);
        webSocketBean.sendToUser(receiverEmail, notificationJson);
    }

    public PagAndUnreadResponse<Object> getAllNotifications(String token, Integer pageNumber, Integer pageSize) {
        SessionEntity session = sessionDao.findSessionByToken(token);

        if(session == null) {
            throw new IllegalArgumentException("Session not found");
        }
        String receiverEmail = session.getUser().getEmail();

        if(pageNumber == null || pageNumber <= 1) {
            pageNumber = 1;
        }
        if(pageSize == null || pageSize <= 1) {
            pageSize = 10;
        }
        PagAndUnreadResponse<NotificationEntity> notifications = notificationDao.findNotifications(receiverEmail, pageNumber, pageSize);

        PagAndUnreadResponse<Object> response = new PagAndUnreadResponse<>();
        response.setTotalPages(notifications.getTotalPages());
        response.setUnreadCount(notifications.getUnreadCount());
        response.setResults(notifications.getResults().stream().map(this::toDto).collect(Collectors.toList()));

        return response;
    }

    public NotificationDto markNotificationAsRead(String token, long id) {
        SessionEntity session = sessionDao.findSessionByToken(token);

        if(session == null) {
            throw new IllegalArgumentException("Session not found");
        }
        String receiverEmail = session.getUser().getEmail();

        NotificationEntity notification = notificationDao.findNotificationById(id);

        if(notification == null) {
            throw new IllegalArgumentException("Notification not found");
        }

        if(!notification.getReceiver().getEmail().equals(receiverEmail)) {
            throw new IllegalArgumentException("Notification not found");
        }

        notification.setRead(true);
        notificationDao.merge(notification);

        return toDto(notification);
    }
}
