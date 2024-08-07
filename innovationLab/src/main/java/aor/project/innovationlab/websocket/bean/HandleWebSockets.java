package aor.project.innovationlab.websocket.bean;


import aor.project.innovationlab.bean.*;
import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.dto.message.MessageDto;
import aor.project.innovationlab.dto.notification.NotificationDto;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.NotificationType;
import aor.project.innovationlab.enums.UserType;
import aor.project.innovationlab.utils.Color;
import aor.project.innovationlab.utils.JsonUtils;
import com.google.gson.JsonObject;
import aor.project.innovationlab.gson.InstantAdapter;
import aor.project.innovationlab.utils.ws.MessageType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.tools.jconsole.JConsoleContext;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.websocket.Session;

import java.time.Instant;
import java.util.List;

@Stateless
public class HandleWebSockets {

    @EJB
    MessageBean messageBean;
    @EJB
    UserBean userBean;
    @EJB
    TaskBean taskBean;
    @EJB
    MessageDao messageDao;
    @EJB
    SessionDao sessionDao;
    @EJB
    ProjectDao projectDao;
    @EJB
    UserDao userDao;
    @EJB
    NotificationBean notificationBean;
    @EJB
    NotificationDao notificationDao;
    @EJB
    ProjectUserDao projectUserDao;
    @EJB
    WebSocketBean webSocketBean;

    public void handleWebSocketJSON(Session session, String json) {
        JsonObject jsonObject = JsonUtils.convertJsonStringToJsonObject(json);
        int typeValue = JsonUtils.getMessageTypeFromJson(json).getValue();
        NotificationType messageType = NotificationType.fromValue(typeValue);

        if (messageType == null) {
            throw new IllegalArgumentException("Invalid message type value: " + typeValue);
        }

        switch (messageType) {
            case PROJECT_MESSAGE:
                handleNewMessage(session, jsonObject);
                break;
            case PROJECT_CLOSE:
                handleProjectClose(session, jsonObject);
                break;
        }
    }

    private void handleProjectClose(Session session, JsonObject jsonObject){
        try {
            long id = jsonObject.get("id").getAsLong();
            String token = session.getPathParameters().get("token");
            ProjectEntity project = projectDao.findProjectById(id);
            if (project == null) return;

            ProjectUserEntity projectUser = projectDao.findProjectUserByProjectAndUserId(project.getId(), sessionDao.findSessionByToken(token).getUser().getId());
            if (projectUser == null) return;

            webSocketBean.closeProjectWindow(token, id);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


//    //MESSAGE -     - MESSAGE -     - MESSAGE -     - MESSAGE -     - MESSAGE
    private void handleNewMessage(Session session, JsonObject jsonObject) {
        try {
            String messageContent = jsonObject.get("message").getAsString();
            String userEmail = jsonObject.get("sendUser").getAsString();
            String projectId = jsonObject.get("projectId").getAsString();
            long id = Long.parseLong(projectId);

            String token = session.getPathParameters().get("token");

            String sender = sessionDao.findSessionByToken(token).getUser().getEmail();

            ProjectEntity project = projectDao.findProjectById(id);

            // Grava a mensagem na base de dados
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setMessage(messageContent);
            messageEntity.setUser(userDao.findUserByEmail(userEmail));
            messageEntity.setProject(project);
            messageDao.persist(messageEntity);

            MessageDto response = new MessageDto();
            response.setMessage(messageEntity.getMessage());
            response.setCreatedAt(messageEntity.getInstant());
            response.setUserEmail(messageEntity.getUser().getEmail());
            response.setId(messageEntity.getId());
            response.setUserFirstName(messageEntity.getUser().getFirstname());
            response.setProjectId(messageEntity.getProject().getId());

            String jsonWithAddedTypePM = WebSocketBean.addTypeToDtoJson(response, NotificationType.MESSAGE);

            // Verifica se os usuários ainda pertencem ao projeto e estão ativos antes de enviar notificações
            for (ProjectUserEntity projectUser : project.getProjectUsers()) {
                if (projectUser.isActive() && projectUser.getRole() != UserType.KICKED) {
                    System.out.println("Project User: " + projectUser.getUser().getEmail());
                    String email = projectUser.getUser().getEmail();
                    List<String> openProjectWindowTokens = webSocketBean.isProjectWindowOpen(email, id);
                    if (!openProjectWindowTokens.isEmpty()) {
                        System.out.println("Project Window is open");
                        for (String t : openProjectWindowTokens) {
                            webSocketBean.sendToUserToken(t, jsonWithAddedTypePM);
                        }
                    } else {
                        NotificationEntity notification = new NotificationEntity();
                        notification.setProject(project);
                        notification.setReceiver(projectUser.getUser());
                        notification.setSender(userDao.findUserByEmail(sender));
                        notification.setContent("New Message from " + sender + " at " + messageEntity.getInstant().toString());
                        notification.setNotificationType(NotificationType.PROJECT_MESSAGE);
                        notificationDao.persist(notification);
                        NotificationDto ndto = notificationBean.toDto(notification);
                        String jsonWithAddedTypeN = WebSocketBean.addTypeToDtoJson(ndto, NotificationType.NOTIFICATION);
                        webSocketBean.sendToUser(projectUser.getUser().getEmail(), jsonWithAddedTypeN);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
