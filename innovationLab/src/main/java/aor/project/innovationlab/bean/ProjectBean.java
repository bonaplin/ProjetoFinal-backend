package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.dto.lab.LabDto;
import aor.project.innovationlab.dto.project.notes.NoteIdNoteDto;
import aor.project.innovationlab.dto.response.IdNameDto;
import aor.project.innovationlab.dto.response.LabelValueDto;
import aor.project.innovationlab.dto.response.PaginatedResponse;
import aor.project.innovationlab.dto.interests.InterestDto;
import aor.project.innovationlab.dto.product.ProductToCreateProjectDto;
import aor.project.innovationlab.dto.project.*;
import aor.project.innovationlab.dto.project.filter.FilterOptionsDto;
import aor.project.innovationlab.dto.response.ResponseYesNoInviteDto;
import aor.project.innovationlab.dto.skill.SkillDto;
import aor.project.innovationlab.dto.statistics.StatisticsDto;
import aor.project.innovationlab.dto.statistics.UserSettingsDto;
import aor.project.innovationlab.dto.task.TaskDto;
import aor.project.innovationlab.dto.user.UserAddToProjectDto;
import aor.project.innovationlab.dto.user.UserImgCardDto;
import aor.project.innovationlab.dto.project.ProjectSideBarDto;
import aor.project.innovationlab.dto.user.project.UserChangeRoleDto;
import aor.project.innovationlab.dto.user.project.UserInviteDto;
import aor.project.innovationlab.dto.user.project.UserToChangeRoleKickDto;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.*;
import aor.project.innovationlab.exception.CustomExceptions;
import aor.project.innovationlab.utils.Color;
import aor.project.innovationlab.utils.InputSanitizerUtil;
import aor.project.innovationlab.utils.TokenUtil;
import aor.project.innovationlab.utils.logs.LoggerUtil;
import aor.project.innovationlab.validator.UserValidator;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.hibernate.stat.Statistics;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Bean for managing projects
 */
@Stateless
public class ProjectBean {

    @EJB
    private ProjectDao projectDao;

    @EJB
    private InterestDao interestDao;

    @EJB
    private ProjectInterestDao projectInterestDao;

    @EJB
    private UserDao userDao;

    @EJB
    private LabDao labDao;

    @EJB
    private SessionDao sessionDao;

    @EJB
    private ProductDao productDao;

    @EJB
    private TaskExecutorDao taskExecutorDao;

    @EJB
    private ProjectUserDao projectUserDao;

    @EJB
    private ProjectProductDao projectProductDao;

    @EJB
    private ProjectSkillDao projectSkillDao;

    @EJB
    private SkillDao skillDao;

    @EJB
    private TaskDao taskDao;

    @EJB
    private LogDao logDao;

    @EJB
    private AppConfigDao appConfigDao;

    @Inject
    private MessageBean messageBean;

    @Inject
    private NotificationBean notificationBean;

    @Inject
    private LogBean logBean;

    @Inject
    private SessionBean sessionBean;

    @Inject
    private InterestBean interestBean;

    @Inject
    private SkillBean skillBean;

    @Inject
    private EmailBean emailBean;

    @Inject
    private WebSocketBean webSocketBean;

    @Inject
    private TaskBean taskBean;

    public ProjectBean() {
    }

    public void toEntity(ProjectDto dto) {
    }

    /**
     * Convert ProjectEntity to ProjectDto
     * @param entity - entidade do projeto
     * @return - dto do projeto
     */
    private ProjectDto toDto(ProjectEntity entity) {
        ProjectDto dto = new ProjectDto();
        dto.setName(entity.getName());
        dto.setActive(entity.isActive());
        dto.setDescription(entity.getDescription());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setFinishDate(entity.getFinishDate());
        dto.setStatus(entity.getStatus().toString());
        dto.setLab_id(entity.getLab().getId());
        return dto;
    }

    private ProjectReadyDto toProjectReadyDto(ProjectEntity entity) {
        ProjectReadyDto dto = new ProjectReadyDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLab(entity.getLab().getLocation());
        dto.setStatus(entity.getStatus().getValue());
        return dto;
    }

    /**
     * Convert ProjectEntity to ProjectCardDto, is used to show the project in a card
     * @param entity
     * @return
     */
    private ProjectCardDto toCardDto(ProjectEntity entity) {

        ProjectCardDto dto = new ProjectCardDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getName());
        dto.setStatus(entity.getStatus());
        dto.setDescription(entity.getDescription());
        dto.setStartDate(entity.getStartDate());
        dto.setMaxParticipants(entity.getMaxParticipants());
        dto.setKeywords(entity.getProjectInterests().stream()
                .map(ProjectInterestEntity::getInterest) // Mapeia para InterestEntity
                .map(InterestEntity::getName) // Obtém o nome
                .collect(Collectors.toList()));

        dto.setSkills(entity.getProjectSkills().stream()
                .map(ProjectSkillEntity::getSkill) // Mapeia para SkillEntity
                .map(SkillEntity::getName) // Obtém o nome
                .collect(Collectors.toList()));

        List<UserEntity> participants = projectUserDao.countActiveUsersByProjectIds(entity.getId());
        List<UserImgCardDto> users = participants.stream()
                .map(userEntity -> {
                    UserImgCardDto userImgCardDto = new UserImgCardDto();
                    userImgCardDto.setId(userEntity.getId());
                    userImgCardDto.setImagePath(userEntity.getProfileImagePath());
                    return userImgCardDto;
                })
                .collect(Collectors.toList());

        dto.setProjectUsers(users);

        return dto;
    }


    /**
     * Convert ProjectEntity to ProjectSideBarDto, is used to show the project in a sidebar
     * @param entity - entidade do projeto
     * @return - dto do projeto
     */
    private ProjectSideBarDto toSideBarDto(ProjectEntity entity) {
        ProjectSideBarDto dto = new ProjectSideBarDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setStatus(entity.getStatus());
        return dto;
    }

    /**
     * Adiciona um interesse a um projeto
     * @param projectName - nome do projeto
     * @param interestName - nome do interesse
     */
    public void addInterestToProject(String projectName, String interestName) {
        ProjectEntity project = projectDao.findProjectByName(projectName);
        if(project == null) {
            return;
        }
        InterestEntity interest = interestDao.findInterestByName(interestName);
        if(interest == null) {
            interest = new InterestEntity();
            interest.setName(interestName);
            interestDao.persist(interest);
        }
        ProjectInterestEntity projectInterest = new ProjectInterestEntity();
        projectInterest.setProject(project);
        projectInterest.setInterest(interest);
        projectInterestDao.persist(projectInterest);

        // Adiciona o interesse ao array de interesses do projeto
        project.getInterests().add(projectInterest);
        projectDao.merge(project);
    }

    /**
     * Remove um interesse de um projeto
     * @param projectName - nome do projeto
     * @param interestName - nome do interesse
     */
    public void removeInterestFromProject(String projectName, String interestName) {
        ProjectEntity project = projectDao.findProjectByName(projectName);
        if(project == null) {
            return;
        }
        InterestEntity interest = interestDao.findInterestByName(interestName);
        if(interest == null) {
            return;
        }
        ProjectInterestEntity projectInterest = projectInterestDao.findProjectInterestIds(project.getId(), interest.getId());
        if(projectInterest == null) {
            return;
        }
        projectInterest.setActive(false);
        projectInterestDao.merge(projectInterest);

        // Remove o interesse do array de interesses do projeto
        project.getInterests().remove(projectInterest);
        projectDao.merge(project);
    }

    public void createInitialData() {
        createProjectIfNotExists("Project1", "Description 1", "admin@admin", "Coimbra");
        createProjectIfNotExists("Project2", "Description 2", "ricardo@ricardo", "Porto");
        createProjectIfNotExists("Project3", "Description 3", "admin@admin", "Lisboa");
        createProjectIfNotExists("Project4", "Description 4", "ricardo@ricardo", "Coimbra");
    }

    public void createProjectIfNotExists(String name, String description, String creatorEmail, String location){
        if (projectDao.findProjectByName(name) == null) {
            ProjectEntity project = new ProjectEntity();
            project.setName(name);
            project.setDescription(description);
            project.setCreator(userDao.findUserByEmail(creatorEmail));
            project.setStartDate(LocalDate.now());
            project.setEndDate(LocalDate.now().plusDays(20));
            project.setFinishDate(LocalDate.now().plusDays(30));
            project.setLab(labDao.findLabByLocation(location));
            project.setSystemName(taskBean.taskSystemNameGenerator(name));
            projectDao.persist(project);
            createFinalTaskForProject(project);

            // create the entity project - creator
            addUserToProject(name, creatorEmail, UserType.MANAGER);

            addInterestToProject(name, "Interest1");
            addInterestToProject(name, "Interest2");

            addResourceToProject(name,"123456788");
            addResourceToProjectByNames(name, "Product 6");
            addResourceToProjectByNames(name, "Product 2");
            addResourceToProjectByNames(name, "Product 3");

            addUserToProject(name, "joao@joao", UserType.NORMAL);
            addSkillToProject(name, "Assembly");
            addSkillToProject(name, "macOS");
            addSkillToProject(name, "IntelIJ");
            addInterestToProject(name, "Interest3");
            addInterestToProject(name, "Interest4");
            addInterestToProject(name, "Interest5");
            addInterestToProject(name, "Interest6");
//            messageBean.sendMessage("admin@admin", name, "Hello, this is a message by Admin");
//            messageBean.sendMessage("ricardo@ricardo", name, "Hello, this is a message by Ricardo");
            sendrandommessages(project.getId());
            ProjectEntity pe = projectDao.findProjectByName(name);
            notificationBean.sendNotification("admin@admin", "ricardo@ricardo", "Invite to "+pe.getName(), NotificationType.INVITE, pe.getId());
            notificationBean.sendNotification("joao@joao", "ricardo@ricardo", "Invite to "+pe.getName(), NotificationType.INVITE, pe.getId());

            //TESTE - add log ao add user
            logBean.addNewUser(project.getId(), userDao.findUserByEmail("admin@admin").getId(), userDao.findUserByEmail("ricardo@ricardo").getId());
        }
    }

    public void sendrandommessages (long id){
        ProjectEntity project = projectDao.findProjectById(id);
        Set<ProjectUserEntity> pu = project.getProjectUsers();

        List<ProjectUserEntity> userList = new ArrayList<>(pu);

        Random r = new Random();

        int numMessages = r.nextInt(userList.size()) + 20;

        for (int i = 0; i < numMessages; i++) {
            // Selecione um usuário aleatório
            UserEntity randomUser = userList.get(r.nextInt(userList.size())).getUser();

            // Gere uma mensagem aleatória
            String randomMessage = "Hello, this is a random message for user " + randomUser.getEmail();

            // Envie a mensagem
            messageBean.sendMessage(randomUser.getEmail(), id, randomMessage);
        }

    }

    /**
     * Cria um novo projeto
     * @param token
     * @param createProjectDto
     */
    public void createProject(String token, CreateProjectDto createProjectDto) {
        String log = "Creating new project";
        if (token == null) {
            LoggerUtil.logError(log, "Token is required", null, null);
            throw new IllegalArgumentException("Token is required");
        }

        SessionEntity session = sessionDao.findSessionByToken(token);
        if (session == null) {
            LoggerUtil.logError(log, "Invalid token", null, token);
            throw new IllegalArgumentException("Invalid token");
        }

        if (createProjectDto == null) {
            LoggerUtil.logError(log, "More data is required", null, null);
            throw new IllegalArgumentException("More data is required");
        }

        ProjectEntity project = new ProjectEntity();

        if (createProjectDto.getName() == null || createProjectDto.getName().isEmpty()) {
            LoggerUtil.logError(log, "Project name is required", null, null);
            throw new IllegalArgumentException("Project name is required");
        } else {
            project.setName(createProjectDto.getName());
        }

        if (createProjectDto.getDescription() == null || createProjectDto.getDescription().isEmpty()) {
            LoggerUtil.logError(log, "Project description is required", null, null);
            throw new IllegalArgumentException("Project description is required");
        } else {
            project.setDescription(createProjectDto.getDescription());
        }

        if (createProjectDto.getStartDate() == null) {
            LoggerUtil.logError(log, "Project start date is required", null, null);
            throw new IllegalArgumentException("Project start date is required");
        } else {
            project.setStartDate(createProjectDto.getStartDate());
        }

        if (createProjectDto.getEndDate() == null) {
            LoggerUtil.logError(log, "Project end date is required", null, null);
            throw new IllegalArgumentException("Project end date is required");
        } else {
            project.setEndDate(createProjectDto.getEndDate());
        }

        if (createProjectDto.getLab_id() == 0) {
            LoggerUtil.logError(log, "Project lab is required", null, null);
            throw new IllegalArgumentException("Project lab is required");
        } else {
            int labId = (int) createProjectDto.getLab_id();
            project.setLab(labDao.findLabById(labId));
        }

        project.setCreatedDate(createProjectDto.getStartDate());

        project.setCreator(session.getUser());
        project.setStatus(ProjectStatus.READY);
        project.setSystemName(taskBean.taskSystemNameGenerator(createProjectDto.getName()));

        projectDao.persist(project);

        addingUsersToCreatedProject(session, project, createProjectDto);

        if (createProjectDto.getResources() != null) {
            for (ProductToCreateProjectDto productDto : createProjectDto.getResources()) {
                ProductEntity productEntity = productDao.findProductById(productDto.getId());
                if (productEntity != null) {
                    ProjectProductEntity projectProductEntity = new ProjectProductEntity();
                    projectProductEntity.setProject(project);
                    projectProductEntity.setProduct(productEntity);
                    projectProductEntity.setStatus(ProductStatus.STOCK);
                    projectProductEntity.setQuantity(productDto.getQuantity());
                    projectProductDao.persist(projectProductEntity);
                }
            }
        }

        if (createProjectDto.getKeywords() != null) {
            for (InterestDto keyword : createProjectDto.getKeywords()) {
                InterestEntity interestEntity = interestDao.findInterestByName(keyword.getName());
                if (interestEntity != null) {

                    ProjectInterestEntity projectInterestEntity = new ProjectInterestEntity();
                    projectInterestEntity.setProject(project);
                    projectInterestEntity.setInterest(interestEntity);
                    projectInterestDao.persist(projectInterestEntity);
                }
            }
        } else {
            LoggerUtil.logError(log, "At least one project keyword is required", session.getUser().getEmail(), token);
            throw new IllegalArgumentException("At least one project keyword is required");
        }

        if (createProjectDto.getSkills() != null) {
            for (SkillDto skill : createProjectDto.getSkills()) {
                SkillEntity skillEntity = skillDao.findSkillByName(skill.getName());
                if (skillEntity != null) {

                    ProjectSkillEntity projectSkillEntity = new ProjectSkillEntity();
                    projectSkillEntity.setProject(project);
                    projectSkillEntity.setSkill(skillEntity);
                    projectSkillDao.persist(projectSkillEntity);
                }
            }
        }
        createFinalTaskForProject(project);
    }

    private void addingUsersToCreatedProject(SessionEntity session, ProjectEntity project, CreateProjectDto createProjectDto) {
        ProjectUserEntity projectUserEntity = new ProjectUserEntity();
        projectUserEntity.setProject(project);
        projectUserEntity.setUser(session.getUser());
        projectUserEntity.setRole(UserType.MANAGER);
        projectUserEntity.setActive(true);
        projectUserDao.persist(projectUserEntity);

        if (createProjectDto.getUsers() != null) {

            if (createProjectDto.getUsers().size() > project.getMaxParticipants()) {
                throw new IllegalArgumentException("Cannot add more users than the maximum allowed participants for the project");
            }

            for (UserAddToProjectDto userDto : createProjectDto.getUsers()) {
                UserEntity userEntity = userDao.findUserById(userDto.getUserId());
                if (userEntity != null) {

                    if (project.getProjectUsers().size() >= project.getMaxParticipants()) {
                        throw new IllegalArgumentException("Project already has the maximum number of participants");
                    }

                    ProjectUserEntity invitedUserEntity = new ProjectUserEntity();
                    invitedUserEntity.setProject(project);
                    invitedUserEntity.setUser(userEntity);
                    invitedUserEntity.setRole(UserType.NORMAL);// when a user is invited to a project in the creation, he is a normal user
                    invitedUserEntity.setActive(true);
                    projectUserDao.persist(invitedUserEntity);
                }
            }
        }
    }

    /**
     * Adiciona um recurso a um projeto
     * @param projectName - nome do projeto
     * @param productIdentifier - identificador do recurso
     */
    public void addResourceToProject(String projectName, String productIdentifier) {
        ProjectEntity project = projectDao.findProjectByName(projectName);
        if(project == null) {
            return;
        }
        ProductEntity product = productDao.findProductByIdentifier(productIdentifier);
        if(product == null) {
            return;
        }
        ProjectProductEntity projectProduct = new ProjectProductEntity();
        projectProduct.setProject(project);
        projectProduct.setProduct(product);
        projectProduct.setStatus(ProductStatus.STOCK);
        projectProduct.setQuantity(1);
        projectProductDao.persist(projectProduct);

        // Adiciona o recurso ao array de recursos do projeto
        project.getProjectProducts().add(projectProduct);
        projectDao.merge(project);
    }

    public void addResourceToProjectByNames(String projectName, String productName) {
        ProjectEntity project = projectDao.findProjectByName(projectName);
        if(project == null) {
            return;
        }
        ProductEntity product = productDao.findProductByName(productName);
        if(product == null) {
            return;
        }
        ProjectProductEntity projectProduct = new ProjectProductEntity();
        projectProduct.setProject(project);
        projectProduct.setProduct(product);
        projectProduct.setStatus(ProductStatus.STOCK);
        projectProduct.setQuantity(1);
        projectProductDao.persist(projectProduct);

        // Adiciona o recurso ao array de recursos do projeto
        project.getProjectProducts().add(projectProduct);
        projectDao.merge(project);
    }

    /**
     * Remove um recurso de um projeto
     * @param projectName - nome do projeto
     * @param productIdentifier - identificador do recurso
     */
    public void removeResourceFromProject(String projectName, String productIdentifier) {
        ProjectEntity project = projectDao.findProjectByName(projectName);
        if(project == null) {
            return;
        }
        ProductEntity product = productDao.findProductByIdentifier(productIdentifier);
        if(product == null) {
            return;
        }
        ProjectProductEntity projectProduct = projectProductDao.findProjectProductIds(project.getId(), product.getId());
        if(projectProduct == null) {
            return;
        }
        projectProduct.setActive(false);
        projectProductDao.merge(projectProduct);

        // Remove o recurso do array de recursos do projeto
        project.getProjectProducts().remove(projectProduct);
        projectDao.merge(project);
    }

    /**
     * Add user to project with role
     * @param projectName - name of the project
     * @param userEmail - email of the user
     * @param role - role of the user
     */
    public void addUserToProject(String projectName, String userEmail, UserType role) {
        ProjectEntity project = projectDao.findProjectByName(projectName);
        if(project == null) {
            return;
        }
        UserEntity user = userDao.findUserByEmail(userEmail);
        if(user == null) {
            return;
        }

        ProjectUserEntity projectUser = new ProjectUserEntity();
        projectUser.setProject(project);
        projectUser.setUser(user);
        projectUser.setRole(role);
        projectUser.setActive(true);
        projectUserDao.persist(projectUser);

        // Adiciona o usuário ao array de usuários do projeto
        project.getProjectUsers().add(projectUser);
        projectDao.merge(project);
    }

    /**
     * Add skill to project
     * @param projectName - name of the project
     * @param skillName - name of the skill
     */
    public void addSkillToProject(String projectName, String skillName) {
        ProjectEntity project = projectDao.findProjectByName(projectName);
        if(project == null) {
            return;
        }
        SkillEntity skill = skillDao.findSkillByName(skillName);
        if(skill == null) {
            return;
        }

        ProjectSkillEntity projectSkill = projectSkillDao.findProjectSkillByProjectIdAndSkillId(project.getId(), skill.getId());
        if(projectSkill != null) {
            projectSkill.setActive(true);
        }
        else{
            projectSkill = new ProjectSkillEntity();
            projectSkill.setProject(project);
            projectSkill.setSkill(skill);
            projectSkillDao.persist(projectSkill);
        }

        // Adiciona o skill ao array de skills do projeto
        project.getProjectSkills().add(projectSkill);
        projectDao.merge(project);
        projectSkillDao.merge(projectSkill); // Adicione esta linha
    }

    public void removeSkillFromProject(String projectName, String skillName) {
        ProjectEntity project = projectDao.findProjectByName(projectName);
        if(project == null) {
            return;
        }
        SkillEntity skill = skillDao.findSkillByName(skillName);
        if(skill == null) {
            return;
        }

        ProjectSkillEntity projectSkill = projectSkillDao.findProjectSkillByProjectIdAndSkillId(project.getId(), skill.getId());
        if(projectSkill != null) {
            projectSkill.setActive(false);
            projectSkillDao.merge(projectSkill);

            // Remove o skill do array de skills do projeto
            project.getProjectSkills().remove(projectSkill);
            projectDao.merge(project);
        }
    }

    /**
     * Method to get the skills, interests statuses and labs to filter the projects
     * @param token - user token
     * @return - dto with the filter options
     */
    public FilterOptionsDto filterOptions(String token){
        sessionBean.validateUserToken(token);
        FilterOptionsDto dto = new FilterOptionsDto();
        List<SkillDto> skills = skillDao.getAllSkills().stream()
                .map(skillBean::toDto)
                .collect(Collectors.toList());
        List<InterestDto> interests = interestDao.getAllInterests().stream()
                .map(interestBean::toDto)
                .collect(Collectors.toList());

        List<IdNameDto> statuses = Arrays.stream(ProjectStatus.values())
                .map(status -> new IdNameDto(status.getValue(), status.name()))
                .collect(Collectors.toList());

        List<IdNameDto> labs = labDao.findAllLabs().stream()
                .map(lab -> new IdNameDto(lab.getId(), lab.getLocation()))
                .collect(Collectors.toList());

        dto.setInterests(interests);
        dto.setSkills(skills);
        dto.setStatuses(statuses);
        dto.setLabs(labs);

        return dto;
    }


//    public void alertWsProjectIsOpen(String token, Long projectId) {
//        System.out.println("Alerting WS that project is open");
//        SessionEntity se = sessionDao.findSessionByToken(token);
//        if(se == null) {
//            return;
//        }
//
//        ProjectEntity project = projectDao.findProjectById(projectId);
//        if (project == null) {
//            return;
//        }
//
//        ProjectUserEntity pue = projectUserDao.findProjectUserByProjectIdAndUserId(projectId, se.getUser().getId());
//
//        if (pue == null) {
//            return;
//        }
//
//        String userEmail = se.getUser().getEmail();
//
//        webSocketBean.isProjectWindowOpen(userEmail, projectId);
//    }

    /**
     * Method to get the projects by a specific dto
     * @param dtoType - type of dto
     * @param name - name of the project
     * @param status - status of the project
     * @param lab - lab of the project
     * @param creatorEmail - email of the creator
     * @param skill - skills of the project
     * @param interest - interests of the project
     * @param participantEmail - email of the participant
     * @param role - role of the user
     * @param orderField - field to order
     * @param orderDirection - direction to order
     * @param auth - user token
     * @param pageNumber - page number
     * @param pageSize - page size
     * @param id - id of the project
     * @return - paginated response with the projects
     */
    public PaginatedResponse<Object> getProjectsByDto(String dtoType, String name,
                                                      List<ProjectStatus> status,
                                                      List<String> lab, String creatorEmail,
                                                      List<String> skill, List<String> interest,
                                                      String participantEmail,
                                                      UserType role, String orderField, String orderDirection,
                                                      String auth, Integer pageNumber, Integer pageSize, Long id) {

        String userEmail = null;

        if(auth != null){
            SessionEntity se = sessionDao.findSessionByToken(auth);
            if(se != null) {
                userEmail = se.getUser().getEmail();
            }
            else{
               throw new IllegalArgumentException("Sorry, you are not authorized to access this resource.");
            }
        }

        if (lab != null && !lab.isEmpty()) {
            for (String l : lab) {
                LabEntity labEntity = labDao.findLabByName(l);
                if (labEntity == null) {
                    throw new IllegalArgumentException("Lab with id " + l + " does not exist.");
                }
            }
        }

        if(pageNumber == null || pageNumber < 0){
            pageNumber = 1;
        }
        if(pageSize == null || pageSize < 0){
            pageSize = 10;
        }
        if (skill != null) {
            skill = skill.stream()
                    .map(InputSanitizerUtil::sanitizeInput)
                    .collect(Collectors.toList());
        }

        if (interest != null) {
            interest = interest.stream()
                    .map(InputSanitizerUtil::sanitizeInput)
                    .collect(Collectors.toList());
        }

        //Validate email
        if(creatorEmail != null && !UserValidator.validateEmail(creatorEmail)){
            throw new IllegalArgumentException("Invalid creator email");
        }
        if(participantEmail != null && !UserValidator.validateEmail(participantEmail)){
            throw new IllegalArgumentException("Invalid participant email");
        }
        if(dtoType == null || dtoType.isEmpty()) {
            dtoType = "ProjectCardDto";
        }

        //Validate inputs
        name = InputSanitizerUtil.sanitizeInput(name);
        creatorEmail = InputSanitizerUtil.sanitizeInput(creatorEmail);
        participantEmail = InputSanitizerUtil.sanitizeInput(participantEmail);

        if(orderDirection != null && orderField != null){
            orderDirection = orderDirection.toLowerCase();
            orderField = orderField.toLowerCase();
            if(orderField.equals("createddate")){
                orderField = "createdDate";
            }
            validateOrderParameters(orderField, orderDirection,userEmail, auth);
        }

        PaginatedResponse<ProjectEntity> projectsResponse = projectDao.findProjects(name, status, lab, creatorEmail, skill, interest, participantEmail, role, userEmail, id, pageNumber, pageSize, orderField, orderDirection);
        List<ProjectEntity> projects = projectsResponse.getResults();

        PaginatedResponse<Object> response = new PaginatedResponse<>();
        response.setTotalPages(projectsResponse.getTotalPages());

        if (userEmail != null && id != null) {
            UserEntity user = userDao.findUserByEmail(userEmail);
            if (user != null) {
                ProjectUserEntity pue = projectUserDao.findProjectUserByProjectIdAndUserId(id, user.getId());
                if (pue != null) {
                    if (pue.isActive()) {
                        webSocketBean.openProjectWindow(auth, id);
                        response.setUserType(pue.getRole().getValue());
                    } else {
                        response.setUserType(UserType.GUEST.getValue());
                    }

                    if (pue.isActive() && (pue.getRole() == UserType.MANAGER || pue.getRole() == UserType.NORMAL)) {
                        response.setUserType(pue.getRole().getValue());
                    } else {
                        response.setUserType(UserType.GUEST.getValue());
                    }
                } else {
                    response.setUserType(UserType.GUEST.getValue());
                }
            } else {
                response.setUserType(UserType.GUEST.getValue());
            }
        } else {
            response.setUserType(UserType.GUEST.getValue());
        }

        UserEntity admin = userDao.findUserByEmail(userEmail);
        if (admin.getRole().equals(UserType.ADMIN)) {
            response.setUserType(UserType.ADMIN.getValue());
        }


        switch (dtoType) {
            case "ProjectCardDto":
                response.setResults(projects.stream()
                        .map(this::toCardDto)
                        .collect(Collectors.toList()));
                break;
            case "ProjectDto":
                response.setResults(projects.stream()
                        .map(this::toDto)
                        .collect(Collectors.toList()));
                break;
            case "ProjectSideBarDto":
                response.setResults(projects.stream()
                        .map(this::toSideBarDto)
                        .collect(Collectors.toList()));
                break;
            default:
                response.setResults(new ArrayList<>());
        }
        return response;
    }

    /**
     * Validate order parameters for projects query "getProjectsDto"
     * @param orderField - field to order
     * @param orderDirection - direction to order
     * @param userEmail - email of the user
     * @param token - user token
     */
    private void validateOrderParameters(String orderField, String orderDirection,String userEmail, String token) {
        String log = "Attempting to validate order parameters. Order field: " + orderField + ", Order direction: " + orderDirection;
        List<String> allowedFields = List.of("createdDate", "name", "status", "vacancies"); // Adicione mais campos conforme necessário

        // Verifica se orderField é válido
        if (orderField != null && !orderField.isEmpty() && !allowedFields.contains(orderField)) {
            LoggerUtil.logError(log,"OrderField is invalid",userEmail,token);
            throw new IllegalArgumentException("Invalid order field. It should be one of " + allowedFields);
        }

        // Verifica se orderDirection é válido
        if (orderDirection != null && !orderDirection.isEmpty() && !orderDirection.equalsIgnoreCase("asc") && !orderDirection.equalsIgnoreCase("desc")) {
            LoggerUtil.logError(log,"OrderDirection is invalid",userEmail,token);
            throw new IllegalArgumentException("Invalid order direction. It should be 'asc' or 'desc'.");
        }
    }

    /**
     * Invite users to project, by email
     * @param token - user token
     * @param projectInviteDto - dto with the project invite
     */
    public void inviteToProject(String token, ProjectInviteDto projectInviteDto) {
        // Validar o token do usuário
        sessionBean.validateUserToken(token);

        // Verificar se os dados do convite são válidos
        if (projectInviteDto == null) {
            throw new IllegalArgumentException("Project invite data is required");
        }

        String invitedUserEmail = projectInviteDto.getInvitedUserEmail();
        if (invitedUserEmail == null || invitedUserEmail.isEmpty()) {
            throw new IllegalArgumentException("Invited user email is required");
        }

        // Buscar o projeto pelo ID
        ProjectEntity project = projectDao.findProjectById(projectInviteDto.getId());
        if (project == null) {
            throw new IllegalArgumentException("Project not found");
        }

        // Buscar o usuário convidado pelo email
        UserEntity invitedUser = userDao.findUserByEmail(invitedUserEmail);
        if (invitedUser == null) {
            throw new IllegalArgumentException("Invited user not found");
        }

        // Verificar se o usuário já está no projeto
        ProjectUserEntity projectUser = projectUserDao.findProjectUserByProjectIdAndUserId(project.getId(), invitedUser.getId());

        // Verificar se o projeto já atingiu o número máximo de participantes
        long activeUsersCount = projectUserDao.countActiveUsersByProjectId(project.getId());
        if (activeUsersCount >= project.getMaxParticipants()) {
            throw new IllegalArgumentException("Project has reached the maximum number of participants");
        }

        String tokenAuthorization = TokenUtil.generateToken();

        // Criar links de aceitação e rejeição
        String acceptLink = "https://localhost:3000/fica-lab/email-list?tokenAuth=" + tokenAuthorization + "&accept=true";
        String rejectLink = "https://localhost:3000/fica-lab/email-list?tokenAuth=" + tokenAuthorization + "&accept=false";
        String projectLink = "https://localhost:3000/fica-lab/project/" + project.getId();

        // Criar o corpo do email
        String emailBody = emailBean.createEmailBody(project.getName(), projectLink, acceptLink, rejectLink);

        // Verificar se o usuário já está no projeto
        if (projectUser != null) {
            if (projectUser.isActive()) {
                throw new IllegalArgumentException("User is already a participant in the project");
            } else {
                projectUser.setActive(true);
                projectUser.setRole(UserType.INVITED);
                projectUser.setTokenAuthorization(tokenAuthorization);
                projectUserDao.merge(projectUser);
            }
        } else {
            projectUser = new ProjectUserEntity();
            projectUser.setProject(project);
            projectUser.setUser(invitedUser);
            projectUser.setRole(UserType.INVITED);
            projectUser.setActive(true);
            projectUser.setTokenAuthorization(tokenAuthorization);
            projectUserDao.persist(projectUser);
        }

        // Enviar o email de convite
        emailBean.sendEmailInviteToUser(token, invitedUserEmail, "Project Invitation", emailBody, project.getId());

        // Registrar o log da ação de convite
        logBean.addNewUser(project.getId(), sessionBean.getUserByToken(token).getId(), invitedUser.getId());

        LoggerUtil.logInfo("Inviting user to project", "User invited successfully", sessionBean.getUserByToken(token).getEmail(), token);
    }



    /**
     * Respond to project invite, accept or reject
     * @param tokenAuthorization - token to respond to the invite
     * @param token - user token
     * @param accept - accept or reject the invite
     */
    public void respondToInvite(String tokenAuthorization, String token, boolean accept) {
        String log = "Responding to project invite";

        try {
            if(tokenAuthorization == null || tokenAuthorization.isEmpty()) {
                throw new IllegalArgumentException("Authorization token is required");
            }

            ProjectUserEntity projectUser = projectUserDao.findProjectUserByToken(tokenAuthorization);
            if(projectUser == null) {
                LoggerUtil.logError(log, "Invalid authorization token", null, tokenAuthorization);
                throw new IllegalArgumentException("Invalid authorization token");
            }

            long participants = projectUserDao.countActiveUsersByProjectId(projectUser.getProject().getId());
            if (participants >= projectUser.getProject().getMaxParticipants()) {
                throw new IllegalArgumentException("Project has reached the maximum number of participants");
            }

            SessionEntity session = sessionDao.findSessionByToken(token);
            if(session == null) {
                LoggerUtil.logError(log, "Invalid user token", null, token);
                throw new IllegalArgumentException("Invalid user token");
            }

            if(!projectUser.getUser().getEmail().equals(session.getUser().getEmail())) {
                LoggerUtil.logError(log, "User is not authorized to respond to this invite", session.getUser().getEmail(), tokenAuthorization);
                throw new IllegalArgumentException("User is not authorized to respond to this invite");
            }

            if(accept) {
                acceptInvite(projectUser, log, tokenAuthorization);
                LoggerUtil.logInfo(log, "Project invite accepted", projectUser.getUser().getEmail(), tokenAuthorization);
            } else {
                LoggerUtil.logInfo(log, "Project invite rejected", projectUser.getUser().getEmail(), tokenAuthorization);
                rejectInvite(projectUser, log, tokenAuthorization);
            }
        } catch (IllegalArgumentException e) {
            LoggerUtil.logError(log, e.getMessage(), null, tokenAuthorization);
        }
    }

    /**
     * Accept project invite
     * @param projectUser - project user entity
     * @param log - log message
     * @param tokenAuthorization - token authorization
     */
    private void acceptInvite(ProjectUserEntity projectUser, String log, String tokenAuthorization) {
        LoggerUtil.logInfo(log, "Project invite accepted", projectUser.getUser().getEmail(), tokenAuthorization);
        projectUser.setRole(UserType.NORMAL);
        projectUser.setTokenAuthorization(null);
        projectUserDao.merge(projectUser);
    }

    /**
     * Reject project invite
     * @param projectUser - project user entity
     * @param log - log message
     * @param tokenAuthorization - token authorization
     */
    private void rejectInvite(ProjectUserEntity projectUser, String log, String tokenAuthorization) {
        LoggerUtil.logInfo(log, "Project invite rejected", projectUser.getUser().getEmail(), tokenAuthorization);
        projectUser.setTokenAuthorization(null);
        projectUser.setActive(false);
        projectUserDao.merge(projectUser);
    }

    /**
     * Get projects that can be invited to
     * @param token - user token
     * @param email - email of the user to invite
     * @return - list of projects that can be invited to
     */
    public List<IdNameDto> getProjectsForInvitation(String token, String email) {
        String log = "Getting projects for invitation";
        SessionEntity session = sessionDao.findSessionByToken(token);
        if(session == null) {
            LoggerUtil.logError(log, "Invalid token", null, token);
            throw new IllegalArgumentException("Invalid token");
        }

        if(email == null || email.isEmpty()) {
            LoggerUtil.logError(log, "Email is required", session.getUser().getEmail(), token);
            throw new IllegalArgumentException("Email is required");
        }

        if(!UserValidator.validateEmail(email)) {
            LoggerUtil.logError(log, "Invalid email", session.getUser().getEmail(), token);
            throw new IllegalArgumentException("Invalid email");
        }

        UserEntity user = userDao.findUserByEmail(email);
        if(user == null) {
            LoggerUtil.logError(log, "User not found", session.getUser().getEmail(), token);
            throw new IllegalArgumentException("User not found");
        }

        String creatorEmail = sessionDao.findSessionByToken(token).getUser().getEmail();

        List<ProjectEntity> projects = projectDao.getProjectsForInvitation(creatorEmail, email);
        return projects.stream()
                .map(project -> new IdNameDto((int) project.getId(), project.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Get the project messages
     * @param token - user token
     * @param id - id of the project
     * @return - list of messages
     */
    public Object getProjectMessages(String token, Long id) {
        String log = "Getting project messages";
        SessionEntity su = sessionDao.findSessionByToken(token);
        if(su == null) {
            LoggerUtil.logError(log, "Invalid token", null, token);
            throw new IllegalArgumentException("Invalid token");
        }
        UserEntity user = userDao.findUserByEmail(su.getUser().getEmail());
        if(user == null) {
            LoggerUtil.logError(log, "User not found", su.getUser().getEmail(), token);
            throw new IllegalArgumentException("Invalid token");
        }

        ProjectEntity project = projectDao.findProjectById(id);
        if(project == null) {
            LoggerUtil.logError(log, "Project not found", user.getEmail(), token);
            throw new IllegalArgumentException("Project not found");
        }

        ProjectUserEntity projectUser = projectUserDao.findProjectUserByProjectIdAndUserId(id, user.getId());
        if(projectUser == null) {
            LoggerUtil.logError(log, "User is not a participant in the project", user.getEmail(), token);
            throw new IllegalArgumentException("User is not a participant in the project");
        }

        if(!projectUser.isActive()) {
            LoggerUtil.logError(log, "User is not a participant in the project", user.getEmail(), token);
            throw new IllegalArgumentException("User is not a participant in the project");
        }

        List<MessageEntity> messages = project.getMessages().stream()
                .filter(MessageEntity::isActive)
                .collect(Collectors.toList());

        return messages.stream()
                .map(messageBean::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Create the final task for the project
     * @param project - project entity
     */
    private void createFinalTaskForProject(ProjectEntity project) {
        try {
            // Verificar se já existe uma tarefa com o título "Presentation of the project"
            List<TaskEntity> existingTasks = taskDao.findTasksByProjectIdAndTitle(project.getId(), "Presentation of the project");
            if (!existingTasks.isEmpty()) {
                // Se já existir, não criar uma nova tarefa
                return;
            }

            // Encontrar a última tarefa do projeto
            List<TaskEntity> tasks = taskDao.findTasksByProjectId(project.getId());
            TaskEntity lastTask = tasks.isEmpty() ? null : tasks.get(tasks.size() - 1);

            // Criar a nova tarefa final
            TaskEntity finalTask = new TaskEntity();
            finalTask.setTitle("Presentation of the project");
            finalTask.setSystemTitle(taskBean.taskSystemNameGenerator("Presentation of the project"));
            finalTask.setDescription("This is the final task of the project.");
            finalTask.setInitialDate(project.getEndDate());
            finalTask.setDuration(Period.ofDays(1));
            finalTask.setFinalDate(project.getEndDate().plusDays(1));
            finalTask.setProject(project);
            finalTask.setStatus(TaskStatus.PRESENTATION);
            finalTask.setActive(true);

            UserEntity responsible = project.getCreator();
            finalTask.setResponsible(responsible);
            finalTask.setCreator(responsible);

            taskDao.persist(finalTask);

            // Adicionar a última tarefa como pré-requisito, se existir
            if (lastTask != null) {
                taskBean.addPrerequisite(finalTask.getId(), lastTask.getId());
            }
        } catch (Exception e) {
            // Logar a exceção e marcar a transação para rollback
            LoggerUtil.logError("Error creating final task for project", e.getMessage(), null, null);
            throw new RuntimeException("Error creating final task for project", e);
        }
    }

    /**
     * Get the project by id, to get info for manage the role, or kick the user from project
     * @param token - user token
     * @param projectId - id of the project
     * @return - project dto
     */
    public List<UserToChangeRoleKickDto> getUsersByProject(String token, Long projectId) {
        // Validar sessão e projeto
        SessionEntity session = sessionDao.findSessionByToken(token);
        ProjectEntity project = projectDao.findProjectById(projectId);
        if (session == null || project == null) {
            throw new IllegalArgumentException("Token ou projeto inválido");
        }
        Long currentUserId = session.getUser().getId();

        List<ProjectUserEntity> projectUsers = projectUserDao.findProjectUserByProjectId(projectId);

        // Filtrar os usuários para excluir o próprio usuário
        return projectUsers.stream()
                .filter(projectUser -> projectUser.getUser().getId() != (currentUserId))
                .map(this::toUserToChangeRoleKickDto)
                .collect(Collectors.toList());
    }

    /**
     * Convert project user entity to user to change role kick dto
     * @param projectUser - project user entity
     * @return - user to change role kick dto
     */
    private UserToChangeRoleKickDto toUserToChangeRoleKickDto(ProjectUserEntity projectUser) {
        UserToChangeRoleKickDto dto = new UserToChangeRoleKickDto();
        dto.setId(projectUser.getUser().getId());
        dto.setEmail(projectUser.getUser().getEmail());
        dto.setRole(projectUser.getRole().getValue());
        dto.setFirstname(projectUser.getUser().getFirstname());
        dto.setLastname(projectUser.getUser().getLastname());
        dto.setLab(projectUser.getUser().getLab().getLocation());
        dto.setImg(projectUser.getUser().getProfileImagePath());
        return dto;
    }

    /**
     * Get the project by id, to get info for manage the role, or kick the user from project
     * @param projectUser - project user entity
     * @return - user to change role kick dto
     */
    private UserInviteDto toUserInviteDto (ProjectUserEntity projectUser){
        UserInviteDto dto = new UserInviteDto();
        dto.setId(projectUser.getUser().getId());
        dto.setEmail(projectUser.getUser().getEmail());
        dto.setFirstname(projectUser.getUser().getFirstname());
        dto.setLastname(projectUser.getUser().getLastname());
        dto.setLab(projectUser.getUser().getLab().getLocation());
        dto.setImg(projectUser.getUser().getProfileImagePath());
        dto.setRole(projectUser.getRole().getValue());
        return dto;
    }

    /**
     * Change the role of a user in a project, only the project manager can do this
     * @param token - user token
     * @param userId - user id
     * @param userChangeRoleDto - user change role dto
     * @return - user to change role kick dto
     */
    public UserToChangeRoleKickDto changeUserRole(String token, Long userId, UserChangeRoleDto userChangeRoleDto) {
        // Validar sessão e projeto
        String log = "Changing user role";
        SessionEntity session = sessionDao.findSessionByToken(token);
        ProjectEntity project = projectDao.findProjectById(userChangeRoleDto.getProjectId());
        if (session == null || project == null) {
            LoggerUtil.logError(log, "Token or project invalid", null, token);
            throw new IllegalArgumentException("Token or project invalid");
        }
        // Verificar se o user é gerente do projeto
        ProjectUserEntity projectUser = projectUserDao.findProjectUserByProjectIdAndUserId(userChangeRoleDto.getProjectId(), session.getUser().getId());
        if (projectUser == null || projectUser.getRole() != UserType.MANAGER) {
            LoggerUtil.logError(log, "User isnt a manager of the project", session.getUser().getEmail(), token);
            throw new IllegalArgumentException("User isnt a manager of the project");
        }
        // Verificar se o usuário a ser alterado é participante do projeto
        ProjectUserEntity userToChange = projectUserDao.findProjectUserByProjectIdAndUserId(userChangeRoleDto.getProjectId(), userId);
        if (userToChange == null) {
            LoggerUtil.logError(log, "User isnt a participant of the project", session.getUser().getEmail(), token);
            throw new IllegalArgumentException("User isnt a participant of the project");
        }
        UserType oldType = userToChange.getRole();
        // Alterar o papel do usuário
        userToChange.setRole(UserType.fromValue(userChangeRoleDto.getRole()));
        projectUserDao.merge(userToChange);
        notificationBean.sendNotification(session.getUser().getEmail(), userToChange.getUser().getEmail(), "Your role in the project " + project.getName() + " has been changed to " + userChangeRoleDto.getRole(), NotificationType.PROJECT_ROLE_CHANGED, userChangeRoleDto.getProjectId());
        logBean.addNewUserChange(project.getId(), session.getUser().getId(), userToChange.getUser().getId() ,oldType, UserType.fromValue(userChangeRoleDto.getRole()));
        return toUserToChangeRoleKickDto(userToChange);
    }

    /**
     * Kick a user from a project, only the project manager can do this
     * @param token - user token
     * @param userId - user id
     * @param projectId - project id
     */
    public void kickUser(String token, Long userId, Long projectId) {
        // Validar sessão e projeto
        String log = "Kicking user from project";
        SessionEntity session = sessionDao.findSessionByToken(token);
        ProjectEntity project = projectDao.findProjectById(projectId);
        if (session == null || project == null) {
            LoggerUtil.logError(log, "Token or project invalid", null, token);
            throw new IllegalArgumentException("Token or project invalid");
        }
        // Verificar se o user é gerente do projeto
        ProjectUserEntity projectUser = projectUserDao.findProjectUserByProjectIdAndUserId(projectId, session.getUser().getId());
        if (projectUser == null || projectUser.getRole() != UserType.MANAGER) {
            LoggerUtil.logError(log, "User isnt a manager of the project", session.getUser().getEmail(), token);
            throw new IllegalArgumentException("User isnt a manager of the project");
        }
        // Verificar se o user a ser alterado é participante do projeto
        ProjectUserEntity userToChange = projectUserDao.findProjectUserByProjectIdAndUserId(projectId, userId);
        if (userToChange == null) {
            LoggerUtil.logError(log, "User isnt a participant of the project", session.getUser().getEmail(), token);
            throw new IllegalArgumentException("User isnt a participant of the project");
        }
        // Remover o user do projeto
        userToChange.setRole(UserType.KICKED);
        userToChange.setActive(false);
        project.getProjectUsers().remove(userToChange);
        projectDao.merge(project    );
        projectUserDao.merge(userToChange);
//        notificationBean.sendNotification(session.getUser().getEmail(), userToChange.getUser().getEmail(), "You have been kicked from the project " + project.getName(), NotificationType.PROJECT_KICKED, projectId);
        emailBean.sendMailToUser(token, userToChange.getUser().getEmail(), "You have been kicked from the project " + project.getName(), "You have been kicked from the project " + project.getName() + " by the project manager " + session.getUser().getEmail() + ".");
        logBean.addNewUserKicked(projectId, session.getUser().getId(), userToChange.getUser().getId());
        LoggerUtil.logInfo(log, "User kicked from project", session.getUser().getEmail(), token);
    }

    /**
     * Get invites to a project verify if the user is a manager of the project
     * @param token - user token
     * @param projectId - project id
     * @return - list of proposed users
     */
    public List<UserInviteDto> getInvites(String token, Long projectId) {
        // Validar sessão e projeto
        SessionEntity session = sessionDao.findSessionByToken(token);
        ProjectEntity project = projectDao.findProjectById(projectId);
        if (session == null || project == null) {
            throw new IllegalArgumentException("Token or project invalid");
        }
        // Verificar se o user é gerente do projeto
        ProjectUserEntity projectUser = projectUserDao.findProjectUserByProjectIdAndUserId(projectId, session.getUser().getId());
        if (projectUser == null || projectUser.getRole() != UserType.MANAGER) {
            throw new IllegalArgumentException("User isnt a manager of the project");
        }
        // Verificar se o user a ser alterado é participante do projeto
        List<ProjectUserEntity> invites = projectUserDao.findProjectUserByProjectIdAndRole(projectId, UserType.PROPOSED);
        System.out.println(invites.size());
        return invites.stream()
                .map(this::toUserInviteDto)
                .collect(Collectors.toList());
    }

    /**
     * Propose a project to a user to join
     * @param token - user token
     * @param projectId - project id
     */
    public void proposeProject(String token, Long projectId) {
        // Validar sessão e projeto
        String log= "Proposing project";
        SessionEntity session = sessionDao.findSessionByToken(token);
        if (session == null) {
            LoggerUtil.logError(log, "Invalid token", null, token);
            throw new IllegalArgumentException("Invalid token");
        }

        ProjectEntity project = projectDao.findProjectById(projectId);
        if (project == null) {
            LoggerUtil.logError(log, "Invalid project ID", session.getUser().getEmail(), token);
            throw new IllegalArgumentException("Invalid project ID");
        }

        List<ProjectUserEntity> projectUsers = projectUserDao.findProjectUserByProjectId(projectId);
        // Verificar se o usuário já está associado ao projeto
        ProjectUserEntity projectUser = projectUserDao.findProjectUserByProjectIdAndUserId(projectId, session.getUser().getId());
        if (projectUser == null) {
            // Criar novo ProjectUserEntity
            ProjectUserEntity newProjectUser = new ProjectUserEntity();
            newProjectUser.setProject(project);
            newProjectUser.setUser(session.getUser());
            newProjectUser.setRole(UserType.PROPOSED);
            newProjectUser.setActive(true);

            for(ProjectUserEntity pu : projectUsers){
                if(pu.getRole() == UserType.MANAGER){
                    System.out.println("manager");
                    notificationBean.sendNotification(session.getUser().getEmail(), pu.getUser().getEmail(), "User proposed to join the project " + project.getName(), NotificationType.INVITE_PROPOSED, projectId);
                }
            }
            projectUserDao.persist(newProjectUser);
//            notificationBean.sendNotification(session.getUser().getEmail(), project.getCreator().getEmail(), "User proposed to join the project " + project.getName(), NotificationType.INVITE_PROPOSED, projectId);
            LoggerUtil.logInfo(log, "User proposed the project", session.getUser().getEmail(), token);
        } else if (projectUser.isActive() && (projectUser.getRole() == UserType.NORMAL || projectUser.getRole() == UserType.MANAGER)) {
            // Lançar exceção se o usuário já for um participante ativo
            LoggerUtil.logInfo(log, "User is already a participant in the project", session.getUser().getEmail(), token);
            throw new IllegalArgumentException("User is already a participant in the project");
        } else {
            // Atualizar o papel do usuário existente no projeto para PROPOSED
            projectUser.setActive(true);
            projectUser.setRole(UserType.PROPOSED);
            projectUserDao.merge(projectUser);

            for(ProjectUserEntity pu : projectUsers){
                if(pu.getRole() == UserType.MANAGER){
                    System.out.println("manager");
                    notificationBean.sendNotification(session.getUser().getEmail(), pu.getUser().getEmail(), "User proposed to join the project " + project.getName(), NotificationType.INVITE_PROPOSED, projectId);
                }
            }
            LoggerUtil.logInfo(log, "User proposed the project", session.getUser().getEmail(), token);
        }
    }

    /**
     * Response to an invite to join a project, accepting or rejecting the invitation
     * @param token - user token
     * @param projectId - project id
     * @param dto - response data
     */
    public void inviteResponse(String token, Long projectId, ResponseYesNoInviteDto dto) {
        // Validar sessão e projeto
        String log = "Processing user invite response";
        SessionEntity session = sessionDao.findSessionByToken(token);
        ProjectEntity project = projectDao.findProjectById(projectId);
        if (session == null || project == null) {
            LoggerUtil.logError(log, "Invalid token or project", null, token);
            throw new IllegalArgumentException("Invalid token or project");
        }
        if (dto == null) {
            LoggerUtil.logError(log, "User invite data is required", session.getUser().getEmail(), token);
            throw new IllegalArgumentException("User invite data is required");
        }

        // Validar se o usuário é gerente do projeto
        ProjectUserEntity projectManager = projectUserDao.findProjectUserByProjectIdAndUserId(projectId, session.getUser().getId());
        if (projectManager == null || projectManager.getRole() != UserType.MANAGER) {
            LoggerUtil.logError(log, "User is not a manager of the project", session.getUser().getEmail(), token);
            throw new IllegalArgumentException("User is not a manager of the project");
        }

        // Validar se o usuário convidado existe e está na lista de convidados
        ProjectUserEntity invitedUser = projectUserDao.findProjectUserByProjectIdAndUserId(projectId, dto.getUserId());
        if (invitedUser == null || invitedUser.getRole() != UserType.PROPOSED) {
            LoggerUtil.logError(log, "User is not invited to the project", session.getUser().getEmail(), token);
            throw new IllegalArgumentException("User is not invited to the project");
        }

        // Responder ao convite
        boolean accept = dto.isAccept();
        if (accept) {

            long participants = projectUserDao.countActiveUsersByProjectId(projectId);
            if(participants >= project.getMaxParticipants()){
                throw new IllegalArgumentException("Project has reached the maximum number of participants");
            }

            invitedUser.setRole(UserType.NORMAL);
            invitedUser.setActive(true);
            logBean.addNewUser(projectId, session.getUser().getId(), invitedUser.getUser().getId());
            notificationBean.sendNotification(session.getUser().getEmail(), invitedUser.getUser().getEmail(), "You have been accepted to join the project " + project.getName(), NotificationType.INVITE_ACCEPTED, projectId);
            LoggerUtil.logInfo(log, "User accepted the invite from user id:"+dto.getUserId(), session.getUser().getEmail(), token);
        } else {
            invitedUser.setActive(false);
            LoggerUtil.logInfo(log, "User rejected the invite from user id:"+dto.getUserId(), session.getUser().getEmail(), token);
        }

        projectUserDao.merge(invitedUser);
    }

    public void leaveProject(String token, Long projectId) {
        SessionEntity se = sessionDao.findSessionByToken(token);
        ProjectEntity pe = projectDao.findProjectById(projectId);
        if(se == null || pe == null) {
            throw new IllegalArgumentException("Invalid token or project");
        }

        UserEntity user = se.getUser();
        ProjectUserEntity pue = projectUserDao.findProjectUserByProjectIdAndUserId(projectId, user.getId());
        if(pue == null) {
            throw new IllegalArgumentException("User is not a participant in the project");
        }

        if(se.getUser().equals(pe.getCreator())) {
            throw new IllegalArgumentException("Project creator cannot leave the project");
        }

        List<TaskExecutorEntity> taskExecutor = taskExecutorDao.findTaskExecutorByProjectIdAndUserId(projectId, user.getId());
        List<TaskEntity> tasks = taskDao.findTasksByProjectIdAndUserId(projectId, user.getId());
        if(taskExecutor != null && !taskExecutor.isEmpty() || tasks != null && !tasks.isEmpty()) {
            throw new IllegalArgumentException("User is responsible for tasks in the project, cannot leave the project with tasks assigned");
        }

        pue.setActive(false);
        pue.setRole(UserType.KICKED);
        projectUserDao.merge(pue);
    }

    public PaginatedResponse<Object> getReadyProjects(String token, Integer pageNumber, Integer pageSize){
        String log = "Getting ready projects";
        SessionEntity se = sessionDao.findSessionByToken(token);
        if(se == null) {
            LoggerUtil.logError(log, "Invalid token", null, token);
            throw new IllegalArgumentException("Invalid token");
        }
        UserType role = se.getUser().getRole();
        if(role != UserType.ADMIN) {
            LoggerUtil.logError(log, "User is not authorized to access this resource", se.getUser().getEmail(), token);
            throw new IllegalArgumentException("User is not authorized to access this resource");
        }

        PaginatedResponse<ProjectEntity> readyProjects = projectDao.findProjects(null, List.of(ProjectStatus.READY), null, null, null, null, null, null, null, null, pageNumber, pageSize, "createdDate", "desc");
        List<ProjectEntity> projects = readyProjects.getResults();

        PaginatedResponse<Object> response = new PaginatedResponse<>();
        response.setTotalPages(readyProjects.getTotalPages());
        response.setResults(projects.stream()
                .map(this::toProjectReadyDto)
                .collect(Collectors.toList()));
        return response;
    }

    public void approveProject(String token, Long projectId, ResponseYesNoInviteDto responseYesNoInviteDto) {
        String log = "Approving/Reject project";

        SessionEntity se = sessionDao.findSessionByToken(token);
        ProjectEntity project = projectDao.findProjectById(projectId);

        if(se == null || project == null) {
            LoggerUtil.logError(log, "Invalid token or project", null, token);
            throw new IllegalArgumentException("Invalid token or project");
        }

        if(se.getUser().getRole() != UserType.ADMIN) {
            LoggerUtil.logError(log, "User is not authorized to access this resource", se.getUser().getEmail(), token);
            throw new IllegalArgumentException("User is not authorized to access this resource");
        }

        ProjectStatus status = responseYesNoInviteDto.isAccept() ? ProjectStatus.APPROVED : ProjectStatus.PLANNING;

        project.setStatus(status);
        projectDao.merge(project);
    }

    public UserSettingsDto getStatisticsByLab(String token, Integer lab) {
        SessionEntity se = sessionDao.findSessionByToken(token);
        if(se == null) {
            throw new IllegalArgumentException("Invalid token");
        }


        UserSettingsDto dto = new UserSettingsDto();
        dto.setTimeout(appConfigDao.findLastConfig().getTimeOut());
        List<LabDto> labs = labDao.findAll()
                .stream().map(laboratory -> new LabDto(laboratory.getId(), laboratory.getLocation()))
                .collect(Collectors.toList());
        dto.setLabs(labs);

        if(lab != null) {
            System.out.println("lab: " + lab);
            LabEntity le = labDao.findLabById(lab);
            if(le == null) {
                System.out.println("lab not found");
                throw new IllegalArgumentException("Lab not found");
            }
            StatisticsDto statisticsDto = new StatisticsDto();
            try {
                statisticsDto = projectDao.getStatisticsByLab(lab);
                if (statisticsDto == null) {
                    statisticsDto = new StatisticsDto();
                }
            }catch (Exception e){
                statisticsDto = new StatisticsDto();
            }finally {
                dto.setStatistics(statisticsDto);
            }
        }

        return dto;
    }

    public void updateTimeout(String token, Integer timeout) {
        String log = "Updating timeout";
        SessionEntity se = sessionDao.findSessionByToken(token);
        if(se == null) {
            LoggerUtil.logError(log, "Invalid token", null, token);
            throw new IllegalArgumentException("Invalid token");
        }

        if(se.getUser().getRole() != UserType.ADMIN) {
            LoggerUtil.logError(log, "User is not authorized to access this resource", se.getUser().getEmail(), token);
            throw new IllegalArgumentException("User is not authorized to access this resource");
        }

        AppConfigEntity appConfig = new AppConfigEntity();
        appConfig.setTimeOut(timeout);
        appConfig.setUser(se.getUser());
        appConfig.setMaxUsers(appConfigDao.findLastConfig().getMaxUsers());
        appConfig.setTimeOutAdmin(appConfigDao.findLastConfig().getTimeOutAdmin());
        appConfigDao.merge(appConfig);
        LoggerUtil.logInfo(log, "Timeout updated", se.getUser().getEmail(), token);
    }

    public void updateRole(String token, LabelValueDto dto) {
        String log = "Updating role";
        SessionEntity se = sessionDao.findSessionByToken(token);
        if(se == null) {
            LoggerUtil.logError(log, "Invalid token", null, token);
            throw new IllegalArgumentException("Invalid token");
        }

        if(se.getUser().getRole() != UserType.ADMIN) {
            LoggerUtil.logError(log, "User is not authorized to access this resource", se.getUser().getEmail(), token);
            throw new IllegalArgumentException("User is not authorized to access this resource");
        }

        UserEntity user = userDao.findUserByEmail(dto.getLabel());
        if(user == null) {
            LoggerUtil.logError(log, "User not found", se.getUser().getEmail(), token);
            throw new IllegalArgumentException("User not found");
        }

        boolean isCreatorOrResponsible = taskDao.isCreatorOrResponsible(user.getId());
        System.out.println(Color.YELLOW + "isCreatorOrResponsible: " + isCreatorOrResponsible + Color.RESET);
        if(isCreatorOrResponsible) {
            LoggerUtil.logError(log, "User cannot change role due to active task associations", se.getUser().getEmail(), token);
            throw new IllegalArgumentException("User cannot change role due to active task associations");
        }

        // Verificar associações ativas com projetos
        boolean isActiveInProjects = projectUserDao.isActiveInAnyProject(user.getId());
        System.out.println(Color.PURPLE + "isActiveInProjects: " + isActiveInProjects + Color.RESET);
        if (isActiveInProjects) {
            throw new IllegalArgumentException("User cannot change role due to active project associations");
        }

        // Verificar associações ativas com tarefas
        boolean isActiveInTasks = taskExecutorDao.isActiveInAnyTask(user.getId());
        System.out.println(Color.CYAN + "isActiveInTasks: " + isActiveInTasks + Color.RESET);
        if (isActiveInTasks) {
            throw new IllegalArgumentException("User cannot change role due to active task associations");
        }

        long role = dto.getValue();
        int intValue = (int) role;

        user.setRole(UserType.fromValue(intValue));
        userDao.merge(user);
    }
}
