package aor.project.innovationlab.dao;

import aor.project.innovationlab.entity.ProjectUserEntity;
import jakarta.ejb.Stateless;

@Stateless
public class ProjectUserDao extends AbstractDao<ProjectUserEntity>{

    private static final long serialVersionUID = 1L;

    public ProjectUserDao() {
        super(ProjectUserEntity.class);
    }

    public ProjectUserEntity findProjectUserIds(long projectid, long userid) {
        try {
            return (ProjectUserEntity) em.createNamedQuery("ProjectUser.findProjectUserIds").setParameter("projectid", projectid).setParameter("userid", userid)
                    .getSingleResult();

        } catch (Exception e) {
            return null;
        }
    }



}
