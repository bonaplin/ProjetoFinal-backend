package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.NotificationEntity;
import jakarta.ejb.Stateless;

@Stateless
public class NotificationDao extends AbstractDao<NotificationEntity> {

    private static final long serialVersionUID = 1L;

    public NotificationDao() {
        super(NotificationEntity.class);
    }

    public NotificationEntity findNotificationById(long id) {
        try {
            return (NotificationEntity) em.createNamedQuery("Notification.findNotificationById").setParameter("id", id)
                    .getSingleResult();

        } catch (Exception e) {
            return null;
        }
    }
}
