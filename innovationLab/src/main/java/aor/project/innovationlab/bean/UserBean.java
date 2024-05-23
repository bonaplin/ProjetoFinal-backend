package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.email.EmailSender;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.UserType;
import aor.project.innovationlab.exception.UserCreationException;
import aor.project.innovationlab.utils.PasswordUtil;
import aor.project.innovationlab.utils.TokenUtil;
import aor.project.innovationlab.validator.UserValidator;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Duration;
import java.time.Instant;

@ApplicationScoped
public class UserBean {

    @EJB
    UserDao userDao;

    @EJB
    LabDao labDao;

    @EJB
    SessionDao sessionDao;

    @Inject
    SkillBean skillBean;

    @Inject
    SessionBean sessionBean;

    public static int DEFAULT_TOKEN_VERIFICATION_EXPIRATION_HOURS = 1;

    //convert dto to entity
//    public UserEntity toEntity(UserDto userDto) {
//        UserEntity userEntity = new UserEntity();
//        userEntity.setUsername(userDto.getUsername());
//        userEntity.setPassword(userDto.getPassword());
//        userEntity.setEmail(userDto.getEmail());
//        userEntity.setFirstname(userDto.getFirstname());
//        userEntity.setLastname(userDto.getLastname());
//        userEntity.setPhone(userDto.getPhone());
//        userEntity.setActive(Boolean.parseBoolean(userDto.getActive()));
//        userEntity.setConfirmed(Boolean.parseBoolean(userDto.getConfirmed()));
//        userEntity.setRole(userDto.getRole());
//        LabEntity labEntity = labDao.findLabByLocation(userDto.getLablocation());
//        userEntity.setLab(labEntity);
//        return userEntity;
//    }

    //convert entity to dto
//    public UserDto toDto(UserEntity userEntity) {
//        UserDto userDto = new UserDto();
//        userDto.setId(userEntity.getId());
//        userDto.setUsername(userEntity.getUsername());
//        userDto.setPassword(userEntity.getPassword());
//        userDto.setEmail(userEntity.getEmail());
//        userDto.setFirstname(userEntity.getFirstname());
//        userDto.setLastname(userEntity.getLastname());
//        userDto.setPhone(userEntity.getPhone());
//        userDto.setActive(userEntity.getActive().toString());
//        userDto.setConfirmed(userEntity.getConfirmed().toString());
//        userDto.setRole(userEntity.getRole());
//        userDto.setLablocation(userEntity.getLab().getLocation());
//        return userDto;
//    }

    /**
     * Creates the initial data for the application
     */
    public void createInitialData() {
        createUserIfNotExists("admin@admin","admin");
        createUserIfNotExists("ricardo@ricardo","ricardo");
        createUserIfNotExists("joao@joao","joao");
    }

    /**
     * Creates a new user with the given email and password
     * Try to persist the user in the database
     * If the email already exists, throws an exception
     * @param email
     * @param username
     */
    public void createUserIfNotExists(String email, String username){
        if(userDao.findUserByEmail(email) != null){
            return;
        }
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(PasswordUtil.hashPassword(username));
        user.setFirstname(username);
        user.setLastname(username);
        user.setPhone(username);
        user.setActive(true);
        user.setConfirmed(true);
        user.setRole(UserType.ADMIN);
        LabEntity labEntity = labDao.findLabByLocation("Coimbra");
        user.setLab(labEntity);
        userDao.persist(user);

        //ADD_SKILL_TO_USER
        skillBean.addSkillToUser(email, "Java");
        skillBean.addSkillToUser(email, "Assembly");
        skillBean.addSkillToUser(email, "macOS");
    }

    /**
     * Creates a new user with the given email and password
     * Try to persist the user in the database
     * If the email already exists, throws an exception
     * @param email
     * @param password
     * @return
     */
    public boolean createNewUser(String email, String password) {
        validateUserInput(email, password);

        if(userDao.findUserByEmail(email) != null){
            throw new UserCreationException("Email already exists");
        }

        UserEntity user = createUserEntity(email, password);

        try {
            userDao.persist(user);
        } catch (Exception e) {
            throw new UserCreationException("Error creating user: " + e.getMessage());
        }
        generateVerificationToken(user);
        EmailSender.sendVerificationEmail(email, user.getTokenVerification());
        return true;
    }

    /**
     * Validates the user input for creating a new user
     * @param email
     * @param password
     */
    private void validateUserInput(String email, String password) {
        if(email == null || password == null) {
            throw new UserCreationException("Email or password cannot be null");
        }
        if(!UserValidator.validateEmail(email)) {
            throw new UserCreationException("Invalid email format");
        }
        if(!UserValidator.validatePassword(password)) {
            throw new UserCreationException("Invalid password format");
        }
    }

    /**
     * Creates a new UserEntity with the given email and password
     * @param email
     * @param password
     * @return
     */
    private UserEntity createUserEntity(String email, String password) {
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword(PasswordUtil.hashPassword(password));
        user.setActive(true);
        user.setConfirmed(false);
        user.setRole(UserType.USER);
        return user;
    }

    /**
     * Sends an email to the user with the password reset link
     * @param email
     * @return
     */
    public boolean sendPasswordResetEmail(String email) {
        UserEntity userEntity = userDao.findUserByEmail(email);
        if (userEntity != null) {
            generateVerificationToken(userEntity);
            try{
            userDao.merge(userEntity);
            } catch (Exception e) {
                throw new UserCreationException("Error sending email: " + e.getMessage());
            }
            EmailSender.sendPasswordResetEmail(email, userEntity.getTokenVerification());
            return true;
        }
        return false;
    }

    /**
     * Generates the expiration date for the token based on the current date
     * @return
     */
    private Instant generateExpirationDate() {
        return Instant.now().plus(Duration.ofHours(DEFAULT_TOKEN_VERIFICATION_EXPIRATION_HOURS));
    }

    /**
     * Generates a new verification token for the user
     * @param userEntity - the user to generate the token for
     */
    public void generateVerificationToken(UserEntity userEntity) {
        String newToken = TokenUtil.generateToken();
        userEntity.setTokenVerification(newToken);
        userEntity.setTokenExpiration(generateExpirationDate());
        userDao.merge(userEntity);
    }


}
