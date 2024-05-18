package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.dto.UserDto;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.SkillType;
import aor.project.innovationlab.enums.UserType;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserBean {

    @EJB
    UserDao userDao;

    @EJB
    LabDao labDao;

    @EJB
    SkillDao skillDao;

    @EJB
    UserInterestDao userInterestDao;

    @EJB
    InterestDao interestDao;

    @Inject
    SkillBean skillBean;

    //convert dto to entity
    public UserEntity toEntity(UserDto userDto) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(userDto.getUsername());
        userEntity.setPassword(userDto.getPassword());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setFirstname(userDto.getFirstname());
        userEntity.setLastname(userDto.getLastname());
        userEntity.setPhone(userDto.getPhone());
        userEntity.setActive(Boolean.parseBoolean(userDto.getActive()));
        userEntity.setConfirmed(Boolean.parseBoolean(userDto.getConfirmed()));
        userEntity.setRole(userDto.getRole());
        LabEntity labEntity = labDao.findLabByLocation(userDto.getLablocation());
        userEntity.setLab(labEntity);
        return userEntity;
    }

    //convert entity to dto
    public UserDto toDto(UserEntity userEntity) {
        UserDto userDto = new UserDto();
        userDto.setId(userEntity.getId());
        userDto.setUsername(userEntity.getUsername());
        userDto.setPassword(userEntity.getPassword());
        userDto.setEmail(userEntity.getEmail());
        userDto.setFirstname(userEntity.getFirstname());
        userDto.setLastname(userEntity.getLastname());
        userDto.setPhone(userEntity.getPhone());
        userDto.setActive(userEntity.getActive().toString());
        userDto.setConfirmed(userEntity.getConfirmed().toString());
        userDto.setRole(userEntity.getRole());
        userDto.setLablocation(userEntity.getLab().getLocation());
        return userDto;
    }

    public void createInitialData() {
        createUserIfNotExists("admin@admin","admin");
        createUserIfNotExists("ricardo@ricardo","ricardo");
        createUserIfNotExists("joao@joao","joao");
    }

    public void createUserIfNotExists(String email, String username){
        if(userDao.findUserByEmail(email) != null){
            return;
        }
        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setUsername(username);
        userDto.setPassword(username);
        userDto.setFirstname(username);
        userDto.setLastname(username);
        userDto.setPhone(username);
        userDto.setActive("true");
        userDto.setConfirmed("true");
        userDto.setRole(UserType.ADMIN.name());
        userDto.setLablocation("Coimbra");
        UserEntity user = toEntity(userDto);
        userDao.persist(user);

        //ADD_SKILL_TO_USER
        skillBean.addSkillToUser(email, "Java");
        skillBean.addSkillToUser(email, "Assembly");
        skillBean.addSkillToUser(email, "macOS");
    }

    public boolean addUser(UserDto userDto) {
        UserEntity userEntity = toEntity(userDto);
        userDao.persist(userEntity);
        return true;
    }


}
