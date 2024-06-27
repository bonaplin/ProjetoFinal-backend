package aor.project.innovationlab.dao;

import aor.project.innovationlab.dto.PagAndUnreadResponse;
import aor.project.innovationlab.entity.NotificationEntity;
import aor.project.innovationlab.entity.ProjectEntity;
import aor.project.innovationlab.entity.UserEntity;
import com.sun.tools.jconsole.JConsoleContext;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;

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

    public PagAndUnreadResponse<NotificationEntity> findNotifications(String receiverEmail, Integer pageNumber, Integer pageSize) {

        System.out.println(receiverEmail);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<NotificationEntity> cq = cb.createQuery(NotificationEntity.class);
        Root<NotificationEntity> notification = cq.from(NotificationEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        if(receiverEmail != null) {
            Join<NotificationEntity, UserEntity> receiver = notification.join("receiver");
            predicates.add(cb.equal(receiver.get("email"), receiverEmail));
        }

        System.out.println("NotificationDao.findNotifications");
        predicates.add(cb.isTrue(notification.get("active")));

        cq.where(predicates.toArray(new Predicate[0]));

        cq.orderBy(cb.desc(notification.get("instant")));

        TypedQuery<NotificationEntity> query = em.createQuery(cq);
        query.setFirstResult((pageNumber - 1) * pageSize);
        query.setMaxResults(pageSize);

        long unreadCount = countUnreadNotifications(receiverEmail);
        System.out.println("Unread count: " + unreadCount);
        PagAndUnreadResponse<NotificationEntity> response = new PagAndUnreadResponse<>();
        response.setResults(query.getResultList());
        response.setUnreadCount(unreadCount);
        response.setTotalPages((int) Math.ceil((double) unreadCount / pageSize));

        return response;
    }

    public Long countUnreadNotifications(String receiverEmail) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<NotificationEntity> notification = cq.from(NotificationEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        if(receiverEmail != null) {
            Join<NotificationEntity, UserEntity> receiver = notification.join("receiver");
            predicates.add(cb.equal(receiver.get("email"), receiverEmail));
        }
        predicates.add(cb.isFalse(notification.get("read")));

        cq.select(cb.count(notification)).where(predicates.toArray(new Predicate[0]));

        return em.createQuery(cq).getSingleResult();
    }
}
