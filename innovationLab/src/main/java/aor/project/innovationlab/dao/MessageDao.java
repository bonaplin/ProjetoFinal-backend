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
        // Ordenar as mensagens por data em ordem decrescente
        cq.orderBy(cb.desc(message.get("instant")));
        // Criar a consulta
        TypedQuery<MessageEntity> query = em.createQuery(cq);

        // Definir o ponto de partida dos resultados
        query.setFirstResult((pageNumber - 1) * pageSize);

        // Definir o número máximo de resultados a serem recuperados
        query.setMaxResults(pageSize);

        // Executar a consulta e obter os resultados
        List<MessageEntity> results = query.getResultList();

        // Calcular o número total de páginas
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<MessageEntity> countRoot = countQuery.from(MessageEntity.class);
        List<Predicate> countPredicates = createPredicates(cb, countRoot, id);

        countQuery.select(cb.count(countRoot));
        countQuery.where(countPredicates.toArray(new Predicate[0]));
        Long count = em.createQuery(countQuery).getSingleResult();
        int totalPages = (int) Math.ceil((double) count / pageSize);

        // Criar a resposta paginada
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
