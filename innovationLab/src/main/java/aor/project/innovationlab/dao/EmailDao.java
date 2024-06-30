package aor.project.innovationlab.dao;

import aor.project.innovationlab.dto.response.PagAndUnreadResponse;
import aor.project.innovationlab.entity.EmailEntity;
import aor.project.innovationlab.entity.UserEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class EmailDao extends AbstractDao<EmailEntity> {

    private static final long serialVersionUID = 1L;

    public EmailDao() {
        super(EmailEntity.class);
    }


    public PagAndUnreadResponse<EmailEntity> findEmails(UserEntity senderUser,
                                                        UserEntity receiverUser,
                                                        Long groupId,
                                                        Long id,
                                                        Boolean isRead,
                                                        Integer pageNumber,
                                                        Integer pageSize,
                                                        String orderField,
                                                        String orderDirection,
                                                        String userEmail,
                                                        String searchText) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EmailEntity> cq = cb.createQuery(EmailEntity.class);
        Root<EmailEntity> email = cq.from(EmailEntity.class);

        List<Predicate> predicates = createPredicates(cb, email, senderUser, receiverUser, groupId, id, isRead);
        predicates.add(cb.or(
                cb.and(cb.equal(email.get("sender").get("email"), userEmail), cb.isFalse(email.get("deletedBySender"))),
                cb.and(cb.equal(email.get("receiver").get("email"), userEmail), cb.isFalse(email.get("deletedByReceiver")))
        ));

        if (searchText != null && !searchText.isEmpty()) {
            Predicate searchTextCondition = cb.or(
                    cb.like(email.get("subject"), "%" + searchText + "%"),
                    cb.like(email.get("body"), "%" + searchText + "%"),
                    cb.like(email.get("sender").get("email"), "%" + searchText + "%"),
                    cb.like(email.get("receiver").get("email"), "%" + searchText + "%"),
                    cb.like(email.get("sender").get("firstname"), "%" + searchText + "%"),
                    cb.like(email.get("sender").get("lastname"), "%" + searchText + "%"),
                    cb.like(email.get("receiver").get("firstname"), "%" + searchText + "%"),
                    cb.like(email.get("receiver").get("lastname"), "%" + searchText + "%")
            );
            predicates.add(searchTextCondition);
        }

        cq.where(predicates.toArray(new Predicate[0]));

        Order order = getOrder(cb, email);
        if (order != null) {
            cq.orderBy(order);
        }

        TypedQuery<EmailEntity> query = em.createQuery(cq);
        query.setFirstResult((pageNumber - 1) * pageSize);
        query.setMaxResults(pageSize);

        List<EmailEntity> emails = query.getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<EmailEntity> countRoot = countQuery.from(EmailEntity.class);
        List<Predicate> countPredicates = createPredicates(cb, countRoot, senderUser, receiverUser, groupId, id, isRead);

        countQuery.select(cb.count(countRoot));
        countQuery.where(countPredicates.toArray(new Predicate[0]));
        Long count = em.createQuery(countQuery).getSingleResult();
        int totalPages = (int) Math.ceil((double) count / pageSize);

        long unreadCount = 0;
        if(receiverUser != null){
            unreadCount = countUnreadEmails(userEmail);
        }
        PagAndUnreadResponse<EmailEntity> response = new PagAndUnreadResponse<>();
        response.setResults(emails);
        response.setTotalPages(totalPages);
        response.setUnreadCount(unreadCount);

        return response;
    }

    private List<Predicate> createPredicates(CriteriaBuilder cb, Root<EmailEntity> email, UserEntity senderUser, UserEntity receiverUser, Long groupId, Long id, Boolean isRead) {
        List<Predicate> predicates = new ArrayList<>();
        if (senderUser != null) {
            predicates.add(cb.equal(email.get("sender"), senderUser));
        }
        if (receiverUser != null) {
            predicates.add(cb.equal(email.get("receiver"), receiverUser));
        }
        if (senderUser != null && receiverUser != null) {
            predicates.add(cb.and(cb.equal(email.get("sender"), senderUser), cb.equal(email.get("receiver"), receiverUser)));
        }
        if (groupId != null) {
            predicates.add(cb.equal(email.get("groupId"), groupId));
        }
        if (isRead != null) {
            predicates.add(cb.equal(email.get("isRead"), isRead));
        }
        if(id != null){
            predicates.add(cb.equal(email.get("id"), id));
        }
        return predicates;
    }

    private Order getOrder(CriteriaBuilder cb, Root<EmailEntity> email) {
        return cb.desc(email.get("sentDate"));
    }

    public EmailEntity findEmailById(Long id) {
        try{
            return em.find(EmailEntity.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    public long countUnreadEmails(String userEmail) {
        UserEntity receiverUser = em.createQuery("SELECT u FROM UserEntity u WHERE u.email = :email", UserEntity.class)
                .setParameter("email", userEmail)
                .getSingleResult();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<EmailEntity> countRoot = countQuery.from(EmailEntity.class);
        List<Predicate> countPredicates = createPredicates(cb, countRoot, null, receiverUser, null, null, false);
        countQuery.select(cb.count(countRoot));
        countQuery.where(countPredicates.toArray(new Predicate[0]));
        return em.createQuery(countQuery).getSingleResult();
    }
}
