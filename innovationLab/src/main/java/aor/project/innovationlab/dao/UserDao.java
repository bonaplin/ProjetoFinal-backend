package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.ProjectUserType;
import aor.project.innovationlab.enums.UserType;
import aor.project.innovationlab.utils.InputSanitizerUtil;
import aor.project.innovationlab.validator.UserValidator;
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

    public List<UserEntity> findUsers(String username,
                                      String email,
                                      String firstname,
                                      String lastname,
                                      UserType role,
                                      Boolean active,
                                      Boolean confirmed,
                                      Boolean privateProfile,
                                      List<String> labs,
                                      List<String> skills,
                                      List<String> interests,
                                      Integer pageNumber,
                                      Integer pageSize) {

        System.out.println(pageNumber + " " + pageSize + " ");

        // Create query and add predicates
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserEntity> cq = cb.createQuery(UserEntity.class);
        Root<UserEntity> user = cq.from(UserEntity.class);
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(user.get("active"), true));
        // Only show public profiles when searching for skills or interests
        if ((skills != null && !skills.isEmpty()) || (interests != null && !interests.isEmpty())) {
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
            predicates.add(cb.equal(user.get("role"), role));
        }
        if (active != null) {
            predicates.add(cb.equal(user.get("active"), active));
        }
        if (confirmed != null) {
            predicates.add(cb.equal(user.get("confirmed"), confirmed));
        }
        if (privateProfile != null) {
            predicates.add(cb.equal(user.get("privateProfile"), privateProfile));
        }
        if (labs != null && !labs.isEmpty()) {
            predicates.add(user.get("lab").get("location").in(labs));
        }
        if (skills != null && !skills.isEmpty()) {
            Subquery<Long> skillSubquery = cq.subquery(Long.class);
            Root<UserSkillEntity> skillRoot = skillSubquery.from(UserSkillEntity.class);
            skillSubquery.select(skillRoot.get("user").get("id"));
            skillSubquery.where(skillRoot.get("skill").get("name").in(skills), cb.isTrue(skillRoot.get("skill").get("active")));
            skillSubquery.groupBy(skillRoot.get("user").get("id"));
            skillSubquery.having(cb.equal(cb.count(skillRoot), skills.size()));
            predicates.add(cb.in(user.get("id")).value(skillSubquery));
        }
        if(interests != null && !interests.isEmpty()){
            Subquery<Long> interestSubquery = cq.subquery(Long.class);
            Root<UserInterestEntity> interestRoot = interestSubquery.from(UserInterestEntity.class);
            interestSubquery.select(interestRoot.get("user").get("id"));
            interestSubquery.where(interestRoot.get("interest").get("name").in(interests), cb.isTrue(interestRoot.get("interest").get("active")));
            interestSubquery.groupBy(interestRoot.get("user").get("id"));
            interestSubquery.having(cb.equal(cb.count(interestRoot), interests.size()));
            predicates.add(cb.in(user.get("id")).value(interestSubquery));
        }

        // Add predicates to query and return result
        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<UserEntity> query = em.createQuery(cq);
        query.setFirstResult((pageNumber - 1) * pageSize);
        query.setMaxResults(pageSize);
        System.out.println(pageNumber + " final " + pageSize + " ");

        return query.getResultList();
    }
}

