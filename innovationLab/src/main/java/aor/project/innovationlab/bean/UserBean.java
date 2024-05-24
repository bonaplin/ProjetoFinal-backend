package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.dto.session.SessionLoginDto;
import aor.project.innovationlab.dto.user.UserChangePasswordDto;
import aor.project.innovationlab.dto.user.UserConfirmAccountDto;
import aor.project.innovationlab.dto.user.UserLogInDto;
import aor.project.innovationlab.dto.user.UserOwnerProfileDto;
import aor.project.innovationlab.email.EmailSender;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.UserType;
import aor.project.innovationlab.exception.UserCreationException;
import aor.project.innovationlab.utils.PasswordUtil;
import aor.project.innovationlab.utils.Color;
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
    InterestBean interestBean;

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

     * @return
     */
    public boolean createNewUser(UserLogInDto userLogInDto) {
        if(userLogInDto == null) {
            throw new IllegalArgumentException("Please fill all the fields.");
        }
        String email = userLogInDto.getEmail();
        String password = userLogInDto.getPassword();
        String confirmPassword = userLogInDto.getConfirmPassword();

        if(!password.equals(confirmPassword)){
            throw new UserCreationException("Passwords do not match.");
        }

        if (email == null || password == null) {
            throw new IllegalArgumentException("Email, password and confirmPassword cannot be null!");
        }

        if(userDao.findUserByEmail(email) != null){
            throw new UserCreationException("Email already exists.");
        }

        validateUserInput(email, password);

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
            throw new UserCreationException("Email or password cannot be null!");
        }
        if(!UserValidator.validateEmail(email)) {
            throw new UserCreationException("Invalid email format!");
        }
        if(!UserValidator.validatePassword(password)) {
            throw new UserCreationException("Invalid password format!");
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
        boolean validEmail = UserValidator.validateEmail(email);
        if (!validEmail) {
            throw new UserCreationException("Invalid email format.");
        }
        UserEntity userEntity = userDao.findUserByEmail(email);
        if (userEntity == null) {
            throw new UserCreationException("Email not found.");
        }
        generateVerificationToken(userEntity);
        try{
            userDao.merge(userEntity);
        } catch (Exception e) {
            throw new UserCreationException("Error sending email: " + e.getMessage());
        }
        EmailSender.sendPasswordResetEmail(email, userEntity.getTokenVerification());
        return true;
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
    private void generateVerificationToken(UserEntity userEntity) {
        String newToken = TokenUtil.generateToken();
        userEntity.setTokenVerification(newToken);
        userEntity.setTokenExpiration(generateExpirationDate());
        userDao.merge(userEntity);
        System.out.println("Token: "+ Color.GREEN +newToken+ Color.GREEN);
    }

    /**
     * Verify the user and change the password
     * @param token
     * @param dto - the dto with the password and confirm password
     * @return
     */
    public boolean changePassword(String token, UserChangePasswordDto dto) {
        UserEntity userEntity = userDao.findUserByToken(token);
        if (userEntity == null) return false;
        if(!verifyToken(token)) return false;

        if(!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new UserCreationException("Passwords do not match");
        }

        String password = dto.getPassword();
        System.out.println("Password: " + password);
        if(!UserValidator.validatePassword(password)) {
            throw new UserCreationException("Invalid password format");
        }

        userEntity.setPassword(PasswordUtil.hashPassword(password));
        userDao.merge(userEntity);
        try{
            cleanToken(userEntity);
        }
        catch (Exception e) {
            throw new UserCreationException("Error changing password: " + e.getMessage());
        }
        return true;
    }

    /**
     * Verifies the token
     * @param token
     * @return
     */
    private boolean verifyToken(String token) {
        UserEntity userEntity = userDao.findUserByToken(token);
        if (userEntity == null) return false;
        if (userEntity.getTokenExpiration().isBefore(Instant.now())) return false;
        return true;
    }

    /**
     * Cleans the token and the expiration date from the user
     * @param userEntity
     */
    private void cleanToken(UserEntity userEntity) {
        System.out.println("Cleaning token");
        userEntity.setTokenVerification(null);
        userEntity.setTokenExpiration(null);
        try{
            userDao.merge(userEntity);
        } catch (Exception e) {
            throw new UserCreationException("Error cleaning token: " + e.getMessage());
        }
    }

    public UserOwnerProfileDto getUserProfile(String token, String email) {
        UserEntity userEntity = userDao.findUserByEmail(email);
        SessionEntity sessionEntity = sessionDao.findSessionByToken(token);
        if (userEntity == null){
            throw new UserCreationException("User not found");
        }
        if(sessionEntity == null){
            throw new UserCreationException("Session not found");
        }

        long userId = sessionEntity.getUser().getId();

        UserOwnerProfileDto userOwnerProfileDto = new UserOwnerProfileDto();

        userOwnerProfileDto.setUsername(userEntity.getUsername());
        userOwnerProfileDto.setEmail(userEntity.getEmail());
        userOwnerProfileDto.setFirstname(userEntity.getFirstname());
        userOwnerProfileDto.setLastname(userEntity.getLastname());
        userOwnerProfileDto.setPhone(userEntity.getPhone());
        userOwnerProfileDto.setPrivateProfile(userEntity.getPrivateProfile());
        if(userEntity.getPrivateProfile() || userId == userEntity.getId()){
            userOwnerProfileDto.setRole(userEntity.getRole().getValue());
            userOwnerProfileDto.setSkills(skillBean.getUserSkills(email));
            userOwnerProfileDto.setLab(userEntity.getLab().getLocation());
            userOwnerProfileDto.setInterests(interestBean.getUserInterests(email));
        }

        System.out.println(userOwnerProfileDto);
        return userOwnerProfileDto;
    }


    public void confirmAccount(String token, UserConfirmAccountDto dto) {
        UserEntity userToConfirm = userDao.findUserByToken(token);
        if (userToConfirm == null) {
            System.out.println("User not found");
            throw new UserCreationException("User not found");
        }
        if(!verifyToken(token)) {
            System.out.println("Token expired");
            throw new UserCreationException("Token expired");
        }
        LabEntity lab = labDao.findLabById(dto.getLabId());
        if(lab == null) {
            throw new UserCreationException("Lab not found");
        }
        userToConfirm.setConfirmed(true);
        userToConfirm.setFirstname(dto.getFirstName());
        userToConfirm.setLastname(dto.getLastName());
        userToConfirm.setAbout(dto.getAbout());
        userToConfirm.setUsername(dto.getUsername());
        userToConfirm.setLab(lab);

        userDao.merge(userToConfirm);
        cleanToken(userToConfirm);
    }

    public SessionLoginDto loginWithValidation(UserLogInDto userLogInDto) throws Exception {
        if(userLogInDto == null){
            throw new Exception("Check the fields.");
        }
        if(userLogInDto.getEmail() == null || userLogInDto.getPassword() == null){
            throw new Exception("Email or password is null. Check the fields.");
        }
        SessionLoginDto sessionLoginDto = sessionBean.login(userLogInDto);
        if(sessionLoginDto == null){
            throw new Exception("Email and password do not match. Check the fields.");
        }
        return sessionLoginDto;
    }
}
