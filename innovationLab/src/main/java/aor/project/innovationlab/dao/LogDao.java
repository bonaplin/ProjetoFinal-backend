package aor.project.innovationlab.dao;

import aor.project.innovationlab.dto.response.PaginatedResponse;
import aor.project.innovationlab.entity.LogEntity;
import aor.project.innovationlab.entity.MessageEntity;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class LogDao extends AbstractDao<LogEntity> {

    private static final long serialVersionUID = 1L;

    public LogDao() {
        super(LogEntity.class);
    }

    public List<LogEntity> findLogByProjectId(long projectId) {
        try {
            return em.createNamedQuery("Log.findLogByProjectId").setParameter("projectId", projectId)
                    .getResultList();

        } catch (Exception e) {
            return null;
        }
    }

    public PaginatedResponse<LogEntity> findLogsByProjectId(Long id, Integer pageNumber, Integer pageSize) {
        // create query builder for the entity
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<LogEntity> cq = cb.createQuery(LogEntity.class);
        Root<LogEntity> log = cq.from(LogEntity.class);
        List<Predicate> predicates = createPredicates(cb, log, id);

        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(log.get("id"))); // Order logs by ID in ascending order
        TypedQuery<LogEntity> createPredicate = em.createQuery(cq);

        // Calculate total pages
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<LogEntity> countRoot = countQuery.from(LogEntity.class);
        List<Predicate> countPredicates = createPredicates(cb, countRoot, id);

        countQuery.select(cb.count(countRoot));
        countQuery.where(countPredicates.toArray(new Predicate[0]));
        Long count = em.createQuery(countQuery).getSingleResult();

        int totalPages = (int) Math.ceil((double) count / pageSize);

        // Set first result and max results based on requested page
        createPredicate.setFirstResult((pageNumber - 1) * pageSize);
        createPredicate.setMaxResults(pageSize);

        List<LogEntity> results = createPredicate.getResultList();

        PaginatedResponse<LogEntity> response = new PaginatedResponse<>();

        response.setResults(results);
        response.setTotalPages(totalPages);
        return response;
    }

    private List<Predicate> createPredicates(CriteriaBuilder cb, Root<LogEntity> log, Long id) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(log.get("project").get("id"), id));

        return predicates;
    }
}
