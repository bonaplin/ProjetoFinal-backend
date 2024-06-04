package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.dto.lab.LabDto;
import aor.project.innovationlab.dto.session.SessionLoginDto;
import aor.project.innovationlab.dto.user.*;
import aor.project.innovationlab.email.EmailSender;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.ProjectUserType;
import aor.project.innovationlab.enums.UserType;
import aor.project.innovationlab.utils.Color;
import aor.project.innovationlab.utils.logs.LoggerUtil;
import aor.project.innovationlab.utils.PasswordUtil;
import aor.project.innovationlab.utils.TokenUtil;
import aor.project.innovationlab.validator.UserValidator;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TransactionRequiredException;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

//    convert entity to dto

    public UserDto toDto(UserEntity userEntity) {
        UserDto user = new UserDto();
        user.setId(userEntity.getId());
        user.setFirstname(userEntity.getFirstname());
        user.setLastname(userEntity.getLastname());
        user.setPrivateProfile(userEntity.getPrivateProfile());
        user.setImagePath(userEntity.getProfileImagePath());
        user.setEmail(userEntity.getEmail());
        user.setRole(userEntity.getRole().name());

        if(!userEntity.getPrivateProfile()) {
            user.setLablocation(userEntity.getLab().getLocation());

            user.setInterests(userEntity.getInterests().stream()
                    .map(UserInterestEntity::getInterest)
                    .map(InterestEntity::getName)
                    .collect(Collectors.toList()));

            user.setSkills(userEntity.getUserSkills().stream()
                    .map(UserSkillEntity::getSkill)
                    .map(SkillEntity::getName)
                    .collect(Collectors.toList()));
        }
        return user;
    }

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
        String log = "Attempt to create user if not exists";
        if(userDao.findUserByEmail(email) != null){
            LoggerUtil.logError(log,"Email already exists.",email,null);
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

        persistUser(user);
    }

    /**
     * Creates a new user with the given email and password
     * Try to persist the user in the database
     * If the email already exists, throws an exception

     * @return
     */
    public boolean createNewUser(UserLogInDto userLogInDto) {
        String log = "Attempt to create new user";
        if(userLogInDto == null) {
            LoggerUtil.logError(log,"UserLogInDto is null.",null,null);
            throw new IllegalArgumentException("Please fill all the fields.");
        }
        System.out.println(Color.PURPLE+userLogInDto.getEmail()+Color.PURPLE);
        String email = userLogInDto.getEmail();
        String password = userLogInDto.getPassword();
        String confirmPassword = userLogInDto.getConfirmPassword();

        if(!password.equals(confirmPassword)){
            LoggerUtil.logError(log,"Passwords do not match.",email,null);
            throw new IllegalArgumentException("Passwords do not match.");
        }

        if (email == null || password == null) {
            LoggerUtil.logError(log,"Email or password are null.",email,null);
            throw new IllegalArgumentException("Email, password and confirmPassword cannot be null!");
        }

        if(userDao.findUserByEmail(email) != null){
            LoggerUtil.logError(log,"Email already exists.",email,null);
            throw new IllegalArgumentException("Email already exists.");
        }

        validateUserInput(email, password);

        UserValidator.validatePassword(password);
        UserEntity user = createUserEntity(email, password);
        persistUser(user);
        System.out.println(Color.PURPLE+user.getEmail()+Color.PURPLE);
        
        String newToken = generateVerificationToken(user);
        EmailSender.sendVerificationEmail(email, newToken);
        return true;
    }

    private void persistUser(UserEntity user) {
        String log = "Attempt to persist user";
        try {
            userDao.persist(user);
            LoggerUtil.logInfo(log,"User created",user.getEmail(),null);
        } catch (IllegalArgumentException | TransactionRequiredException | EntityNotFoundException e) {
            LoggerUtil.logError(log,"Error creating user: " + e.getMessage(),user.getEmail(),null);
            throw new RuntimeException("Error creating user: " + e.getMessage());
        } catch (PersistenceException e) {
            LoggerUtil.logError(log,"Persistence error creating user: " + e.getMessage(),user.getEmail(),null);
            throw new RuntimeException("Persistence error creating user: " + e.getMessage());
        } catch (Exception e) {
            LoggerUtil.logError(log,"Unexpected error creating user: " + e.getMessage(),user.getEmail(),null);
            throw new RuntimeException("Unexpected error creating user: " + e.getMessage());
        }
    }

    /**
     * Validates the user input for creating a new user
     * @param email
     * @param password
     */
    private void validateUserInput(String email, String password) {
        String log = "Attempt to validate user input";
        if(email == null || password == null) {
            LoggerUtil.logError(log,"Email or password cannot be null!",email,null);
            throw new IllegalArgumentException("Email or password cannot be null!");
        }
        if(!UserValidator.validateEmail(email)) {
            LoggerUtil.logError(log,"Invalid email format!",email,null);
            throw new IllegalArgumentException("Invalid email format!");
        }
        if(!UserValidator.validatePassword(password)) {
            System.out.println(Color.RED+password+ Color.RED);
            LoggerUtil.logError(log,"Invalid password format!",email,null);
            throw new IllegalArgumentException("Invalid password format!");
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
        String log = "Attempt to send password reset email";
        boolean validEmail = UserValidator.validateEmail(email);
        if (!validEmail) {
            LoggerUtil.logError(log,"Invalid email format.",email,null);
            throw new IllegalArgumentException("Invalid email format.");
        }
        UserEntity userEntity = userDao.findUserByEmail(email);
        if (userEntity == null) {
            LoggerUtil.logError(log,"Email not found.",email,null);
            throw new IllegalArgumentException("Email not found.");
        }
        generateVerificationToken(userEntity);
        try{
            userDao.merge(userEntity);
        } catch (IllegalArgumentException | TransactionRequiredException | EntityNotFoundException e) {
            LoggerUtil.logError(log,"Error sending password reset email: " + e.getMessage(),email,null);
            throw new IllegalArgumentException("Error sending password reset email: " + e.getMessage());
        } catch (PersistenceException e) {
            LoggerUtil.logError(log,"Persistence error sending password reset email: " + e.getMessage(),email,null);
            throw new IllegalArgumentException("Persistence error sending password reset email: " + e.getMessage());
        } catch (Exception e) {
            LoggerUtil.logError(log,"Unexpected error sending password reset email: " + e.getMessage(),email,null);
            throw new IllegalArgumentException("Unexpected error sending password reset email: " + e.getMessage());
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
    private String generateVerificationToken(UserEntity userEntity) {
        String log = "Attempt to generate verification token";
        String newToken = TokenUtil.generateToken();
        userEntity.setTokenVerification(newToken);
        userEntity.setTokenExpiration(generateExpirationDate());
        userDao.merge(userEntity);
        LoggerUtil.logInfo(log,"Token generated",userEntity.getEmail(),newToken);
        return newToken;
    }

    /**
     * Verify the user and change the password
     * @param token
     * @param dto - the dto with the password and confirm password
     * @return
     */
    public boolean changePassword(String token, UserChangePasswordDto dto) {
        String log = "Attempt to change password";
        UserEntity userEntity = userDao.findUserByToken(token);
        if (userEntity == null) {
            LoggerUtil.logError(log,"User not found.",null,token);
            throw new IllegalArgumentException("User not found.");
        }
        if(!verifyToken(token)){
            LoggerUtil.logError(log,"Token expired.",userEntity.getEmail(),token);
            throw new IllegalArgumentException("Token expired.");
        }

        if(!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        String password = dto.getPassword();
        if(!UserValidator.validatePassword(password)) {
            throw new IllegalArgumentException("Invalid password format");
        }

        userEntity.setPassword(PasswordUtil.hashPassword(password));
        userDao.merge(userEntity);
        try{
            cleanToken(userEntity);
        } catch(IllegalArgumentException | TransactionRequiredException | EntityNotFoundException e) {
            throw new IllegalArgumentException("Error changing password: " + e.getMessage());
        } catch (PersistenceException e) {
            throw new IllegalArgumentException("Persistence error changing password: " + e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unexpected error changing password: " + e.getMessage());
        }
        return true;
    }

    /**
     * Verifies the token
     * @param token
     * @return
     */
    public boolean verifyToken(String token) {
        String log = "Attempt to verify token";
        UserEntity userEntity = userDao.findUserByToken(token);
        if (userEntity == null){
            LoggerUtil.logError(log,"User not found.",null,token);
            throw new IllegalArgumentException("User not found.");
        }
        if (userEntity.getTokenExpiration().isBefore(Instant.now())){
            LoggerUtil.logError(log,"Token expired.",userEntity.getEmail(),token);
            throw new IllegalArgumentException("Token expired.");
        }
        return true;
    }

    /**
     * Cleans the token and the expiration date from the user
     * @param userEntity
     */
    private void cleanToken(UserEntity userEntity) {
        String log = "Attempt to clean token";
        userEntity.setTokenVerification(null);
        userEntity.setTokenExpiration(null);
        try {
            userDao.merge(userEntity);
        } catch (IllegalArgumentException | TransactionRequiredException | EntityNotFoundException e) {
            LoggerUtil.logError(log,"Error cleaning token: " + e.getMessage(),userEntity.getEmail(),null);
            throw new IllegalArgumentException("Error cleaning token: " + e.getMessage());
        } catch (PersistenceException e) {
            LoggerUtil.logError(log,"Persistence error cleaning token: " + e.getMessage(),userEntity.getEmail(),null);
            throw new IllegalArgumentException("Persistence error cleaning token: " + e.getMessage());
        } catch (Exception e) {
            LoggerUtil.logError(log,"Unexpected error cleaning token: " + e.getMessage(),userEntity.getEmail(),null);
            throw new IllegalArgumentException("Unexpected error cleaning token: " + e.getMessage());
        }
    }

    public UserOwnerProfileDto getUserProfile(String token, String email) {
        String log = "Attempt to get user profile";
        UserEntity userEntity = userDao.findUserByEmail(email);
        SessionEntity sessionEntity = sessionDao.findSessionByToken(token);
        sessionBean.validateUserToken(token);
        if (userEntity == null){
            LoggerUtil.logError(log,"User not found.",email,token);
            throw new IllegalArgumentException("User not found");
        }
        if(sessionEntity == null){
            LoggerUtil.logError(log,"Session not found.",email,token);
            throw new IllegalArgumentException("Session not found");
        }

        long userId = sessionEntity.getUser().getId();

        UserOwnerProfileDto userOwnerProfileDto = new UserOwnerProfileDto();

        userOwnerProfileDto.setUsername(userEntity.getUsername());
//        userOwnerProfileDto.setEmail(userEntity.getEmail());
        userOwnerProfileDto.setFirstname(userEntity.getFirstname());
        userOwnerProfileDto.setLastname(userEntity.getLastname());
        userOwnerProfileDto.setImagePath(userEntity.getProfileImagePath());
        userOwnerProfileDto.setPrivateProfile(userEntity.getPrivateProfile());
        userOwnerProfileDto.setImagePath(userEntity.getProfileImagePath());
        if(!userEntity.getPrivateProfile() || userId == userEntity.getId()){
            userOwnerProfileDto.setRole(userEntity.getRole().getValue());
//            userOwnerProfileDto.setPhone(userEntity.getPhone());
            userOwnerProfileDto.setLab((userEntity.getLab().getId()));
            userOwnerProfileDto.setAbout(userEntity.getAbout());

        }
        LoggerUtil.logInfo(log,"User profile retrieved",email,token);
        return userOwnerProfileDto;
    }

    public void confirmAccount(String token, UserConfirmAccountDto dto) {
        String log = "Attempt to confirm account";
        UserEntity userToConfirm = userDao.findUserByToken(token);
        if (userToConfirm == null) {
            LoggerUtil.logError(log,"User not found.",null,token);
            throw new IllegalArgumentException("Your request isn't valid.");
        }
        // Campos obrigatórios
        if(dto.getFirstName()==null || dto.getLastName()==null || dto.getLabId() == 0){
            LoggerUtil.logError(log,"Required fields are empty.",userToConfirm.getEmail(),token);
            throw new IllegalArgumentException("Please fill out the required fields.");
        }
        if(!verifyToken(token)) {
            LoggerUtil.logError(log,"Token expired.",userToConfirm.getEmail(),token);
            throw new IllegalArgumentException("Token expired.");
        }

        LabEntity lab = labDao.findLabById(dto.getLabId());
        if(lab == null) {
            LoggerUtil.logError(log,"Lab not found with id: "+dto.getLabId(),userToConfirm.getEmail(),token);
            throw new IllegalArgumentException("Lab not found");
        }
        userToConfirm.setConfirmed(true);
        userToConfirm.setFirstname(dto.getFirstName());
        userToConfirm.setLastname(dto.getLastName());
        userToConfirm.setAbout(dto.getAbout());
        userToConfirm.setUsername(dto.getUsername());
        userToConfirm.setLab(lab);

        userDao.merge(userToConfirm);
        LoggerUtil.logInfo(log,"User account confirmed",userToConfirm.getEmail(),token);
        cleanToken(userToConfirm);
    }

    public SessionLoginDto loginWithValidation(UserLogInDto userLogInDto) {
        String log = "Attempt to login";
        if(userLogInDto == null){
            LoggerUtil.logError(log,"UserLogInDto is null.",null, null);
            throw new IllegalArgumentException("Fill the fields.");
        }
        if(userLogInDto.getEmail() == null || userLogInDto.getPassword() == null){
            LoggerUtil.logError(log,"Email or password is null.",null, null);
            throw new IllegalArgumentException("Email or password is null. Check the fields.");
        }
        SessionLoginDto sessionLoginDto = sessionBean.login(userLogInDto);
        if(sessionLoginDto == null){
            LoggerUtil.logError(log,"Email and password do not match.", userLogInDto.getEmail(), null);
            throw new IllegalArgumentException("Email and password do not match. Check the fields.");
        }
        LoggerUtil.logInfo(log, "User logged in", userLogInDto.getEmail(), sessionLoginDto.getToken());
        return sessionLoginDto;
    }

    public void updateUser(String token, UserOwnerProfileDto dto) {
        String log = "Attempt to update user";
        UserEntity userEntity = sessionDao.findSessionByToken(token).getUser();
        if (userEntity == null) {
            LoggerUtil.logError(log,"User not found.",null,token);
            throw new IllegalArgumentException("User not found.");
        }
        sessionBean.validateUserToken(token);
        if(dto.getUsername() != null){
            userEntity.setUsername(dto.getUsername());
        }
        if(dto.getFirstname() != null){
            userEntity.setFirstname(dto.getFirstname());
        }
        if(dto.getLastname() != null){
            userEntity.setLastname(dto.getLastname());
        }
        if(dto.getLab() != 0){
            LabEntity lab = labDao.findLabById(dto.getLab());
            if(lab == null){
                LoggerUtil.logError(log,"Lab not found with id: "+dto.getLab(),userEntity.getEmail(),token);
                throw new IllegalArgumentException("Lab not found.");
            }
            System.out.println(Color.PURPLE+"lab"+lab.getLocation()+Color.PURPLE);
            userEntity.setLab(lab);
        }
        if(dto.getAbout() != null){
            userEntity.setAbout(dto.getAbout());
        }
        if(dto.getImagePath() != null){
            userEntity.setProfileImagePath(dto.getImagePath());
        }
        userDao.merge(userEntity);
        LoggerUtil.logInfo(log,"User updated",userEntity.getEmail(),token);
    }

    public List<?> getUsers(String dtoType, String username, String email, String firstname, String lastname, UserType role, Boolean active, Boolean confirmed, Boolean privateProfile, Long labId) {
        String log = "Attempt to get users";

        List<UserEntity> users = userDao.findUsers(username, email, firstname, lastname, role,  active, confirmed, privateProfile, labId);


        if(dtoType == null || dtoType.isEmpty()) {
            dtoType = "UserDto";
        }
        switch (dtoType) {
            case "UserDto":
                return users.stream()
                        .map(this::toDto)
                        .collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }
    }
}
