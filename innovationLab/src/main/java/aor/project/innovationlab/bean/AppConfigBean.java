package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.AppConfigDao;
import aor.project.innovationlab.dao.SessionDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.entity.AppConfigEntity;
import aor.project.innovationlab.entity.SessionEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.enums.UserType;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;

@Stateless
public class AppConfigBean {

    @EJB
    private SessionDao sessionDao;
    @EJB
    private AppConfigDao appConfigDao;
    @EJB
    private UserDao userDao;

    public AppConfigBean() {
    }

    /**
     * Set the maximum number of users
     * @param token
     * @param maxUsers
     */
    public void setMaxUsers(String token, int maxUsers) {
        SessionEntity se = sessionDao.findSessionByToken(token);
        if (se == null) {
            return;
        }
        if (se.getUser().getRole() != UserType.ADMIN) {
            return;
        }

        AppConfigEntity appConfigEntity = appConfigDao.findLastConfig();
        appConfigEntity.setMaxUsers(maxUsers);
        appConfigEntity.setTimeOut(appConfigEntity.getTimeOut());
        appConfigEntity.setTimeOutAdmin(appConfigEntity.getTimeOutAdmin());
        appConfigEntity.setUser(se.getUser());
        appConfigDao.merge(appConfigEntity);
    }

    /**
     * Set the timeout for normal users
     * @param token
     * @param timeOut
     */
    public void setTimeOut(String token, int timeOut) {
        SessionEntity se = sessionDao.findSessionByToken(token);
        if (se == null) {
            return;
        }
        if (se.getUser().getRole() != UserType.ADMIN) {
            return;
        }

        AppConfigEntity appConfigEntity = appConfigDao.findLastConfig();
        appConfigEntity.setMaxUsers(appConfigEntity.getMaxUsers());
        appConfigEntity.setTimeOut(timeOut);
        appConfigEntity.setTimeOutAdmin(appConfigEntity.getTimeOutAdmin());
        appConfigEntity.setUser(se.getUser());
        appConfigDao.merge(appConfigEntity);
    }

    /**
     * Set the timeout for admin users
     * @param token
     * @param timeOutAdmin
     */
    public void setTimeOutAdmin(String token, int timeOutAdmin) {
        SessionEntity se = sessionDao.findSessionByToken(token);
        if (se == null) {
            return;
        }
        if (se.getUser().getRole() != UserType.ADMIN) {
            return;
        }

        AppConfigEntity appConfigEntity = appConfigDao.findLastConfig();
        appConfigEntity.setMaxUsers(appConfigEntity.getMaxUsers());
        appConfigEntity.setTimeOut(appConfigEntity.getTimeOut());
        appConfigEntity.setTimeOutAdmin(timeOutAdmin);
        appConfigEntity.setUser(se.getUser());
        appConfigDao.merge(appConfigEntity);
    }

    /**
     * Get the app config
     */
    public void createInitialData() {
        if (appConfigDao.findLastConfig() == null) {
            AppConfigEntity appConfigEntity = new AppConfigEntity();
            appConfigEntity.setMaxUsers(10);
            appConfigEntity.setTimeOut(60);
            appConfigEntity.setTimeOutAdmin(60);

            UserEntity userEntity = userDao.findUserByEmail("admin@admin");
            appConfigEntity.setUser(userEntity);

            appConfigDao.persist(appConfigEntity);
        }

    }
}
