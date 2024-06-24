package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.ProjectUserEntity;
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
}
