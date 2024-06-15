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
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
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

//    public PaginatedResponse<ProjectEntity> findProjects(String name,
//                                                         List<ProjectStatus> status,
//                                                         List<String> labs,
//                                                         String creatorEmail,
//                                                         List<String> skills,
//                                                         List<String> interests,
//                                                         String participantEmail,
//                                                         ProjectUserType role,
//                                                         String requestingUserEmail,
//                                                         Integer pageNumber,
//                                                         Integer pageSize) {
//
//        //Validate inputs
//        name = InputSanitizerUtil.sanitizeInput(name);
//        creatorEmail = InputSanitizerUtil.sanitizeInput(creatorEmail);
//        participantEmail = InputSanitizerUtil.sanitizeInput(participantEmail);
//
//        if (skills != null) {
//            skills = skills.stream()
//                    .map(InputSanitizerUtil::sanitizeInput)
//                    .collect(Collectors.toList());
//        }
//
//        if (interests != null) {
//            interests = interests.stream()
//                    .map(InputSanitizerUtil::sanitizeInput)
//                    .collect(Collectors.toList());
//        }
//
//        //Validate email
//        if(creatorEmail != null && !UserValidator.validateEmail(creatorEmail)){
//            throw new IllegalArgumentException("Invalid creator email");
//        }
//        if(participantEmail != null && !UserValidator.validateEmail(participantEmail)){
//            throw new IllegalArgumentException("Invalid participant email");
//        }
//
//        //Create query and add predicates
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<ProjectEntity> cq = cb.createQuery(ProjectEntity.class);
//        Root<ProjectEntity> project = cq.from(ProjectEntity.class);
//        List<Predicate> predicates = new ArrayList<>();
//
//        predicates.add(cb.isTrue(project.get("active")));
//
//        if (name != null) {
//            predicates.add(cb.and(
//                    cb.equal(project.get("name"), name),
//                    cb.isTrue(project.get("active")) // Verifica se o projeto está ativo
//            ));
//        }
//        if (status != null && !status.isEmpty()) {
//            predicates.add(cb.and(
//                    project.get("status").in(status),
//                    cb.isTrue(project.get("active")) // Verifica se o projeto está ativo
//            ));
//        }
//
//        if (skills != null && !skills.isEmpty()) {
//            for (String skill : skills) {
//                Subquery<Long> skillSubquery = cq.subquery(Long.class);
//                Root<ProjectSkillEntity> skillRoot = skillSubquery.from(ProjectSkillEntity.class);
//                skillSubquery.select(skillRoot.get("project").get("id"));
//                skillSubquery.where(cb.and(
//                        cb.equal(skillRoot.get("skill").get("name"), skill),
//                        cb.isTrue(skillRoot.get("skill").get("active"))
//                ));
//                predicates.add(cb.in(project.get("id")).value(skillSubquery));
//            }
//        }
//        if (interests != null && !interests.isEmpty()) {
//            for (String interest : interests) {
//                Subquery<Long> interestSubquery = cq.subquery(Long.class);
//                Root<ProjectInterestEntity> interestRoot = interestSubquery.from(ProjectInterestEntity.class);
//                interestSubquery.select(interestRoot.get("project").get("id"));
//                interestSubquery.where(cb.and(
//                        cb.equal(interestRoot.get("interest").get("name"), interest),
//                        cb.isTrue(interestRoot.get("interest").get("active")) // Verifica se o interesse está ativo
//                ));
//                predicates.add(cb.in(project.get("id")).value(interestSubquery));
//            }
//        }
//        if (labs != null && !labs.isEmpty()) {
//            predicates.add(project.get("lab").get("location").in(labs));
//        }
//        if (creatorEmail != null) {
//            predicates.add(cb.and(
//                    cb.equal(project.get("creator").get("email"), creatorEmail),
//                    cb.isTrue(project.get("active")) // Verifica se o projeto está ativo
//            ));
//        }
//        if (participantEmail != null) {
//            // Join with ProjectUserEntity to get projects where the user is a participant
//            Join<ProjectEntity, ProjectUserEntity> userJoin = project.join("projectUsers", JoinType.LEFT);
//            predicates.add(cb.and(
//                    cb.or(
//                            cb.equal(userJoin.get("user").get("email"), participantEmail),
//                            cb.equal(project.get("creator").get("email"), participantEmail)
//                    ),
//
//                    cb.isTrue(project.get("active")) // Verifica se o projeto está ativo
//            ));
//            if(role != null){
//                predicates.add(cb.equal(userJoin.get("role"), role));
//            } else {
//                predicates.add(userJoin.get("role").in(ProjectUserType.MANAGER, ProjectUserType.NORMAL));
//            }
//        }
//
//        //Order by creator, participant, other
//        Join<ProjectEntity, ProjectUserEntity> projectUserJoin = project.join("projectUsers");
//        Join<ProjectUserEntity, UserEntity> userJoin = projectUserJoin.join("user");
//
//        System.out.println("Requesting user email: before: " + requestingUserEmail);
//        if (requestingUserEmail != null) {
//            System.out.println("Requesting user email: after: " + requestingUserEmail);
//            //Create expression to order by creator, participant, other
//            Expression<Integer> orderByExpression = cb.<Integer>selectCase()
//                    .when(cb.equal(project.get("creator").get("email"), requestingUserEmail), 1) // Se o user que faz o pedido é criador, atribui 1
//                    .when(cb.equal(userJoin.get("email"), requestingUserEmail), 2) // Se o user que faz o pedido é participante, atribui 2
//                    .otherwise(3) // Se o user que faz o pedido não é nem criador nem participante, atribui 3
//                    .as(Integer.class); // Converte a Expression<Object> em uma Expression<Integer>
//
//            cq.orderBy(cb.asc(orderByExpression));
//        }
//        //Add predicates to query and return result
//        cq.where(predicates.toArray(new Predicate[0]));
//
//        TypedQuery<ProjectEntity> query = em.createQuery(cq);
//        query.setFirstResult((pageNumber - 1) * pageSize);
//        query.setMaxResults(pageSize);
//
//        List<ProjectEntity> projects = query.getResultList();
//
//        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
//        countQuery.select(cb.count(countQuery.from(ProjectEntity.class)));
//        Long count = em.createQuery(countQuery).getSingleResult();
//        int totalPages = (int) Math.ceil((double) count / pageSize);
//
//        PaginatedResponse<ProjectEntity> response = new PaginatedResponse<>();
//        response.setResults(projects);
//        response.setTotalPages(totalPages);
//
//        return response;
//    }

    public PaginatedResponse<ProjectEntity> findProjects(String name,
                                                         List<ProjectStatus> status,
                                                         List<String> labs,
                                                         String creatorEmail,
                                                         List<String> skills,
                                                         List<String> interests,
                                                         String participantEmail,
                                                         ProjectUserType role,
                                                         String requestingUserEmail,
                                                         Integer pageNumber,
                                                         Integer pageSize) {

        //Create query and add predicates
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProjectEntity> cq = cb.createQuery(ProjectEntity.class);
        cq.distinct(true);
        Root<ProjectEntity> project = cq.from(ProjectEntity.class);

        List<Predicate> predicates = createPredicatesWithoutJoin(cb, cq, project, name, status, labs, creatorEmail, skills, interests, participantEmail, role, requestingUserEmail);
        cq.where(predicates.toArray(new Predicate[0]));

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

//    private List<Predicate> createPredicates(CriteriaBuilder cb, CriteriaQuery<ProjectEntity> cq, Root<ProjectEntity> project, String name,
//                                             List<ProjectStatus> status, List<String> labs, String creatorEmail,
//                                             List<String> skills, List<String> interests, String participantEmail,
//                                             ProjectUserType role, String requestingUserEmail) {
//        List<Predicate> predicates = new ArrayList<>();
//        predicates.add(cb.isTrue(project.get("active")));
//
//        if (name != null) {
//            predicates.add(cb.and(
//                    cb.equal(project.get("name"), name),
//                    cb.isTrue(project.get("active")) // Verifica se o projeto está ativo
//            ));
//        }
//        if (status != null && !status.isEmpty()) {
//            predicates.add(cb.and(
//                    project.get("status").in(status),
//                    cb.isTrue(project.get("active")) // Verifica se o projeto está ativo
//            ));
//        }
//        if (skills != null && !skills.isEmpty()) {
//            for (String skill : skills) {
//                Subquery<Long> skillSubquery = cq.subquery(Long.class);
//                Root<ProjectSkillEntity> skillRoot = skillSubquery.from(ProjectSkillEntity.class);
//                skillSubquery.select(skillRoot.get("project").get("id"));
//                skillSubquery.where(cb.and(
//                        cb.equal(skillRoot.get("skill").get("name"), skill),
//                        cb.isTrue(skillRoot.get("skill").get("active"))
//                ));
//                predicates.add(cb.in(project.get("id")).value(skillSubquery));
//            }
//        }
//        if (interests != null && !interests.isEmpty()) {
//            for (String interest : interests) {
//                Subquery<Long> interestSubquery = cq.subquery(Long.class);
//                Root<ProjectInterestEntity> interestRoot = interestSubquery.from(ProjectInterestEntity.class);
//                interestSubquery.select(interestRoot.get("project").get("id"));
//                interestSubquery.where(cb.and(
//                        cb.equal(interestRoot.get("interest").get("name"), interest),
//                        cb.isTrue(interestRoot.get("interest").get("active")) // Verifica se o interesse está ativo
//                ));
//                predicates.add(cb.in(project.get("id")).value(interestSubquery));
//            }
//        }
//        if (labs != null && !labs.isEmpty()) {
//            predicates.add(project.get("lab").get("location").in(labs));
//        }
//        if (creatorEmail != null) {
//            predicates.add(cb.and(
//                    cb.equal(project.get("creator").get("email"), creatorEmail),
//                    cb.isTrue(project.get("active")) // Verifica se o projeto está ativo
//            ));
//        }
//        if (participantEmail != null) {
//            System.out.println("Participant email: " + participantEmail);
//            // Join with ProjectUserEntity to get projects where the user is a participant
//            Join<ProjectEntity, ProjectUserEntity> userJoin = project.join("projectUsers", JoinType.LEFT);
//            predicates.add(cb.and(
//                    cb.or(
//                            cb.equal(userJoin.get("user").get("email"), participantEmail),
//                            cb.equal(project.get("creator").get("email"), participantEmail)
//                    ),
//                    cb.isTrue(project.get("active")) // Verifica se o projeto está ativo
//            ));
//            if(role != null){
//                predicates.add(cb.equal(userJoin.get("role"), role));
//            } else {
//                predicates.add(userJoin.get("role").in(ProjectUserType.MANAGER, ProjectUserType.NORMAL));
//            }
//        }
//
//        //Order by creator, participant, other
//        Join<ProjectEntity, ProjectUserEntity> projectUserJoin = project.join("projectUsers");
//        Join<ProjectUserEntity, UserEntity> userJoin = projectUserJoin.join("user");
//
//        System.out.println("Requesting user email: before: " + requestingUserEmail);
//        if (requestingUserEmail != null) {
//            System.out.println("Requesting user email: after: " + requestingUserEmail);
//            //Create expression to order by creator, participant, other
//            Expression<Integer> orderByExpression = cb.<Integer>selectCase()
//                    .when(cb.equal(project.get("creator").get("email"), requestingUserEmail), 1) // Se o user que faz o pedido é criador, atribui 1
//                    .when(cb.equal(userJoin.get("email"), requestingUserEmail), 2) // Se o user que faz o pedido é participante, atribui 2
//                    .otherwise(3) // Se o user que faz o pedido não é nem criador nem participante, atribui 3
//                    .as(Integer.class); // Converte a Expression<Object> em uma Expression<Integer>
//
//            cq.orderBy(cb.asc(orderByExpression));
//        }
//
//        return predicates;
//    }

}
