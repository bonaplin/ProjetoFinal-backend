package aor.project.innovationlab.utils.populator;

import aor.project.innovationlab.bean.UserBean;
import aor.project.innovationlab.dao.LabDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.dto.user.UserDto;
import aor.project.innovationlab.entity.LabEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.enums.UserType;
import aor.project.innovationlab.utils.PasswordUtil;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.Collections;
import java.util.List;

@Stateless
public class PopulatorBean {

    @EJB
    private UserDao userDao;

    @EJB
    private LabDao labDao;

    @Inject
    private UserBean userBean;

    public static <T> List<T> getRandomElements(List<T> list, int numElements) {
        Collections.shuffle(list);
        return list.subList(0, Math.min(numElements, list.size()));
    }

    public void addUser(PopulatorUserDto userDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userDto.getUsername());
        userEntity.setPassword(userDto.getPassword());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setFirstname(userDto.getFirstname());
        userEntity.setLastname(userDto.getLastname());
        userEntity.setPhone(userDto.getPhone());
        userEntity.setRole(UserType.ADMIN);
        userEntity.setActive(true);
        userEntity.setConfirmed(true);
        userEntity.setPrivateProfile(false);
        List<?> labs = labDao.findAllLabs();
        LabEntity labEntity = (LabEntity) getRandomElements(labs, 1).get(0);
        userEntity.setLab(labEntity);
        userDao.persist(userEntity);

        userEntity.setProfileImagePath(userDto.getPhotoURL());
        userDao.merge(userEntity);
    }

    public void addSkills(String skill) {


    }


}
