package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.MessageDao;
import aor.project.innovationlab.dao.ProjectDao;
import aor.project.innovationlab.dao.SessionDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.dto.message.MessageDto;
import aor.project.innovationlab.dto.response.PaginatedResponse;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.utils.logs.LoggerUtil;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
public class MessageBean {

    @EJB
    private UserDao userDao;

    @EJB
    private ProjectDao projectDao;

    @EJB
    private MessageDao messageDao;

    @EJB
    private SessionDao sessionDao;

    @Inject
    private NotificationBean notificationBean;

    @Inject
    private WebSocketBean webSocketBean;

    public MessageBean() {
    }

    /**
     * Convert MessageEntity to MessageDto
     * @param me - MessageEntity
     * @return - MessageDto
     */
    public MessageDto toDto(MessageEntity me){
        MessageDto dto = new MessageDto();
        dto.setId(me.getId());
        dto.setUserFirstName(me.getUser().getFirstname());
        dto.setUserEmail(me.getUser().getEmail());
        dto.setProjectId(me.getProject().getId());
        dto.setMessage(me.getMessage());
        dto.setCreatedAt(me.getInstant());
        return dto;
    }

    /**
     * Convert MessageDto to MessageEntity
     * @param dto - MessageDto
     * @return - MessageEntity
     */
    public MessageEntity toEntity(MessageDto dto) {
        MessageEntity message = new MessageEntity();
        UserEntity sender = userDao.findUserByEmail(dto.getUserEmail());
        ProjectEntity project = projectDao.findProjectById(dto.getId());
        if(sender == null || project == null) {
            return null;
        }
        message.setUser(sender);
        message.setMessage(dto.getMessage());
        message.setProject(project);
        return message;
    }

    /**
     * Send message to project
     * @param senderEmail - email of sender
     * @param id - project id
     * @param content - content of message
     */
    public void sendMessage(String senderEmail, long id, String content) {
        MessageEntity message = new MessageEntity();
        UserEntity sender = userDao.findUserByEmail(senderEmail);
        ProjectEntity project = projectDao.findProjectById(id);
        if(sender == null || project == null) {
            return;
        }
        message.setUser(sender);
        message.setMessage(content);
        message.setProject(project);
        messageDao.persist(message);
    }

    /**
     * Get messages for project
     * @param token - session token
     * @param id - project id
     * @param pageNumber - page number
     * @param pageSize - page size
     * @return - PaginatedResponse
     */
    public PaginatedResponse<Object> getProjectMessages(String token, Long id, Integer pageNumber, Integer pageSize) {
        String log = "Attempting to get messages for project with id: " + id;
        String msg="";
        SessionEntity session = sessionDao.findSessionByToken(token);
        if(session == null) {
            msg = "Session not found";
            LoggerUtil.logInfo(log,msg,null,token );
            throw new RuntimeException(log +msg);
        }

        ProjectEntity project = projectDao.findProjectById(id);
        UserEntity user = session.getUser();
        if(project == null) {
            msg = "Project not found";
            LoggerUtil.logInfo(log,msg,user.getEmail(),token );
            throw new RuntimeException(log + msg);
        }

        ProjectUserEntity pue = projectDao.findProjectUserByProjectAndUserId(project.getId(), user.getId());

        if(pue == null) {
            msg = "User not found in the project";
            LoggerUtil.logInfo(log,msg,user.getEmail(),token );
            throw new RuntimeException(log + msg);
        }

        if(!pue.isActive()) {
            msg = "User is not active in the project";
            LoggerUtil.logInfo(log,msg,user.getEmail(),token );
            throw new RuntimeException(log + msg);
        }

        if(pageNumber == null || pageNumber < 0) {
            pageNumber = 1;
        }

        if(pageSize == null || pageSize < 0) {
            pageSize = 5;
        }

        PaginatedResponse<MessageEntity> messageResponse = messageDao.findMessagesByProjectId(project.getId(), pageNumber, pageSize);

        List<MessageEntity> messages = messageResponse.getResults();

        PaginatedResponse<Object> response = new PaginatedResponse<>();
        response.setTotalPages(messageResponse.getTotalPages());
        response.setResults(messages.stream().map(this::toDto).collect(Collectors.toList()));

        return response;
    }

}
