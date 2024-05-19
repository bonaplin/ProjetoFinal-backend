package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.dto.ProjectDto;
import aor.project.innovationlab.entity.InterestEntity;
import aor.project.innovationlab.entity.ProjectEntity;
import aor.project.innovationlab.entity.ProjectInterestEntity;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;

@ApplicationScoped
public class ProjectBean {

    private static final long serialVersionUID = 1L;

    @EJB
    private ProjectDao projectDao;

    @EJB
    private InterestDao interestDao;

    @EJB
    private ProjectInterestDao projectInterestDao;

    @EJB
    UserDao userDao;

    @EJB
    LabDao labDao;

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
        }
    }
}
