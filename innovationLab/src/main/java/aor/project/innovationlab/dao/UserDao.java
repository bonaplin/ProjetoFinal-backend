package aor.project.innovationlab.dao;

import aor.project.innovationlab.dto.response.PaginatedResponse;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.UserType;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class UserDao extends AbstractDao<UserEntity> {

    private static final long serialVersionUID = 1L;

    public UserDao() {
        super(UserEntity.class);
    }

    public UserEntity findUserByEmail(String email) {
        try {
            return (UserEntity) em.createNamedQuery("User.findUserByEmail").setParameter("email", email)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public UserEntity findUserById(Long additionalExecutorId) {
        try {
            return (UserEntity) em.createNamedQuery("User.findUserById").setParameter("id", additionalExecutorId)
                    .getSingleResult();

        } catch (NoResultException e) {
            return null;
        }
    }

    public UserSkillEntity findUserSkillIds(long id, int id1) {
        try {
            return (UserSkillEntity) em.createNamedQuery("UserSkill.findUserSkillIds")
                    .setParameter("user", id)
                    .setParameter("skill", id1)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public UserEntity findUserByToken(String token) {
        try {
            return (UserEntity) em.createNamedQuery("User.findUserByToken").setParameter("token", token)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public PaginatedResponse<UserEntity> findUsers(Long id,
                                                    String username,
                                                   String email,
                                                   String firstname,
                                                   String lastname,
                                                   String role,
                                                   Boolean active,
                                                   Boolean confirmed,
                                                   Boolean privateProfile,
                                                   List<String> labs,
                                                   List<String> skills,
                                                   List<String> interests,
                                                   Integer pageNumber,
                                                   Integer pageSize,
                                                   String orderField,
                                                   String orderDirection) {

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // Construct the main query
        CriteriaQuery<UserEntity> cq = cb.createQuery(UserEntity.class);
        Root<UserEntity> user = cq.from(UserEntity.class);
        List<Predicate> predicates = createPredicates(cb, user, username, email, firstname, lastname, role, active, confirmed, privateProfile, labs, skills, interests);

        cq.where(predicates.toArray(new Predicate[0]));
        Order order = getOrder(cb, user, orderField, orderDirection);

        // Adicione a ordem à consulta
        if (order != null) {
            cq.orderBy(order);
        }
        // Se um ID foi fornecido, retorne o user com esse ID
        if (id != null) {
            UserEntity userentity = em.find(UserEntity.class, id);
            if (userentity != null) {
                List<UserEntity> users = new ArrayList<>();
                users.add(userentity);

                // Construct the response
                PaginatedResponse<UserEntity> response = new PaginatedResponse<>();
                response.setResults(users);
                response.setTotalPages(1); // Como apenas um usuário é retornado, o total de páginas é 1

                return response;
            }
        }

        TypedQuery<UserEntity> query = em.createQuery(cq);
        query.setFirstResult((pageNumber - 1) * pageSize);
        query.setMaxResults(pageSize);
        List<UserEntity> users = query.getResultList();

        // Construct the count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<UserEntity> countRoot = countQuery.from(UserEntity.class);
        List<Predicate> countPredicates = createPredicates(cb, countRoot, username, email, firstname, lastname, role, active, confirmed, privateProfile, labs, skills, interests);

        countQuery.select(cb.count(countRoot));
        countQuery.where(countPredicates.toArray(new Predicate[0]));
        Long count = em.createQuery(countQuery).getSingleResult();
        int totalPages = (int) Math.ceil((double) count / pageSize);

        // Construct the response
        PaginatedResponse<UserEntity> response = new PaginatedResponse<>();
        response.setResults(users);
        response.setTotalPages(totalPages);

        return response;
    }

    public List<UserEntity> findUsersByProjectId(Long projectId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserEntity> cq = cb.createQuery(UserEntity.class);
        Root<ProjectUserEntity> root = cq.from(ProjectUserEntity.class);


        Join<ProjectUserEntity, UserEntity> userJoin = root.join("user");


        cq.where(cb.equal(root.get("project").get("id"), projectId));


        cq.select(userJoin);


        List<UserEntity> users = em.createQuery(cq).getResultList();

        return users;
    }

    /**
     * Find all users that are active and have a role of MANAGER or NORMAL in a project
     * @param projectId - the ID of the project
     * @return - a list of users that are active and have a role of MANAGER or NORMAL in the project
     */
    public List<UserEntity> findUsersByProjectIdActives(Long projectId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserEntity> cq = cb.createQuery(UserEntity.class);
        Root<ProjectUserEntity> root = cq.from(ProjectUserEntity.class);
        Join<ProjectUserEntity, UserEntity> userJoin = root.join("user");

        // Adiciona as condições de filtro
        Predicate projectCondition = cb.equal(root.get("project").get("id"), projectId); // Filtra pelo ID do projeto
        Predicate activeCondition = cb.isTrue(root.get("active")); // Filtra apenas users ativos
        Predicate roleCondition = root.get("role").in(UserType.MANAGER, UserType.NORMAL); // Filtra users com função MANAGER ou NORMAL

        // Combina todas as condições
        cq.where(cb.and(projectCondition, activeCondition, roleCondition));
        cq.select(userJoin);

        // Executa a consulta e retorna a lista de usuários
        return em.createQuery(cq).getResultList();
    }

    private Order getOrder(CriteriaBuilder cb, Root<UserEntity> user, String orderField, String orderDirection) {
        List<Order> orders = new ArrayList<>();

        // Ordenar por username
        if (orderField != null && orderField.equals("username") && orderDirection != null) {
            if (orderDirection.equalsIgnoreCase("asc")) {
                orders.add(cb.asc(user.get("username")));
            } else if (orderDirection.equalsIgnoreCase("desc")) {
                orders.add(cb.desc(user.get("username")));
            }
        }

        // Ordenar por firstname
        if (orderField != null && orderField.equals("firstname") && orderDirection != null) {
            if (orderDirection.equalsIgnoreCase("asc")) {
                orders.add(cb.asc(user.get("firstname")));
            } else if (orderDirection.equalsIgnoreCase("desc")) {
                orders.add(cb.desc(user.get("firstname")));
            }
        }

        // Ordenar por privateProfile (publicos primeiro e privados depois)
        orders.add(cb.asc(user.get("privateProfile")));

        // Retorna a primeira ordem na lista. Se a lista estiver vazia, retorna null.
        return orders.isEmpty() ? null : orders.get(0);
    }

    private List<Predicate> createPredicates(CriteriaBuilder cb, Root<UserEntity> user, String username, String email,
                                             String firstname, String lastname, String role, Boolean active,
                                             Boolean confirmed, Boolean privateProfile, List<String> labs,
                                             List<String> skills, List<String> interests) {
        List<Predicate> predicates = new ArrayList<>();
//        predicates.add(cb.isFalse(user.get("privateProfile")));
        predicates.add(cb.equal(user.get("active"), true));
        // Only show public profiles when searching for skills or interests
        if ((skills != null && !skills.isEmpty()) || (interests != null && !interests.isEmpty()) ||
                (labs != null && !labs.isEmpty() && (username != null || email != null || firstname != null || lastname != null || role != null || active != null || confirmed != null))) {
            predicates.add(cb.isFalse(user.get("privateProfile")));
        }
        if (username != null) {
            predicates.add(cb.equal(user.get("username"), username));
        }
        if (email != null) {
            predicates.add(cb.equal(user.get("email"), email));
        }
        if (firstname != null) {
            predicates.add(cb.equal(user.get("firstname"), firstname));
        }
        if (lastname != null) {
            predicates.add(cb.equal(user.get("lastname"), lastname));
        }
        if (role != null) {
            predicates.add(cb.equal(user.get("role"), UserType.fromString(role)));
        }
        if (confirmed != null) {
            predicates.add(cb.equal(user.get("confirmed"), confirmed));
        }
        if (labs != null && !labs.isEmpty()) {
            predicates.add(user.get("lab").get("location").in(labs));
        }
        if (skills != null && !skills.isEmpty()) {
            Subquery<Long> skillSubquery = cb.createQuery().subquery(Long.class);
            Root<UserSkillEntity> skillRoot = skillSubquery.from(UserSkillEntity.class);
            skillSubquery.select(skillRoot.get("user").get("id"));
            skillSubquery.where(skillRoot.get("skill").get("name").in(skills), cb.isTrue(skillRoot.get("skill").get("active")));
            skillSubquery.groupBy(skillRoot.get("user").get("id"));
            skillSubquery.having(cb.equal(cb.count(skillRoot), skills.size()));
            predicates.add(cb.in(user.get("id")).value(skillSubquery));
        }
        if (interests != null && !interests.isEmpty()) {
            Subquery<Long> interestSubquery = cb.createQuery().subquery(Long.class);
            Root<UserInterestEntity> interestRoot = interestSubquery.from(UserInterestEntity.class);
            interestSubquery.select(interestRoot.get("user").get("id"));
            interestSubquery.where(interestRoot.get("interest").get("name").in(interests), cb.isTrue(interestRoot.get("interest").get("active")));
            interestSubquery.groupBy(interestRoot.get("user").get("id"));
            interestSubquery.having(cb.equal(cb.count(interestRoot), interests.size()));
            predicates.add(cb.in(user.get("id")).value(interestSubquery));
        }

        return predicates;
    }

    public UserEntity findUserByUsername(String username) {
        try {
            return (UserEntity) em.createNamedQuery("User.findUserByUsername").setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}

