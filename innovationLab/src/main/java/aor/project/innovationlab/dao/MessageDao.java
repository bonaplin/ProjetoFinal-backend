package aor.project.innovationlab.dao;

import aor.project.innovationlab.dto.response.PaginatedResponse;
import aor.project.innovationlab.entity.MessageEntity;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Stateless
public class MessageDao extends AbstractDao<MessageEntity> {

    private static final long serialVersionUID = 1L;

    public MessageDao() {
        super(MessageEntity.class);
    }

    public MessageEntity findMessageById(long id) {
        try {
            return (MessageEntity) em.createNamedQuery("Message.findMessageById").setParameter("id", id)
                    .getSingleResult();

        } catch (Exception e) {
            return null;
        }
    }

    public PaginatedResponse<MessageEntity> findMessagesByProjectId(long id, Integer pageNumber, Integer pageSize) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<MessageEntity> cq = cb.createQuery(MessageEntity.class);
        Root<MessageEntity> message = cq.from(MessageEntity.class);
        List<Predicate> predicates = createPredicates(cb, message, id);

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(message.get("id"))); // Ordenar as mensagens por ID em ordem ascendente
        TypedQuery<MessageEntity> query = em.createQuery(cq);

        // Calcular o total de páginas
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<MessageEntity> countRoot = countQuery.from(MessageEntity.class);
        List<Predicate> countPredicates = createPredicates(cb, countRoot, id);

        countQuery.select(cb.count(countRoot));
        countQuery.where(countPredicates.toArray(new Predicate[0]));
        Long count = em.createQuery(countQuery).getSingleResult();

        int totalPages = (int) Math.ceil((double) count / pageSize);

        // Calcular a página real com base no total de páginas e na página solicitada
        int realPageNumber = totalPages - pageNumber + 1;

        // Definir o primeiro resultado e o máximo de resultados com base na página real
        query.setFirstResult((realPageNumber - 1) * pageSize);
        query.setMaxResults(pageSize);

        List<MessageEntity> results = query.getResultList();

        PaginatedResponse<MessageEntity> response = new PaginatedResponse<>();
        response.setResults(results);
        response.setTotalPages(totalPages);

        return response;
    }

    private List<Predicate> createPredicates(CriteriaBuilder cb, Root<MessageEntity> message, long id) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(message.get("project").get("id"), id));

        return predicates;
    }
}
