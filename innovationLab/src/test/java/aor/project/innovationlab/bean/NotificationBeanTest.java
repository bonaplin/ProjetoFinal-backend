package aor.project.innovationlab.bean;

import aor.project.innovationlab.bean.NotificationBean;
import aor.project.innovationlab.dao.NotificationDao;
import aor.project.innovationlab.dao.ProjectDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.entity.NotificationEntity;
import aor.project.innovationlab.entity.ProjectEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.enums.NotificationType;
import aor.project.innovationlab.dto.notification.NotificationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class NotificationBeanTest {

    @InjectMocks
    private NotificationBean notificationBean;

    @Mock
    private NotificationDao notificationDao;

    @Mock
    private UserDao userDao;

    @Mock
    private WebSocketBean webSocketBean;

    @Mock
    private ProjectDao projectDao;

    @BeforeEach
    void setUp() {
        notificationBean = new NotificationBean();
        MockitoAnnotations.initMocks(this);

    }

    @Test
    void testToDto() {
        // Create mock NotificationEntity
        NotificationEntity ne = new NotificationEntity();
        ne.setId(1L);
        ne.setContent("Test Content");
        ne.setInstant(java.time.Instant.now());
        ne.setRead(false);

        // Mock sender and receiver UserEntity
        UserEntity sender = new UserEntity();
        sender.setEmail("sender@example.com");
        sender.setFirstname("SenderName");
        sender.setProfileImagePath("path/to/sender/image");
        ne.setSender(sender);

        UserEntity receiver = new UserEntity();
        receiver.setEmail("receiver@example.com");
        ne.setReceiver(receiver);

        // Mock ProjectEntity (optional)
        ProjectEntity project = new ProjectEntity();
        project.setId(2L);
        ne.setProject(project);

        ne.setNotificationType(NotificationType.MESSAGE);

        // Convert to DTO
        NotificationDto dto = notificationBean.toDto(ne);

        // Assertions
        assertEquals(ne.getId(), dto.getId());
        assertEquals(ne.getSender().getEmail(), dto.getSenderEmail());
        assertEquals(ne.getReceiver().getEmail(), dto.getReceiverEmail());
        assertEquals(ne.getContent(), dto.getContent());
        assertEquals(ne.getInstant(), dto.getInstant());
        assertEquals(ne.isRead(), dto.isRead());
        assertEquals(ne.getSender().getFirstname(), dto.getSenderName());
        assertEquals(ne.getSender().getProfileImagePath(), dto.getSenderImg());
        assertEquals(ne.getNotificationType().getValue(), dto.getNotificationType());
        assertEquals(ne.getProject().getId(), dto.getProjectId());
    }

    @Test
    void testConvert() {
        // Create mock NotificationEntity
        NotificationEntity ne = new NotificationEntity();
        ne.setId(1L);
        ne.setContent("Test Content");
        ne.setInstant(java.time.Instant.now());
        ne.setRead(false);

        // Mock sender and receiver UserEntity
        UserEntity sender = new UserEntity();
        sender.setEmail("sender@example.com");
        sender.setFirstname("SenderName");
        sender.setProfileImagePath("path/to/sender/image");
        ne.setSender(sender);

        UserEntity receiver = new UserEntity();
        receiver.setEmail("receiver@example.com");
        ne.setReceiver(receiver);

        // Mock ProjectEntity (optional)
        ProjectEntity project = new ProjectEntity();
        project.setId(2L);
        ne.setProject(project);

        ne.setNotificationType(NotificationType.MESSAGE);

        // Convert to DTO
        NotificationDto dto = notificationBean.toDto(ne);

        // Assertions
        assertEquals(ne.getId(), dto.getId());
        assertEquals(ne.getSender().getEmail(), dto.getSenderEmail());
        assertEquals(ne.getReceiver().getEmail(), dto.getReceiverEmail());
        assertEquals(ne.getContent(), dto.getContent());
        assertEquals(ne.getInstant(), dto.getInstant());
        assertEquals(ne.isRead(), dto.isRead());
        assertEquals(ne.getSender().getFirstname(), dto.getSenderName());
        assertEquals(ne.getSender().getProfileImagePath(), dto.getSenderImg());
        assertEquals(ne.getNotificationType().getValue(), dto.getNotificationType());
        assertEquals(ne.getProject().getId(), dto.getProjectId());
    }


}