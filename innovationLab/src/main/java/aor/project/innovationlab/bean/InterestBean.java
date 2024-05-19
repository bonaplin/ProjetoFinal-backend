package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.entity.*;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InterestBean {

    @EJB
    private InterestDao interestDao;

    @EJB
    private UserDao userDao;

    @EJB
    private UserInterestDao userInterestDao;

    @EJB
    private ProjectDao projectDao;

    @EJB
    private ProjectInterestDao projectInterestDao;

    public InterestBean() {
    }

    public void createInitialData() {
        addInterestToUser("admin@admin","New Interest");
        addInterestToUser("ricardo@ricardo", "Another Interest");
        addInterestToUser("joao@ajoao","Fallowing Interest");
        addInterestToUser("admin@admin","Interest");
        addInterestToUser("ricardo@ricardo","Bob Interest");
        addInterestToProject("Project 1", "Interest 1 project");
        addInterestToProject("Project 2", "Interest 2 project");
    }

    /**
     * Adiciona um interesse a um user
     * @param email - email do user
     * @param interestName - nome do interesse
     */
    public void addInterestToUser(String email, String interestName) {
        UserEntity user = userDao.findUserByEmail(email);
        if(user == null) {
            return;
        }
        InterestEntity interest = interestDao.findInterestByName(interestName);
        if(interest == null) {
            // Cria um novo interesse se ele não existir
            interest = new InterestEntity();
            interest.setName(interestName);
            interestDao.persist(interest);
        }
        // Cria a relação entre o user e o interesse
        UserInterestEntity userInterest = new UserInterestEntity();
        userInterest.setUser(user);
        userInterest.setInterest(interest);
        userInterestDao.persist(userInterest);

        // Adiciona o interesse ao array de interesses do usuário
        user.getInterests().add(userInterest);
        interest.getUserInterests().add(userInterest); // Adiciona o user ao interesse
        userDao.merge(user);
        interestDao.merge(interest);
    }

    /**
     * Remove um interesse de um user
     * @param email - email do user
     * @param interestName - nome do interesse
     */
    public void removeInterestFromUser(String email, String interestName) {
        UserEntity user = userDao.findUserByEmail(email);
        if(user == null) {
            return;
        }
        InterestEntity interest = interestDao.findInterestByName(interestName);
        if(interest == null) {
            return;
        }
        UserInterestEntity userInterest = userInterestDao.findUserInterestIds(user.getId(), interest.getId());
        if(userInterest == null) {
            return;
        }
        userInterest.setActive(false);
        userInterestDao.merge(userInterest);

        // Remove o interesse do array de interesses do usuário
        user.getInterests().remove(userInterest);
        interest.getUserInterests().remove(userInterest); // Remove o user do interesse
        userDao.merge(user);
        interestDao.merge(interest);
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
            // Cria um novo interesse se ele não existir
            interest = new InterestEntity();
            interest.setName(interestName);
            interestDao.persist(interest);
        }
        // Cria a relação entre o projeto e o interesse
        ProjectInterestEntity projectInterest = new ProjectInterestEntity();
        projectInterest.setProject(project);
        projectInterest.setInterest(interest);
        projectInterestDao.persist(projectInterest);

        // Adiciona o interesse ao array de interesses do projeto
        project.getInterests().add(projectInterest);
        interest.getProjectInterests().add(projectInterest); // Adiciona o projeto ao interesse
        projectDao.merge(project);
        interestDao.merge(interest);
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
        interest.getProjectInterests().remove(projectInterest); // Remove o projeto do interesse
        projectDao.merge(project);
        interestDao.merge(interest);
    }

}
