package aor.project.innovationlab.dao;

import aor.project.innovationlab.dto.PaginatedResponse;
import aor.project.innovationlab.entity.EmailEntity;
import aor.project.innovationlab.entity.ProductEntity;
import aor.project.innovationlab.entity.UserEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.validation.constraints.Email;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class EmailDao extends AbstractDao<EmailEntity> {

    private static final long serialVersionUID = 1L;

    public EmailDao() {
        super(EmailEntity.class);
    }


    public PaginatedResponse<EmailEntity> findEmails(UserEntity senderUser,
                                                     UserEntity receiverUser,
                                                     Long groupId,
                                                     Long id,
                                                     Boolean isRead,
                                                     Integer pageNumber,
                                                     Integer pageSize,
                                                     String orderField,
                                                     String orderDirection) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EmailEntity> cq = cb.createQuery(EmailEntity.class);
        Root<EmailEntity> email = cq.from(EmailEntity.class);

        List<Predicate> predicates = createPredicates(cb, email, senderUser, receiverUser, groupId, id, isRead);
        cq.where(predicates.toArray(new Predicate[0]));

        Order order = getOrder(cb, email, orderField, orderDirection);
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

        PaginatedResponse<EmailEntity> response = new PaginatedResponse<>();
        response.setResults(emails);
        response.setTotalPages(totalPages);

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

    private Order getOrder(CriteriaBuilder cb, Root<EmailEntity> email, String orderField, String orderDirection) {
        if (orderField == null || orderDirection == null) {
            return null;
        }

        Path<Object> field = email.get(orderField);
        if (orderDirection.equalsIgnoreCase("ASC")) {
            return cb.asc(field);
        } else {
            return cb.desc(field);
        }
    }
}
