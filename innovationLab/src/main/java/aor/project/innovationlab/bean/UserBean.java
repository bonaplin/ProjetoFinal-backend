package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.LabDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.dao.UserSkillDao;
import aor.project.innovationlab.dto.UserDto;
import aor.project.innovationlab.entity.LabEntity;
import aor.project.innovationlab.entity.SkillEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.entity.UserSkillEntity;
import aor.project.innovationlab.enums.SkillType;
import aor.project.innovationlab.enums.UserType;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserBean {

    @EJB
    UserDao userDao;

    @EJB
    LabDao labDao;

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
        createUserIfNotExists("admin@admin");
    }

    public void createUserIfNotExists(String email){
        if(userDao.findUserByEmail(email) != null){
            return;
        }
        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setUsername("admin");
        userDto.setPassword("admin");
        userDto.setFirstname("admin");
        userDto.setLastname("admin");
        userDto.setPhone("123456789");
        userDto.setActive("true");
        userDto.setConfirmed("true");
        userDto.setRole(UserType.ADMIN.name());
        userDto.setLablocation("Coimbra");
        UserEntity entity = toEntity(userDto);
        userDao.persist(entity);
    }

    public boolean addUser(UserDto userDto) {
        UserEntity userEntity = toEntity(userDto);
        userDao.persist(userEntity);
        return true;
    }

}
