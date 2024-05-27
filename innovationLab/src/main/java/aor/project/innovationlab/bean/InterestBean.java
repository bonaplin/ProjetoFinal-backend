package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.dto.interests.InterestDto;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.utils.Color;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @EJB
    private SessionDao sessionDao;

    @Inject
    private SessionBean sessionBean;

    public InterestBean() {
    }

    public InterestDto toDto(InterestEntity interestEntity) {
        InterestDto interestDto = new InterestDto();
        interestDto.setId(interestEntity.getId());
        interestDto.setName(interestEntity.getName());
        return interestDto;
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

    public void createInterestIfNotExists(String interestName) {
        InterestEntity interest = interestDao.findInterestByName(interestName);
        if(interest == null) {
            interest = new InterestEntity();
            interest.setName(interestName);
            interestDao.persist(interest);
        }
    }

    /**
     * Adiciona um interesse a um user
     * @param email - email do user
     * @param interestName - nome do interesse
     */
    public InterestDto addInterestToUser(String email, String interestName) {
        UserEntity user = userDao.findUserByEmail(email);
        if(user == null) {
            throw new IllegalArgumentException("User not found");
        }
        InterestEntity interest = interestDao.findInterestByName(interestName);
        if(interest == null) {
            createInterestIfNotExists(interestName);
            interest = interestDao.findInterestByName(interestName);
        }
        UserInterestEntity userInterest = userInterestDao.userHasInterest(user, interest);
        if(userInterest == null){
            userInterest = new UserInterestEntity();
            userInterest.setInterest(interest);
            userInterest.setUser(user);
            userInterestDao.persist(userInterest);
            // Adiciona o interesse ao array de interesses do usuário
            user.getInterests().add(userInterest);
            // Adiciona o user ao interesse
            interest.getUserInterests().add(userInterest);
        }else{
            userInterest.setActive(true);
        }

        userDao.merge(user);
        interestDao.merge(interest);
        userInterestDao.merge(userInterest);
        return toDto(interest);
    }

    /**
     * Remove um interesse de um user
     * @param email - email do user
     * @param interestName - nome do interesse
     */
    public void removeInterestFromUser(String email, String interestName) {
        UserEntity user = userDao.findUserByEmail(email);
        if(user == null) {
            throw new IllegalArgumentException("User not found");
        }
        InterestEntity interest = interestDao.findInterestByName(interestName);
        if(interest == null) {
            throw new IllegalArgumentException("Interest not found");
        }
        UserInterestEntity userInterest = userInterestDao.userHasInterest(user, interest);
        if(userInterest == null) {
            throw new IllegalArgumentException("User dont have this interest");
        }
        userInterest.setActive(false);
        // Remove o interesse do array de interesses do user
        user.getInterests().remove(userInterest);
        interest.getUserInterests().remove(userInterest); // Remove o user do interesse
        userDao.merge(user);
        interestDao.merge(interest);

        userInterestDao.merge(userInterest);
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

    public List<InterestDto> getUserInterests(String email) {
        UserEntity user = userDao.findUserByEmail(email);
        if(user == null) {
            return new ArrayList<>();
        }
        List<InterestEntity> interestEntities = interestDao.getUserInterests(user.getId());
        return interestEntities.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<InterestDto> getUserInterests(String token, String email) {
        sessionBean.validateUserToken(token);
        return getUserInterests(email);
    }

    public List<InterestDto> getAllInterests(String token) {
        if(token == null) {
            throw new IllegalArgumentException("Token is required");
        }
        sessionBean.validateUserToken(token);
        List<InterestEntity> interestEntities = interestDao.getAllInterests();
        return interestEntities.stream().map(this::toDto).collect(Collectors.toList());
    }

    public InterestDto addInterest(String token, InterestDto interestDto) {
        if(token == null) {
            throw new IllegalArgumentException("Token is required");
        }
        if(interestDto == null) {
            throw new IllegalArgumentException("Interest is required");
        }
        sessionBean.validateUserToken(token);
        if(interestDto.getName() == null) {
            throw new IllegalArgumentException("Interest name is required");
        }
        InterestEntity interest = interestDao.findInterestByName(interestDto.getName());
        if(interest == null) {
            createInterestIfNotExists(interestDto.getName());
        }
        SessionEntity session = sessionDao.findSessionByToken(token);
        if(session == null) {
            throw new IllegalArgumentException("Session not found");
        }
        System.out.println("Adding interest to user");
        return addInterestToUser(session.getUser().getEmail(), interestDto.getName());
    }

    public void deleteInterest(String token, InterestDto interestDto) {
        if(token == null) {
            throw new IllegalArgumentException("Token is required");
        }
        if(interestDto == null) {
            throw new IllegalArgumentException("Interest is required");
        }
        sessionBean.validateUserToken(token);
        if(interestDto.getName() == null) {
            throw new IllegalArgumentException("Interest name is required");
        }
//        InterestEntity interest = interestDao.findInterestByName(interestDto.getName());
//        if(interest == null) {
//            throw new IllegalArgumentException("Interest not found");
//        }
        SessionEntity session = sessionDao.findSessionByToken(token);
        if(session == null) {
            throw new IllegalArgumentException("Session not found");
        }

        UserEntity user = session.getUser();
        System.out.println(Color.GREEN + "Deleting interest from user" + Color.GREEN);
        removeInterestFromUser(user.getEmail(), interestDto.getName());
    }
}
