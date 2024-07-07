package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.ProjectUserEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.enums.UserType;
import jakarta.ejb.Stateless;

import java.util.ArrayList;
import java.util.List;

@Stateless
public class ProjectUserDao extends AbstractDao<ProjectUserEntity>{

    private static final long serialVersionUID = 1L;

    public ProjectUserDao() {
        super(ProjectUserEntity.class);
    }

//    public ProjectUserEntity findProjectUserIds(long projectid, long userid) {
//        try {
//            return (ProjectUserEntity) em.createNamedQuery("ProjectUser.findProjectUserIds").setParameter("projectid", projectid).setParameter("userid", userid)
//                    .getSingleResult();
//
//        } catch (Exception e) {
//            return null;
//        }
//    }

    public List<ProjectUserEntity> findProjectsByUserId(long userId) {
        try {
            return em.createQuery("SELECT pu FROM ProjectUserEntity pu WHERE pu.user.id = :userId", ProjectUserEntity.class)
                    .setParameter("userId", userId)
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public ProjectUserEntity findProjectUserByProjectIdAndUserId(long id, long id1) {
        try{
            return em.createQuery("SELECT pu FROM ProjectUserEntity pu WHERE pu.project.id = :projectId AND pu.user.id = :userId", ProjectUserEntity.class)
                    .setParameter("projectId", id)
                    .setParameter("userId", id1)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public ProjectUserEntity findProjectUserByToken(String tokenAuthorization) {
        try {
            return em.createQuery("SELECT pu FROM ProjectUserEntity pu WHERE pu.tokenAuthorization = :tokenAuthorization", ProjectUserEntity.class)
                    .setParameter("tokenAuthorization", tokenAuthorization)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<UserEntity> findUsersByProjectId(Long projectId) {
        try {
            return em.createQuery("SELECT pu.user FROM ProjectUserEntity pu WHERE pu.project.id = :projectId", UserEntity.class)
                    .setParameter("projectId", projectId)
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<ProjectUserEntity> findProjectUserByProjectId(Long projectId) {
        try {
            return em.createQuery(
                            "SELECT pu FROM ProjectUserEntity pu WHERE pu.project.id = :projectId AND pu.active = true AND (pu.role = :managerRole OR pu.role = :normalRole)",
                            ProjectUserEntity.class)
                    .setParameter("projectId", projectId)
                    .setParameter("managerRole", UserType.MANAGER)
                    .setParameter("normalRole", UserType.NORMAL)
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<ProjectUserEntity> findProjectUserByProjectIdAndRole(Long projectId, UserType userType) {
        try {
            return em.createQuery("SELECT pu FROM ProjectUserEntity pu WHERE pu.project.id = :projectId AND pu.role = :role AND pu.active = true", ProjectUserEntity.class)
                    .setParameter("projectId", projectId)
                    .setParameter("role", userType)
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public boolean isUserInProject(String userEmail, long id) {
        try {
            return em.createQuery("SELECT pu FROM ProjectUserEntity pu WHERE pu.project.id = :projectId AND pu.user.email = :userEmail AND pu.active = true", ProjectUserEntity.class)
                    .setParameter("projectId", id)
                    .setParameter("userEmail", userEmail)
                    .getSingleResult() != null;
        } catch (Exception e) {
            return false;
        }
    }
}
