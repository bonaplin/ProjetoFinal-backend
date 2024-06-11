package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.dto.IdNameDto;
import aor.project.innovationlab.dto.interests.InterestDto;
import aor.project.innovationlab.dto.lab.LabDto;
import aor.project.innovationlab.dto.project.ProjectCardDto;
import aor.project.innovationlab.dto.project.ProjectDto;
import aor.project.innovationlab.dto.project.filter.FilterOptionsDto;
import aor.project.innovationlab.dto.skill.SkillDto;
import aor.project.innovationlab.dto.user.UserImgCardDto;
import aor.project.innovationlab.dto.project.ProjectSideBarDto;
import aor.project.innovationlab.dto.project.ProjectSideBarDto;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.*;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
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
            projectDao.persist(project);
            // create the entity project - creator
            addUserToProject(name, creatorEmail, ProjectUserType.MANAGER);

            addInterestToProject(name, "Interest1");
            addInterestToProject(name, "Interest2");

            addResourceToProject(name,"123456788");
            addResourceToProjectByNames(name, "Product 6");
            addResourceToProjectByNames(name, "Product 2");
            addResourceToProjectByNames(name, "Product 3");

            addUserToProject(name, "joao@joao", ProjectUserType.NORMAL);
            addSkillToProject(name, "Assembly");
            addSkillToProject(name, "macOS");
            addSkillToProject(name, "IntelIJ");
            addInterestToProject(name, "Interest3");
            addInterestToProject(name, "Interest4");
            addInterestToProject(name, "Interest5");
            addInterestToProject(name, "Interest6");
            messageBean.sendMessage("admin@admin", name, "Hello, this is a message by Admin");
            messageBean.sendMessage("ricardo@ricardo", name, "Hello, this is a message by Ricardo");

            notificationBean.sendNotification("admin@admin", "ricardo@ricardo", "Hello, this is a notification by Admin", NotificationType.MESSAGE, name);
            notificationBean.sendNotification("joao@joao", "ricardo@ricardo", "Olá ric",NotificationType.INVITE,null);


            //TESTE - add log ao add user
            logBean.addNewUser(project.getId(), userDao.findUserByEmail("admin@admin").getId(), userDao.findUserByEmail("ricardo@ricardo").getId(), LogType.USER_JOIN);
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

    public void addUserToProject(String projectName, String userEmail, ProjectUserType role) {
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

    public List<?> getProjectsByDto(String dtoType, String name,
                                    List<ProjectStatus> status,
                               List<String> lab, String creatorEmail,
                               List<String> skill, List<String> interest,
                               String participantEmail,
                               ProjectUserType role, String auth){

        String userEmail = null;

        if(auth != null){
//            String token = sessionBean.getTokenFromAuthorizationHeader(auth);

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

        List<ProjectEntity> projects = projectDao.findProjects(name, status, lab, creatorEmail, skill, interest, participantEmail, role, userEmail);



        if(dtoType == null || dtoType.isEmpty()) {
            dtoType = "ProjectCardDto";
        }
        switch (dtoType) {
            case "ProjectCardDto":
                return projects.stream()
                        .map(this::toCardDto)
                        .collect(Collectors.toList());
            case "ProjectDto":
                return projects.stream()
                        .map(this::toDto)
                        .collect(Collectors.toList());
            case "ProjectSideBarDto":
                return projects.stream()
                        .map(this::toSideBarDto)
                        .collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }
    }


}
