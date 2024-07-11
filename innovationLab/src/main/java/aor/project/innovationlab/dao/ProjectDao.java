package aor.project.innovationlab.dao;

import aor.project.innovationlab.dto.response.PaginatedResponse;
import aor.project.innovationlab.dto.statistics.StatisticsDto;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.ProjectStatus;
import aor.project.innovationlab.enums.UserType;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.hibernate.stat.Statistics;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public List<ProjectEntity> getAllProjects() {
        try {
            return em.createNamedQuery("Project.getAllProjects", ProjectEntity.class).getResultList();
        } catch (Exception e) {
            return null;

        }
    }

    public ProjectUserEntity findProjectUserByProjectAndUserId(long p, long u) {
        try{
            return (ProjectUserEntity) em.createNamedQuery("ProjectUserEntity.findProjectUserByProjectAndUserId")
                    .setParameter("projectId", p)
                    .setParameter("userId", u)
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
                                                         UserType role,
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

        // Se o ID for fornecido, retorne o project com esse ID
        if (id != null) {
            ProjectEntity projectEntity = findProjectById(id);
            if(projectEntity != null){
                List<ProjectEntity> projects = new ArrayList<>();
                projects.add(projectEntity);

                PaginatedResponse<ProjectEntity> response = new PaginatedResponse<>();
                response.setResults(projects);
                response.setTotalPages(1);

                return response;
            }
        }

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
                cq.orderBy(order, cb.asc(orderByExpression));
            } else {
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

    private List<Predicate> createPredicatesWithoutJoin(CriteriaBuilder cb, CriteriaQuery<ProjectEntity> cq, Root<ProjectEntity> project, String name,
                                                        List<ProjectStatus> status, List<String> labs, String creatorEmail,
                                                        List<String> skills, List<String> interests, String participantEmail,
                                                        UserType role, String requestingUserEmail) {
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
                rolePredicate = userRoot.get("role").in(UserType.MANAGER, UserType.NORMAL);
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
                    .when(cb.equal(project.get(orderField), ProjectStatus.CANCELLED), 40)
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

    public List<ProjectEntity> getProjectsForInvitation(String creatorEmail, String userEmail) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProjectEntity> cq = cb.createQuery(ProjectEntity.class);
        Root<ProjectEntity> project = cq.from(ProjectEntity.class);

        // Filtra projetos pelo email do criador e ativos
        Predicate creatorPredicate = cb.equal(project.get("creator").get("email"), creatorEmail);
        Predicate activePredicate = cb.isTrue(project.get("active"));

        // Subquery para verificar se o usuário já está associado ao projeto
        Subquery<Long> userSubquery = cq.subquery(Long.class);
        Root<ProjectUserEntity> userRoot = userSubquery.from(ProjectUserEntity.class);
        userSubquery.select(userRoot.get("project").get("id"));
        Predicate userPredicate = cb.and(
                cb.equal(userRoot.get("user").get("email"), userEmail),
                cb.isTrue(userRoot.get("active"))
        );
        userSubquery.where(userPredicate);

        // Excluir projetos onde o usuário já está associado
        Predicate notAssociatedPredicate = cb.not(project.get("id").in(userSubquery));

        // Subquery para contar o número de usuários ativos em cada projeto que são USER ou MANAGER
        Subquery<Long> userCountSubquery = cq.subquery(Long.class);
        Root<ProjectUserEntity> userCountRoot = userCountSubquery.from(ProjectUserEntity.class);
        userCountSubquery.select(cb.count(userCountRoot.get("id")));
        Predicate projectPredicate = cb.equal(userCountRoot.get("project"), project);
        Predicate activeUserPredicate = cb.isTrue(userCountRoot.get("active"));
        Predicate roleUserPredicate = cb.or(
                cb.equal(userCountRoot.get("role"), UserType.NORMAL),
                cb.equal(userCountRoot.get("role"), UserType.MANAGER)
        );
        userCountSubquery.where(cb.and(projectPredicate, activeUserPredicate, roleUserPredicate));

        // Predicate para garantir que o projeto tem vagas
        Predicate hasVacancyPredicate = cb.lessThan(userCountSubquery.getSelection(), project.get("maxParticipants"));

        // Combina todos os predicados
        cq.where(cb.and(creatorPredicate, activePredicate, notAssociatedPredicate, hasVacancyPredicate));

        return em.createQuery(cq).getResultList();
    }


    public StatisticsDto getStatisticsByLab(Integer lab) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StatisticsDto> cq = cb.createQuery(StatisticsDto.class);
        Root<ProjectEntity> project = cq.from(ProjectEntity.class);

        // Subqueries to count specific project statuses
        Subquery<Long> readyProjects = cq.subquery(Long.class);
        Root<ProjectEntity> readyProjectRoot = readyProjects.from(ProjectEntity.class);
        readyProjects.select(cb.count(readyProjectRoot))
                .where(cb.equal(readyProjectRoot.get("lab").get("id"), lab),
                        cb.equal(readyProjectRoot.get("status"), ProjectStatus.READY));

        Subquery<Long> inProgressProjects = cq.subquery(Long.class);
        Root<ProjectEntity> inProgressProjectRoot = inProgressProjects.from(ProjectEntity.class);
        inProgressProjects.select(cb.count(inProgressProjectRoot))
                .where(cb.equal(inProgressProjectRoot.get("lab").get("id"), lab),
                        cb.equal(inProgressProjectRoot.get("status"), ProjectStatus.IN_PROGRESS));

        Subquery<Long> finishedProjects = cq.subquery(Long.class);
        Root<ProjectEntity> finishedProjectRoot = finishedProjects.from(ProjectEntity.class);
        finishedProjects.select(cb.count(finishedProjectRoot))
                .where(cb.equal(finishedProjectRoot.get("lab").get("id"), lab),
                        cb.equal(finishedProjectRoot.get("status"), ProjectStatus.FINISHED));

        Subquery<Long> cancelledProjects = cq.subquery(Long.class);
        Root<ProjectEntity> cancelledProjectRoot = cancelledProjects.from(ProjectEntity.class);
        cancelledProjects.select(cb.count(cancelledProjectRoot))
                .where(cb.equal(cancelledProjectRoot.get("lab").get("id"), lab),
                        cb.equal(cancelledProjectRoot.get("status"), ProjectStatus.CANCELLED));


        Expression<Long> daysBetween = cb.function(
                "DATEDIFF",
                Long.class,
                project.get("endDate"),
                project.get("startDate")
        );
        // Main query to fetch general statistics
        cq.select(cb.construct(StatisticsDto.class,
                cb.count(project.get("id")), // Total projects
                cb.avg(project.get("maxParticipants")), // Average participants
                readyProjects.getSelection(), // Ready projects
                inProgressProjects.getSelection(), // In progress projects
                finishedProjects.getSelection(), // Finished projects
                cancelledProjects.getSelection(), // Cancelled projects
                cb.avg(cb.diff(daysBetween, 1)) // Average execution time
        ));

        cq.where(cb.equal(project.get("lab").get("id"), lab));

        TypedQuery<StatisticsDto> query = em.createQuery(cq);

        return query.getSingleResult();
    }

    public List<UserEntity> findUsersByProjectId(long id) {
        try {
            return em.createNamedQuery("Project.findUsersByProjectId").setParameter("projectId", id).getResultList();
        } catch (Exception e) {
            return null;
        }
    }
}
