package aor.project.innovationlab.dao;


import aor.project.innovationlab.entity.AppConfigEntity;
import jakarta.ejb.Stateless;

@Stateless
public class AppConfigDao extends AbstractDao<AppConfigEntity> {
private static final long serialVersionUID = 1L;

    public AppConfigDao() {
        super(AppConfigEntity.class);
    }

    public AppConfigEntity findLastConfig() {
        try {
            return (AppConfigEntity) em.createNamedQuery("AppConfig.findLastConfig")
                    .setMaxResults(1)
                    .getResultList()
                    .stream()
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return new AppConfigEntity();
        }
    }

    public AppConfigEntity getMaxUsersAllowed() {
        try {
            return (AppConfigEntity) em.createNamedQuery("AppConfig.getMaxUsersAllowed")
                    .setMaxResults(1)
                    .getResultList()
                    .stream()
                    .findFirst()
                    .orElse(null);
        }
        catch (Exception e) {
            return new AppConfigEntity();
        }
    }

}
