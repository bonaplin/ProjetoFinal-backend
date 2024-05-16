package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.SkillDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.dao.UserSkillDao;
import aor.project.innovationlab.entity.SkillEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.entity.UserSkillEntity;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserSkillBean {

    @EJB
    private UserDao userDao;

    @EJB
    private UserSkillDao userSkillDao;

    @EJB
    private SkillDao skillDao;

    public void createInitialData() {
        UserEntity user = userDao.findUserByEmail("admin@admin");
        SkillEntity skill = skillDao.findSkillByName("Java");
        if(user == null || skill == null) {
            return;
        }
        if(userSkillDao.userHasSkill(user, skill)){
            return;
        }
        addSkillToUser(user, skill);

    }

    public void addSkillToUser(UserEntity user, SkillEntity skill) {
        if(user == null || skill == null) {
            return;
        }
        UserSkillEntity entity = new UserSkillEntity();
        entity.setUser(user);
        entity.setSkill(skill);
        userSkillDao.persist(entity);
    }

}