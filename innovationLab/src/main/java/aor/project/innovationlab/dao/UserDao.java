package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.LabEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.entity.UserSkillEntity;
import aor.project.innovationlab.enums.ProjectUserType;
import aor.project.innovationlab.enums.UserType;
import aor.project.innovationlab.utils.InputSanitizerUtil;
import aor.project.innovationlab.validator.UserValidator;
import jakarta.ejb.Stateless;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

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
                                      Long labId) {


        // Validate inputs
        username = InputSanitizerUtil.sanitizeInput(username);
        email = InputSanitizerUtil.sanitizeInput(email);
        firstname = InputSanitizerUtil.sanitizeInput(firstname);
        lastname = InputSanitizerUtil.sanitizeInput(lastname);

        // Validate email
        if(email != null && !UserValidator.validateEmail(email)){
            throw new IllegalArgumentException("Invalid email");
        }

        // Create query and add predicates
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserEntity> cq = cb.createQuery(UserEntity.class);
        Root<UserEntity> user = cq.from(UserEntity.class);
        List<Predicate> predicates = new ArrayList<>();
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
        if(labId != null && labId > 0L && em.find(LabEntity.class, labId) != null){
            predicates.add(cb.equal(user.get("lab").get("id"), labId));
        }

        // Add predicates to query and return result
        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }
}

