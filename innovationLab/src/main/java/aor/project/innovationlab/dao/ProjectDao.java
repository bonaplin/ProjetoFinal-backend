package aor.project.innovationlab.dao;

import aor.project.innovationlab.bean.SessionBean;
import aor.project.innovationlab.dto.PaginatedResponse;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.ProjectStatus;
import aor.project.innovationlab.enums.ProjectUserType;
import aor.project.innovationlab.utils.Color;
import aor.project.innovationlab.utils.InputSanitizerUtil;
import aor.project.innovationlab.utils.PasswordUtil;
import aor.project.innovationlab.validator.UserValidator;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class ProjectDao extends AbstractDao<ProjectEntity> {

        private static final long serialVersionUID = 1L;

        public ProjectDao() {
            super(ProjectEntity.class);
        }

        public ProjectEntity findProjectByName(String name) {
            try {
                return (ProjectEntity) em.createNamedQuery("Project.findProjectByName").setParameter("name", name)
                        .getSingleResult();

            } catch (Exception e) {
                return null;
            }
        }

    public ProjectEntity findProjectById(long projectId) {
        try {
            return (ProjectEntity) em.createNamedQuery("Project.findProjectById").setParameter("projectId", projectId)
                    .getSingleResult();

        } catch (Exception e) {
            return null;
        }
    }

    public PaginatedResponse<ProjectEntity> findProjects(String name,
                                                         List<ProjectStatus> status,
                                                         List<String> labs,
                                                         String creatorEmail,
                                                         List<String> skills,
                                                         List<String> interests,
                                                         String participantEmail,
                                                         ProjectUserType role,
                                                         String requestingUserEmail,
                                                         Long id,
                                                         Integer pageNumber,
                                                         Integer pageSize,
                                                         String orderField,
                                                         String orderDirection) {

        //Create query and add predicates
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProjectEntity> cq = cb.createQuery(ProjectEntity.class);
//        cq.distinct(true);
        Root<ProjectEntity> project = cq.from(ProjectEntity.class);

        List<Predicate> predicates = createPredicatesWithoutJoin(cb, cq, project, name, status, labs, creatorEmail, skills, interests, participantEmail, role, requestingUserEmail);
        cq.where(predicates.toArray(new Predicate[0]));

        Order order = getOrder(cb, project, orderField, orderDirection);
        // Subquery to order by 1st creator, 2nd participant, 3rd others
        if (requestingUserEmail != null) {
            Subquery<Long> userSubquery = cq.subquery(Long.class);
            Root<ProjectUserEntity> userRoot = userSubquery.from(ProjectUserEntity.class);
            userSubquery.select(userRoot.get("project").get("id"));
            userSubquery.where(cb.equal(userRoot.get("user").get("email"), requestingUserEmail));

            Expression<Integer> orderByExpression = cb.<Integer>selectCase()
                    .when(cb.equal(project.get("creator").get("email"), requestingUserEmail), 1)
                    .when(cb.exists(userSubquery), 2)
                    .otherwise(3)
                    .as(Integer.class);
            if (order != null) {
                System.out.println("ORDER BY: " + order + " " + orderByExpression);
                cq.orderBy(order, cb.asc(orderByExpression));
            } else {
                System.out.println("ORDER BY: " + orderByExpression);
                cq.orderBy(cb.asc(orderByExpression));
            }
        } else if (order != null) {
            cq.orderBy(order);
        }

        TypedQuery<ProjectEntity> query = em.createQuery(cq);
        query.setFirstResult((pageNumber - 1) * pageSize);
        query.setMaxResults(pageSize);

        List<ProjectEntity> projects = query.getResultList();

        // Construct the count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ProjectEntity> countRoot = countQuery.from(ProjectEntity.class);
        List<Predicate> countPredicates = createPredicatesWithoutJoin(cb, cq, countRoot, name, status, labs, creatorEmail, skills, interests, participantEmail, role, requestingUserEmail);

        countQuery.select(cb.count(countRoot));
        countQuery.where(countPredicates.toArray(new Predicate[0]));
        Long count = em.createQuery(countQuery).getSingleResult();
        // Calculate total pages
        int totalPages = (int) Math.ceil((double) count / pageSize);

        PaginatedResponse<ProjectEntity> response = new PaginatedResponse<>();
        response.setResults(projects);
        response.setTotalPages(totalPages);

        return response;
    }

    private List<Predicate> createPredicatesWithoutJoin(CriteriaBuilder cb,CriteriaQuery<ProjectEntity> cq, Root<ProjectEntity> project, String name,
                                                        List<ProjectStatus> status, List<String> labs, String creatorEmail,
                                                        List<String> skills, List<String> interests, String participantEmail,
                                                        ProjectUserType role, String requestingUserEmail) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.isTrue(project.get("active")));

        if (name != null) {
            predicates.add(cb.and(
                    cb.equal(project.get("name"), name),
                    cb.isTrue(project.get("active")) // Verifica se o projeto está ativo
            ));
        }
        if (status != null && !status.isEmpty()) {
            predicates.add(cb.and(
                    project.get("status").in(status),
                    cb.isTrue(project.get("active")) // Verifica se o projeto está ativo
            ));
        }
        if (skills != null && !skills.isEmpty()) {
            for (String skill : skills) {
                Subquery<Long> skillSubquery = cq.subquery(Long.class);
                Root<ProjectSkillEntity> skillRoot = skillSubquery.from(ProjectSkillEntity.class);
                skillSubquery.select(skillRoot.get("project").get("id"));
                skillSubquery.where(cb.and(
                        cb.equal(skillRoot.get("skill").get("name"), skill),
                        cb.isTrue(skillRoot.get("skill").get("active"))
                ));
                predicates.add(cb.in(project.get("id")).value(skillSubquery));
            }
        }
        if (interests != null && !interests.isEmpty()) {
            for (String interest : interests) {
                Subquery<Long> interestSubquery = cq.subquery(Long.class);
                Root<ProjectInterestEntity> interestRoot = interestSubquery.from(ProjectInterestEntity.class);
                interestSubquery.select(interestRoot.get("project").get("id"));
                interestSubquery.where(cb.and(
                        cb.equal(interestRoot.get("interest").get("name"), interest),
                        cb.isTrue(interestRoot.get("interest").get("active")) // Verifica se o interesse está ativo
                ));
                predicates.add(cb.in(project.get("id")).value(interestSubquery));
            }
        }
        if (labs != null && !labs.isEmpty()) {
            predicates.add(project.get("lab").get("location").in(labs));
        }
        if (creatorEmail != null) {
            predicates.add(cb.and(
                    cb.equal(project.get("creator").get("email"), creatorEmail),
                    cb.isTrue(project.get("active")) // Verifica se o projeto está ativo
            ));
        }

        if (participantEmail != null) {
            // Create a subquery to check if the project has a ProjectUserEntity that matches the criteria
            Subquery<Long> userSubquery = cq.subquery(Long.class);
            Root<ProjectUserEntity> userRoot = userSubquery.from(ProjectUserEntity.class);
            userSubquery.select(userRoot.get("project").get("id"));
            Predicate emailPredicate = cb.equal(userRoot.get("user").get("email"), participantEmail);
            Predicate activePredicate = cb.isTrue(userRoot.get("user").get("active"));
            Predicate rolePredicate;
            if(role != null){
                rolePredicate = cb.equal(userRoot.get("role"), role);
            } else {
                rolePredicate = userRoot.get("role").in(ProjectUserType.MANAGER, ProjectUserType.NORMAL);
            }
            userSubquery.where(cb.and(emailPredicate, activePredicate, rolePredicate));
            predicates.add(cb.in(project.get("id")).value(userSubquery));
        }

        return predicates;
    }

    private Order getOrder(CriteriaBuilder cb, Root<ProjectEntity> project, String orderField, String orderDirection) {
        if (orderField != null && !orderField.isEmpty() && orderDirection != null && !orderDirection.isEmpty()) {
            Path<LocalDate> orderPath;
            if (orderField.equals("createdDate")) {
                // If the order field is 'createdDate', get the path as a LocalDate
                orderPath = project.<LocalDate>get(orderField);
            }
        if (orderField.equals("status")) {
            // Se o campo de ordenação é 'status', obtenha o valor do status como um Integer
            Expression<Integer> statusValue = cb.<Integer>selectCase()
                    .when(cb.equal(project.get(orderField), ProjectStatus.PLANNING), 10)
                    .when(cb.equal(project.get(orderField), ProjectStatus.READY), 20)
                    .when(cb.equal(project.get(orderField), ProjectStatus.IN_PROGRESS), 30)
                    .when(cb.equal(project.get(orderField), ProjectStatus.CANCELED), 40)
                    .when(cb.equal(project.get(orderField), ProjectStatus.FINISHED), 50)
                    .otherwise(0)
                    .as(Integer.class);

            if (orderDirection.equalsIgnoreCase("asc")) {
                return cb.asc(statusValue);
            } else if (orderDirection.equalsIgnoreCase("desc")) {
                return cb.desc(statusValue);
            } else {
                throw new IllegalArgumentException("Invalid order direction. It should be 'asc' or 'desc'.");
            }
        } else if(orderField.equals("vacancies")){
            Subquery<Long> sq = cb.createQuery().subquery(Long.class);
            Root<ProjectUserEntity> projectUser = sq.from(ProjectUserEntity.class);
            sq.select(cb.count(projectUser)).where(cb.equal(projectUser.get("project"), project));

            Expression<Number> vacancies = cb.diff(project.<Integer>get("maxParticipants"), sq.getSelection());

            if (orderDirection.equalsIgnoreCase("asc")) {
                return cb.asc(vacancies);
            } else if (orderDirection.equalsIgnoreCase("desc")) {
                return cb.desc(vacancies);
            } else {
                throw new IllegalArgumentException("Invalid order direction. It should be 'asc' or 'desc'.");
            }
        }


        else {
        orderPath = project.get(orderField);
            }

            if (orderDirection.equalsIgnoreCase("asc")) {
                return cb.asc(orderPath);
            } else if (orderDirection.equalsIgnoreCase("desc")) {
                return cb.desc(orderPath);
            } else {
                throw new IllegalArgumentException("Invalid order direction. It should be 'asc' or 'desc'.");
            }
        }
        return null;
    }


}
