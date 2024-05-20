package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.MessageDao;
import aor.project.innovationlab.dao.ProjectDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.entity.MessageEntity;
import aor.project.innovationlab.entity.ProjectEntity;
import aor.project.innovationlab.entity.UserEntity;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class MessageBean {

    @EJB
    private UserDao userDao;

    @EJB
    private ProjectDao projectDao;

    @EJB
    private MessageDao messageDao;

    public MessageBean() {
    }

    public void sendMessage(String senderEmail, String projectName, String content) {
        MessageEntity message = new MessageEntity();
        UserEntity sender = userDao.findUserByEmail(senderEmail);
        ProjectEntity project = projectDao.findProjectByName(projectName);
        if(sender == null || project == null) {
            return;
        }
        message.setUser(sender);
        message.setMessage(content);
        message.setProject(project);
        messageDao.persist(message);
    }

    public void removeMessage(long messageId) {
            MessageEntity message = messageDao.findMessageById(messageId);
            if(message == null) {
                return;
            }
            message.setActive(false);
            messageDao.merge(message);
    }
}
