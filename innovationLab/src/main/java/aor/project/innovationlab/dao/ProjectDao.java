package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.ProjectStatus;
import aor.project.innovationlab.enums.ProjectUserType;
import aor.project.innovationlab.utils.InputSanitizerUtil;
import aor.project.innovationlab.utils.PasswordUtil;
import aor.project.innovationlab.validator.UserValidator;
import jakarta.ejb.Stateless;
import jakarta.persistence.criteria.*;

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

    public List<ProjectEntity> findProjects(String name,
                                            ProjectStatus status,
                                            Long labId,
                                            String creatorEmail,
                                            String skill,
                                            String interest,
                                            String participantEmail,
                                            ProjectUserType role,
                                            String requestingUserEmail) {

        //Validate inputs
        name = InputSanitizerUtil.sanitizeInput(name);
        creatorEmail = InputSanitizerUtil.sanitizeInput(creatorEmail);
        skill = InputSanitizerUtil.sanitizeInput(skill);
        interest = InputSanitizerUtil.sanitizeInput(interest);
        participantEmail = InputSanitizerUtil.sanitizeInput(participantEmail);


        //Validate email
        if(creatorEmail != null && !UserValidator.validateEmail(creatorEmail)){
            throw new IllegalArgumentException("Invalid creator email");
        }
        if(participantEmail != null && !UserValidator.validateEmail(participantEmail)){
            throw new IllegalArgumentException("Invalid participant email");
        }

        //Create query and add predicates
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProjectEntity> cq = cb.createQuery(ProjectEntity.class);
        Root<ProjectEntity> project = cq.from(ProjectEntity.class);
        List<Predicate> predicates = new ArrayList<>();
        if (name != null) {
            predicates.add(cb.equal(project.get("name"), name));
        }
        if (status != null) {
            predicates.add(cb.equal(project.get("status"), status));
        }
        if (labId != null && labId > 0L && em.find(LabEntity.class, labId) != null) {
            predicates.add(cb.equal(project.get("lab").get("id"), labId));
        }
        if (creatorEmail != null) {
            predicates.add(cb.equal(project.get("creator").get("email"), creatorEmail));
        }
        if (skill != null) {
            Join<ProjectEntity, ProjectSkillEntity> skillJoin = project.join("projectSkills");
            predicates.add(cb.equal(skillJoin.get("skill").get("name"), skill));
        }
        if (interest != null) {
            Join<ProjectEntity, ProjectInterestEntity> interestJoin = project.join("projectInterests");
            predicates.add(cb.equal(interestJoin.get("interest").get("name"), interest));
        }
        if (participantEmail != null) {
            Join<ProjectEntity, ProjectUserEntity> userJoin = project.join("projectUsers");
            predicates.add(cb.equal(userJoin.get("user").get("email"), participantEmail));
            predicates.add(userJoin.get("role").in(ProjectUserType.MANAGER, ProjectUserType.NORMAL));
        }

        //Order by creator, participant, other
        Join<ProjectEntity, ProjectUserEntity> projectUserJoin = project.join("projectUsers");
        Join<ProjectUserEntity, UserEntity> userJoin = projectUserJoin.join("user");

        //Create expression to order by creator, participant, other
        Expression<Integer> orderByExpression = cb.<Integer>selectCase()
                .when(cb.equal(project.get("creator").get("email"), requestingUserEmail), 1) // Se o user que faz o pedido é criador, atribui 1
                .when(cb.equal(userJoin.get("email"), requestingUserEmail), 2) // Se o user que faz o pedido é participante, atribui 2
                .otherwise(3) // Se o user que faz o pedido não é nem criador nem participante, atribui 3
                .as(Integer.class); // Converte a Expression<Object> em uma Expression<Integer>

        cq.orderBy(cb.asc(orderByExpression));
        //Add predicates to query and return result
        cq.where(predicates.toArray(new Predicate[0]));
        return em.createQuery(cq).getResultList();
    }

}
