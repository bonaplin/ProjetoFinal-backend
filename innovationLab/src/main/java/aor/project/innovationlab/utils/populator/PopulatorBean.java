package aor.project.innovationlab.utils.populator;

import aor.project.innovationlab.bean.UserBean;
import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.SkillType;
import aor.project.innovationlab.enums.UserType;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.*;

@Stateless
public class PopulatorBean {

    @EJB
    private UserDao userDao;

    @EJB
    private LabDao labDao;

    @EJB
    private InterestDao interestDao;

    @EJB
    private SkillDao skillDao;

    @EJB
    private UserSkillDao userSkillDao;

    @EJB
    private UserInterestDao userInterestDao;

    @Inject
    private UserBean userBean;

    public static <T> List<T> getRandomElements(List<T> list, int numElements) {
        Collections.shuffle(list);
        return list.subList(0, Math.min(numElements, list.size()));
    }

    public void addUser(PopulatorUserDto userDto) {
        Random random = new Random();
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userDto.getUsername());
        userEntity.setPassword(userDto.getPassword());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setFirstname(userDto.getFirstname());
        userEntity.setLastname(userDto.getLastname());
        userEntity.setPhone(userDto.getPhone());
        userEntity.setRole(UserType.NORMAL);
        userEntity.setActive(true);
        userEntity.setConfirmed(true);
        userEntity.setPrivateProfile(random.nextBoolean());
        List<?> labs = labDao.findAllLabs();
        LabEntity labEntity = (LabEntity) getRandomElements(labs, 1).get(0);
        userEntity.setLab(labEntity);
        userDao.persist(userEntity);
        assignRandomSkillsToUser(userEntity);
        assignRandomInterestsToUser(userEntity);
        userEntity.setProfileImagePath(userDto.getPhotoURL());
        userDao.merge(userEntity);
    }

    public void addSkills(String skill) {
        SkillType[] skillTypes = SkillType.values();
        List<SkillType> skillTypeList = new ArrayList<>(Arrays.asList(skillTypes));
        SkillType st = getRandomElements(skillTypeList,1).get(0);
        SkillEntity skillEntity = new SkillEntity();
        skillEntity.setName(skill);
        skillEntity.setSkillType(st);
        skillDao.persist(skillEntity);
    }

    public void addInterest(String interest) {
        InterestEntity ie = new InterestEntity();
        ie.setName(interest);
        interestDao.persist(ie);
    }

    public void assignRandomSkillsToUser(UserEntity userEntity) {
        List<SkillEntity> allSkills = skillDao.getAllSkills();
        int numSkills = new Random().nextInt(allSkills.size()) + 1;
        List<SkillEntity> selectedSkills = getRandomElements(allSkills, numSkills);
        for (SkillEntity skill : selectedSkills) {
            UserSkillEntity userSkillEntity = new UserSkillEntity();
            userSkillEntity.setUser(userEntity);
            userSkillEntity.setSkill(skill);
            userSkillDao.persist(userSkillEntity);
        }
    }

    public void assignRandomInterestsToUser(UserEntity userEntity) {
        List<InterestEntity> allInterests = interestDao.getAllInterests();
        int numInterests = new Random().nextInt(allInterests.size()) + 1;
        List<InterestEntity> selectedInterests = getRandomElements(allInterests, numInterests);
        for (InterestEntity interest : selectedInterests) {
            UserInterestEntity userInterestEntity = new UserInterestEntity();
            userInterestEntity.setUser(userEntity);
            userInterestEntity.setInterest(interest);
            userInterestDao.persist(userInterestEntity);
        }
    }


}
