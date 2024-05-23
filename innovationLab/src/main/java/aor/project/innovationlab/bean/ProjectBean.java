package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.dto.project.ProjectDto;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.LogType;
import aor.project.innovationlab.enums.NotificationType;
import aor.project.innovationlab.enums.ProductStatus;
import aor.project.innovationlab.enums.ProjectUserType;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;

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
    private ProductDao productDao;

    @EJB
    private ProjectUserDao projectUserDao;

    @EJB
    private ProjectProductDao projectProductDao;

    @Inject
    private MessageBean messageBean;

    @Inject
    private NotificationBean notificationBean;

    @Inject
    private LogBean logBean;

    public ProjectBean() {
    }

    public void toEntity(ProjectDto dto) {
    }

    public void toDto(ProjectEntity entity) {
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
            return;
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
        createProjectIfNotExists("Project 1", "Description 1", "admin@admin", "Coimbra");
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
            addResourceToProjectByNames(name, "Product 6");
            addResourceToProjectByNames(name, "Product 2");
            addResourceToProjectByNames(name, "Product 3");
            removeResourceFromProject(name, "123456788");
            addUserToProject(name, "admin@admin", ProjectUserType.MANAGER);
            addUserToProject(name, "ricardo@ricardo", ProjectUserType.NORMAL);
            addUserToProject(name, "joao@joao", ProjectUserType.INVITED);
            addInterestToProject(name, "Interest 1");
            addInterestToProject(name, "Interest 2");
            addResourceToProject(name,"123456788");

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
}