package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.InterestDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.dao.UserInterestDao;
import aor.project.innovationlab.entity.InterestEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.entity.UserInterestEntity;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InterestBean {

    private static final long serialVersionUID = 1L;

    @EJB
    private InterestDao interestDao;

    @EJB
    private UserDao userDao;

    @EJB
    private UserInterestDao userInterestDao;

    public InterestBean() {
    }

    public void createInitialData() {
        addInterest("New Interest", "admin@admin");
        addInterest("Another Interest", "ricardo@ricardo");
        addInterest("Fallowing Interest", "joao@ajoao");
        addInterest("Interest", "admin@admin");
    }

    public void addInterest(String name, String userEmail) {
        InterestEntity interest = interestDao.findInterestByName(name);
        if(interest == null) {
            interest = new InterestEntity();
            interest.setName(name);
            interest.setActive(true);
            interestDao.persist(interest);
        }
        UserInterestEntity userInterest = new UserInterestEntity();
        UserEntity user = userDao.findUserByEmail(userEmail);
        if(user == null) {
            return;
        }
        userInterest.setUser(user);
        userInterest.setInterest(interest);
        userInterestDao.persist(userInterest);
    }

}
