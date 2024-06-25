package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.SessionDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.utils.JsonUtils;
import aor.project.innovationlab.utils.ws.MessageType;
import aor.project.innovationlab.websocket.Notifier;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Session;

import java.io.IOException;
import java.util.List;

/**
 * Bean for sending messages to user tokens
 */
@Stateless
public class WebSocketBean {

    @EJB
    private SessionDao sessionDao;

    @EJB
    private UserDao userDao;

    @Inject
    private Notifier notifier;

    /**
     * Send message to user tokens by username
     * @param userTokens - list of user tokens
     * @param messageJson - message in json format
     *
     */
    private void sendToUserTokens(List<String> userTokens, String messageJson) {
        for (String token : userTokens) {
            Session session = notifier.getSessions().get(token);
            if (session != null) {
                try {
                    session.getBasicRemote().sendObject(messageJson);
                } catch (IOException e) {
                    System.out.println("Something went wrong!");
                } catch (EncodeException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Send message to user by email address
     * @param userEmail - email address of user
     * @param messageJson - message in json format
     */
    public void sendToUser(String userEmail, String messageJson) {
        UserEntity user = userDao.findUserByEmail(userEmail);
        if(user == null) {
            throw new IllegalArgumentException("User not found");
        }

        List<String> userTokens;
        try {
            userTokens = getUserTokens(user);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get user tokens", e);
        }
        sendToUserTokens(userTokens, messageJson);
    }

    /**
     * Send message to all sessions
     * @param messageJson - message in json format
     */
    public void sendToAllSessions(String messageJson) {
        for (Session session : notifier.getSessions().values()) {
            try {
                session.getBasicRemote().sendObject(messageJson);
            } catch (Exception e) {
                System.out.println("Something went wrong!");
            }
        }
    }

    /**
     * Send message to user by email address
     * @param user - entity of user
     * @return - list of user tokens
     */
    private List<String> getUserTokens(UserEntity user) {
        try {
            return sessionDao.findUserTokens(user);
        } catch (Exception e) {
            throw new IllegalArgumentException("User not found");
        }
    }

    /**
     * Add type to DTO JSON
     * @param dto - DTO object
     * @param mt - message type
     * @param <T> - generic type
     * @return - JSON string with type
     */
    public <T> String addTypeToDtoJson(T dto, MessageType mt) {
        // Convert the DTO to a JSON string
        String json = JsonUtils.convertDtoToJson(dto);

        // Create a new JsonObject from the JSON string
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        // Add the "type" property to the JsonObject
        jsonObject.addProperty("type", mt.getValue());
        // Convert the JsonObject to a JSON string and return it
        return jsonObject.toString();
    }
}
