package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.dto.log.LogDto;
import aor.project.innovationlab.dto.project.notes.NoteIdNoteDto;
import aor.project.innovationlab.dto.response.IdNameDto;
import aor.project.innovationlab.dto.response.PaginatedResponse;
import aor.project.innovationlab.dto.interests.InterestDto;
import aor.project.innovationlab.dto.product.ProductToCreateProjectDto;
import aor.project.innovationlab.dto.project.*;
import aor.project.innovationlab.dto.project.filter.FilterOptionsDto;
import aor.project.innovationlab.dto.response.ResponseYesNoInviteDto;
import aor.project.innovationlab.dto.skill.SkillDto;
import aor.project.innovationlab.dto.task.TaskDto;
import aor.project.innovationlab.dto.user.UserAddToProjectDto;
import aor.project.innovationlab.dto.user.UserImgCardDto;
import aor.project.innovationlab.dto.project.ProjectSideBarDto;
import aor.project.innovationlab.dto.user.project.UserChangeRoleDto;
import aor.project.innovationlab.dto.user.project.UserInviteDto;
import aor.project.innovationlab.dto.user.project.UserToChangeRoleKickDto;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.*;
import aor.project.innovationlab.utils.Color;
import aor.project.innovationlab.utils.InputSanitizerUtil;
import aor.project.innovationlab.utils.TokenUtil;
import aor.project.innovationlab.utils.logs.LoggerUtil;
import aor.project.innovationlab.validator.UserValidator;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

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

        List<UserImgCardDto> users = entity.getProjectUsers().stream()
                .map(projectUserEntity -> {
                    UserImgCardDto userImgCardDto = new UserImgCardDto();
                    userImgCardDto.setId(projectUserEntity.getUser().getId());
                    userImgCardDto.setImagePath(projectUserEntity.getUser().getProfileImagePath());
                    return userImgCardDto;
                })
                .collect(Collectors.toList());

        dto.setProjectUsers(users);

        return dto;
    }

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
                    invitedUserEntity.setRole(UserType.INVITED);
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

        if(userEmail != null && id != null){
            ProjectUserEntity pue = projectUserDao.findProjectUserByProjectIdAndUserId(id, userDao.findUserByEmail(userEmail).getId());
            if(pue != null && pue.isActive()){
                webSocketBean.openProjectWindow(auth, id);
            }
            if(pue != null){
                response.setUserType(pue.getRole().getValue());
            }else{
                response.setUserType(UserType.GUEST.getValue());
            }
        }
        else {
            response.setUserType(UserType.GUEST.getValue());
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


    public void inviteToProject(String token, ProjectInviteDto projectInviteDto) {
        sessionBean.validateUserToken(token);

        if(projectInviteDto == null) {
            throw new IllegalArgumentException("Project invite data is required");
        }

        if(projectInviteDto.getInvitedUserEmail() == null || projectInviteDto.getInvitedUserEmail().isEmpty()) {
            throw new IllegalArgumentException("Invited user email is required");
        }

        ProjectEntity project = projectDao.findProjectById(projectInviteDto.getId());
        if(project == null) {
            throw new IllegalArgumentException("Project not found");
        }

        UserEntity invitedUser = userDao.findUserByEmail(projectInviteDto.getInvitedUserEmail());
        if(invitedUser == null) {
            throw new IllegalArgumentException("Invited user not found");
        }

        ProjectUserEntity projectUser = projectUserDao.findProjectUserByProjectIdAndUserId(project.getId(), invitedUser.getId());
        String tokenAuthorization = TokenUtil.generateToken();

        String acceptLink = "https://localhost:3000/fica-lab/email-list?tokenAuth=" + tokenAuthorization +"&accept=true";
        String rejectLink = "https://localhost:3000/fica-lab/email-list?tokenAuth=" + tokenAuthorization+ "&accept=false";
        String projectLink = "https://localhost:3000/fica-lab/project/" + project.getId();

        String emailBody = emailBean.createEmailBody(project.getName(),projectLink, acceptLink, rejectLink);

        if(projectUser != null) {
            if(projectUser.isActive()) {
                throw new IllegalArgumentException("User is already a participant in the project");
            }
            else {
                projectUser.setActive(true);
                projectUser.setRole(UserType.INVITED);
                projectUser.setTokenAuthorization(tokenAuthorization);
                projectUserDao.merge(projectUser);
            }
        }else{
            projectUser = new ProjectUserEntity();
            projectUser.setProject(project);
            projectUser.setUser(invitedUser);
            projectUser.setRole(UserType.INVITED);
            projectUser.setActive(true);
            projectUser.setTokenAuthorization(tokenAuthorization);
            projectUserDao.persist(projectUser);
            emailBean.sendEmailInviteToUser(token, projectInviteDto.getInvitedUserEmail(), "Project Invited", emailBody, project.getId());
//            notificationBean.sendNotification(sessionBean.getUserByToken(token).getEmail(), projectInviteDto.getInvitedUserEmail(), "You have been invited to join the project " + project.getName(),NotificationType.INVITE, project.getId());
        }


    }

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
            } else {
                rejectInvite(projectUser, log, tokenAuthorization);
            }
        } catch (IllegalArgumentException e) {
            LoggerUtil.logError(log, e.getMessage(), null, tokenAuthorization);
        }
    }

    private void acceptInvite(ProjectUserEntity projectUser, String log, String tokenAuthorization) {
        LoggerUtil.logInfo(log, "Project invite accepted", projectUser.getUser().getEmail(), tokenAuthorization);
        projectUser.setRole(UserType.NORMAL);
        projectUser.setTokenAuthorization(null);
        projectUserDao.merge(projectUser);
    }

    private void rejectInvite(ProjectUserEntity projectUser, String log, String tokenAuthorization) {
        LoggerUtil.logInfo(log, "Project invite rejected", projectUser.getUser().getEmail(), tokenAuthorization);
        projectUser.setTokenAuthorization(null);
        projectUser.setActive(false);
        projectUserDao.merge(projectUser);
    }

    public List<IdNameDto> getProjectsForInvitation(String token, String email) {

        SessionEntity session = sessionDao.findSessionByToken(token);
        if(session == null) {
            throw new IllegalArgumentException("Invalid token");
        }

        if(email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        if(!UserValidator.validateEmail(email)) {
            throw new IllegalArgumentException("Invalid email");
        }

        UserEntity user = userDao.findUserByEmail(email);
        if(user == null) {
            throw new IllegalArgumentException("User not found");
        }

        String creatorEmail = sessionDao.findSessionByToken(token).getUser().getEmail();

        List<ProjectEntity> projects = projectDao.getProjectsForInvitation(creatorEmail, email);
        return projects.stream()
                .map(project -> new IdNameDto((int) project.getId(), project.getName()))
                .collect(Collectors.toList());
    }

    public Object getProjectMessages(String token, Long id) {

        SessionEntity su = sessionDao.findSessionByToken(token);
        if(su == null) {
            throw new IllegalArgumentException("Invalid token");
        }
        UserEntity user = userDao.findUserByEmail(su.getUser().getEmail());
        if(user == null) {
            throw new IllegalArgumentException("Invalid token");
        }

        ProjectEntity project = projectDao.findProjectById(id);
        if(project == null) {
            throw new IllegalArgumentException("Project not found");
        }

        ProjectUserEntity projectUser = projectUserDao.findProjectUserByProjectIdAndUserId(id, user.getId());
        if(projectUser == null) {
            throw new IllegalArgumentException("User is not a participant in the project");
        }

        if(!projectUser.isActive()) {
            throw new IllegalArgumentException("User is not a participant in the project");
        }

        List<MessageEntity> messages = project.getMessages().stream()
                .filter(MessageEntity::isActive)
                .collect(Collectors.toList());

        return messages.stream()
                .map(messageBean::toDto)
                .collect(Collectors.toList());
    }

    public void createFinalTaskForProject(ProjectEntity project) {
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

    public UserToChangeRoleKickDto changeUserRole(String token, Long userId, UserChangeRoleDto userChangeRoleDto) {
        // Validar sessão e projeto
        SessionEntity session = sessionDao.findSessionByToken(token);
        ProjectEntity project = projectDao.findProjectById(userChangeRoleDto.getProjectId());
        if (session == null || project == null) {
            throw new IllegalArgumentException("Token or project invalid");
        }
        // Verificar se o user é gerente do projeto
        ProjectUserEntity projectUser = projectUserDao.findProjectUserByProjectIdAndUserId(userChangeRoleDto.getProjectId(), session.getUser().getId());
        if (projectUser == null || projectUser.getRole() != UserType.MANAGER) {
            throw new IllegalArgumentException("User isnt a manager of the project");
        }
        // Verificar se o usuário a ser alterado é participante do projeto
        ProjectUserEntity userToChange = projectUserDao.findProjectUserByProjectIdAndUserId(userChangeRoleDto.getProjectId(), userId);
        if (userToChange == null) {
            throw new IllegalArgumentException("User isnt a participant of the project");
        }
        // Alterar o papel do usuário
        userToChange.setRole(UserType.fromValue(userChangeRoleDto.getRole()));
        projectUserDao.merge(userToChange);

        return toUserToChangeRoleKickDto(userToChange);
    }

    public void kickUser(String token, Long userId, Long projectId) {
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
        ProjectUserEntity userToChange = projectUserDao.findProjectUserByProjectIdAndUserId(projectId, userId);
        if (userToChange == null) {
            throw new IllegalArgumentException("User isnt a participant of the project");
        }
        // Remover o user do projeto
        userToChange.setActive(false);
        projectUserDao.merge(userToChange);
    }

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

    public void proposeProject(String token, Long projectId) {
        // Validar sessão e projeto
        SessionEntity session = sessionDao.findSessionByToken(token);
        if (session == null) {
            throw new IllegalArgumentException("Invalid token");
        }

        ProjectEntity project = projectDao.findProjectById(projectId);
        if (project == null) {
            throw new IllegalArgumentException("Invalid project ID");
        }

        // Verificar se o usuário já está associado ao projeto
        ProjectUserEntity projectUser = projectUserDao.findProjectUserByProjectIdAndUserId(projectId, session.getUser().getId());
        if (projectUser == null) {
            // Criar novo ProjectUserEntity
            ProjectUserEntity newProjectUser = new ProjectUserEntity();
            newProjectUser.setProject(project);
            newProjectUser.setUser(session.getUser());
            newProjectUser.setRole(UserType.PROPOSED);
            newProjectUser.setActive(true);
            projectUserDao.persist(newProjectUser);
        } else if (projectUser.isActive() && (projectUser.getRole() == UserType.NORMAL || projectUser.getRole() == UserType.MANAGER)) {
            // Lançar exceção se o usuário já for um participante ativo
            throw new IllegalArgumentException("User is already a participant in the project");
        } else {
            // Atualizar o papel do usuário existente no projeto para PROPOSED
            projectUser.setActive(true);
            projectUser.setRole(UserType.PROPOSED);
            projectUserDao.merge(projectUser);
        }
    }

    public void inviteResponse(String token, Long projectId, ResponseYesNoInviteDto dto) {
        // Validar sessão e projeto
        SessionEntity session = sessionDao.findSessionByToken(token);
        ProjectEntity project = projectDao.findProjectById(projectId);
        if (session == null || project == null) {
            throw new IllegalArgumentException("Invalid token or project");
        }
        if (dto == null) {
            throw new IllegalArgumentException("User invite data is required");
        }

        // Validar se o usuário é gerente do projeto
        ProjectUserEntity projectManager = projectUserDao.findProjectUserByProjectIdAndUserId(projectId, session.getUser().getId());
        if (projectManager == null || projectManager.getRole() != UserType.MANAGER) {
            throw new IllegalArgumentException("User is not a manager of the project");
        }

        // Validar se o usuário convidado existe e está na lista de convidados
        ProjectUserEntity invitedUser = projectUserDao.findProjectUserByProjectIdAndUserId(projectId, dto.getUserId());
        if (invitedUser == null || invitedUser.getRole() != UserType.PROPOSED) {
            throw new IllegalArgumentException("User is not invited to the project");
        }

        // Responder ao convite
        boolean accept = dto.isAccept();
        if (accept) {
            invitedUser.setRole(UserType.NORMAL);
            invitedUser.setActive(true);
        } else {
            invitedUser.setActive(false);
        }

        // Persistir as mudanças no usuário convidado
        projectUserDao.merge(invitedUser);

        // Logs para depuração (opcional)
        System.out.println("User invite response processed: " + dto.getUserId() + " accept: " + accept);
    }

}
