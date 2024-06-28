package aor.project.innovationlab.websocket.bean;


import aor.project.innovationlab.bean.MessageBean;
import aor.project.innovationlab.bean.NotificationBean;
import aor.project.innovationlab.bean.TaskBean;
import aor.project.innovationlab.bean.UserBean;
import aor.project.innovationlab.dao.MessageDao;
import aor.project.innovationlab.dao.NotificationDao;
import aor.project.innovationlab.dao.SessionDao;
import aor.project.innovationlab.entity.MessageEntity;
import aor.project.innovationlab.entity.NotificationEntity;
import aor.project.innovationlab.utils.Color;
import aor.project.innovationlab.utils.JsonUtils;
import com.google.gson.JsonObject;
import aor.project.innovationlab.entity.SessionEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.gson.InstantAdapter;
import aor.project.innovationlab.utils.ws.MessageType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

import jakarta.websocket.Session;

import java.time.Instant;

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
    NotificationBean notificationBean;
    @EJB
    NotificationDao notificationDao;

//    Gson gson = new GsonBuilder()
//            .registerTypeAdapter(Instant.class, new InstantAdapter())
//            .create();

    public void handleWebSocketJSON(Session session, String json) {
        System.out.println(json);
        JsonObject jsonObject = JsonUtils.convertJsonStringToJsonObject(json);
        int typeValue = JsonUtils.getMessageTypeFromJson(json).getValue();
        System.out.println("Type value: " + typeValue);
        MessageType messageType = MessageType.fromValue(typeValue);

        switch (messageType) {
            case MESSAGE_RECEIVER:
//                handleNewMessage(session, jsonObject);
                System.out.println(Color.CYAN + "MESSAGE_RECEIVER" + Color.RESET);
                break;

            case EMAIL_INVITE_SEND:
                System.out.println(Color.CYAN + "EMAIL_SEND" + Color.RESET);
                break;
            case EMAIL_INVITE_RECEIVER:
                System.out.println(Color.CYAN + "EMAIL_RECEIVER" + Color.RESET);
                break;
                case EMAIL_RESPONSE_FROM:
                System.out.println(Color.CYAN + "EMAIL_RESPONSE_FROM" + Color.RESET);
                break;
            case EMAIL_RESPONSE_TO:
                System.out.println(Color.CYAN + "EMAIL_RESPONSE_TO" + Color.RESET);
                break;
            case EMAIL_SEND_TO:
                System.out.println(Color.CYAN + "EMAIL_SEND_TO" + Color.RESET);
                break;
            case EMAIL_SEND_FROM:
                System.out.println(Color.CYAN + "EMAIL_SEND_FROM" + Color.RESET);
                break;
            case EMAIL_DELETE:
                System.out.println(Color.CYAN + "EMAIL_DELETE" + Color.RESET);
                break;
        }
//            case TASK_MOVE:
//                taskBean.handleTaskMove(session, jsonObject);
//                break;
//            case MESSAGE_READ_CONFIRMATION:
//                handleReadConfirmation(session, jsonObject);
//                break;
//            default:
//                System.out.println("Unknown type");
//        }
    }


//    public boolean isUserOnline(String to) {
//        SessionEntity se = sessionDao.findSessionByEmail(to);
//        if(se == null) return false;
//
//        //encontrar a sessão do user com o email to
//        //se a sessão for diferente de null, então o user está online
//        return true;
//
//
//    }

//    //MESSAGE -     - MESSAGE -     - MESSAGE -     - MESSAGE -     - MESSAGE
//    private void handleNewMessage(Session session, JsonObject jsonObject) {
//        try {
//            String messageContent = jsonObject.get("message").getAsString();
//            String receiver = jsonObject.get("receiver").getAsString();
//
////            String sender = findUserEntityBySession(session).getUsername();
//            String token = session.getPathParameters().get("token");
//            String sender = sessionDao.findUserByTokenString(token).getUsername();
//
//            // Cria um novo objeto MessageDto
//            MessageDto messageDto = new MessageDto();
//            messageDto.setMessage(messageContent);
//            messageDto.setSender(sender);
//            messageDto.setReceiver(receiver);
//
//            // Grava a mensagem na base de dados
//            MessageEntity messageEntity = messageBean.convertMessageDtoToMessageEntity(messageDto);
//            messageDao.persist(messageEntity);
//
//            // Cria um novo objeto JSON para enviar a mensagem
//            jsonObject.addProperty("time", messageEntity.getTime().toString());
//            jsonObject.addProperty("sender", messageEntity.getSender_id().getUsername());
//            jsonObject.addProperty("id", messageEntity.getId());
//
//            // Envia a mensagem para o destinatário
//            messageBean.sendToUser(receiver, jsonObject.toString());
//            sendNotify(sender, receiver, "New Message from " + sender + " at " + messageEntity.getTime().toString());
//
//            // Envia a mensagem de volta para o remetente
//            jsonObject.addProperty("type", MessageType.MESSAGE_SENDER.getValue());
//            session.getBasicRemote().sendText(jsonObject.toString());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

//    //MESSAGE -     - MESSAGE -     - MESSAGE -     - MESSAGE -     - MESSAGE
//    //NOTIFICATION -- NOTIFICATION -- NOTIFICATION -- NOTIFICATION -- NOTIFICATION
//    private void sendNotify(String sender, String receiver, String notify) {
//        try {
//            NotificationDto notificationDto = new NotificationDto();
//            notificationDto.setReceiver(receiver);
//            notificationDto.setMessage(notify);
//            notificationDto.setSender(sender);
//
//            // Grava a notificação na base de dados
//            NotificationEntity notificationEntity = notificationBean.convertNotificationDtoToNotificationEntity(notificationDto);
//            notificationDao.persist(notificationEntity);
//
//            // Transforma a DTO em JSON
//            String json = gson.toJson(notificationDto);
//
//            // Cria um novo objeto JSON para enviar a notificação
//            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
//            jsonObject.add("time", gson.toJsonTree(notificationEntity.getTime()));
//            jsonObject.add("read", gson.toJsonTree(notificationEntity.isRead()));
//            jsonObject.add("type", gson.toJsonTree(MessageType.TYPE_40.getValue()));
//            jsonObject.add("id", gson.toJsonTree(notificationEntity.getId()));
//
//            messageBean.sendToUser(receiver, jsonObject.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

//    //NOTIFICATION -- NOTIFICATION -- NOTIFICATION -- NOTIFICATION -- NOTIFICATION
//    private UserEntity findUserEntityBySession(Session session) {
//        String token = session.getPathParameters().get("token");
//        if (token == null) return null;
//        return sessionDao.findUserByTokenString(token);
//    }
//
//    public boolean isProductOwner(String token) {
//        return sessionDao.findUserByTokenString(token).getRole().equals("po");
//    }
//
//    public String convertToJsonString(Object object, MessageType messageType) {
//        // Convert the original object to a JsonObject
//        JsonObject jsonObject = gson.toJsonTree(object).getAsJsonObject();
//
//        // Add the "type" property to the JsonObject
//        jsonObject.addProperty("type", messageType.getValue());
//
//        // Return the JSON representation of the modified JsonObject
//        return gson.toJson(jsonObject);
//    }

//    public String convertListToJsonString(Object object, MessageType messageType) {
//        // Convert the list to a JsonElement
//        JsonElement jsonElement = gson.toJsonTree(object);
//
//        // Create a new JsonObject to hold the array and the "type" property
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.add("data", jsonElement);
//        jsonObject.addProperty("type", messageType.getValue());
//
//        // Return the JSON representation of the JsonObject
//        return gson.toJson(jsonObject);
//    }

//    public void handleReadConfirmation(Session session, JsonObject jsonObject) {
//        int id = jsonObject.get("id").getAsInt();
//        String token = session.getPathParameters().get("token");
//
//        MessageEntity messageEntity = messageDao.findMessageById(id);
//        if (messageEntity == null) return;
//
//        messageEntity.setIsRead(true);
//        messageDao.merge(messageEntity);
//
//        jsonObject.addProperty("type", MessageType.MESSAGE_READ_CONFIRMATION.getValue());
//
//        messageBean.sendToUser(messageEntity.getSender_id().getUsername(), jsonObject.toString());
//    }


}
