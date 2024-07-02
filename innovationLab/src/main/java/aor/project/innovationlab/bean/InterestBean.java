package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.dto.interests.InterestDto;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.utils.Color;
import aor.project.innovationlab.utils.logs.LoggerUtil;
import com.sun.tools.jconsole.JConsoleContext;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TransactionRequiredException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
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
            persistInterest(interest);
        }
    }

    private void persistInterest(InterestEntity interest) {
        String log = "Attempting to persist interest";
        try {
            interestDao.persist(interest);
            LoggerUtil.logInfo(log,"Interest persisted: "+interest.getName(),null,null);
        }
        catch (IllegalArgumentException | TransactionRequiredException | EntityNotFoundException e) {
            LoggerUtil.logError(log,"Error persisting interest: " + e.getMessage(),null,null);
            throw new RuntimeException("Error persisting interest: " + e.getMessage());
        } catch (PersistenceException e) {
            LoggerUtil.logError(log,"Persistence error persisting interest: " + e.getMessage(),null,null);
            throw new RuntimeException("Persistence error persisting interest: " + e.getMessage());
        } catch (Exception e) {
            LoggerUtil.logError(log,"Unexpected error persisting interest: " + e.getMessage(),null,null);
            throw new RuntimeException("Unexpected error persisting interest: " + e.getMessage());
        }

    }

    /**
     * Adiciona um interesse a um user
     * @param email - email do user
     * @param interestName - nome do interesse
     */
    public InterestDto addInterestToUser(String email, String interestName) {
        String log = "Attempting to add interest to user";
        UserEntity user = userDao.findUserByEmail(email);
        if(user == null) {
            LoggerUtil.logError(log,"User not found to add the "+interestName,email,null);
            throw new IllegalArgumentException("User not found");
        }
        InterestEntity interest = interestDao.findInterestByName(interestName);
        if(interest == null) {
            createInterestIfNotExists(interestName);
            interest = interestDao.findInterestByName(interestName);
            LoggerUtil.logInfo(log,"Interest created: "+interestName,email,null);
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
            LoggerUtil.logInfo(log,"UserInterest created",email,null);
        }else{
            userInterest.setActive(true);
            LoggerUtil.logInfo(log,"UserInterest already exists, just setActive",email,null);
        }

        userDao.merge(user);
        interestDao.merge(interest);
        userInterestDao.merge(userInterest);
        LoggerUtil.logInfo(log,"Interest added to user",null,null);
        return toDto(interest);
    }


    public List<InterestDto> getProjectInterests(String token, long projectId) {

        String log = "Attempt to get interests for project info";
        SessionEntity sessionEntity = sessionDao.findSessionByToken(token);
        if(sessionEntity == null){
            LoggerUtil.logError(log,"Session not found.",null,token);
            throw new IllegalArgumentException("Session not found.");
        }

        List<InterestEntity> interests = projectInterestDao.findInterestByProjectId(projectId);
        if(interests == null) {
            return new ArrayList<>();
        }
        return interests.stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Remove um interesse de um user
     * @param email - email do user
     * @param interestName - nome do interesse
     */
    public void removeInterestFromUser(String email, String interestName) {
        String log = "Attempting to remove interest from user";
        UserEntity user = userDao.findUserByEmail(email);
        if(user == null) {
            LoggerUtil.logError(log,"User not found to remove the "+interestName,email,null);
            throw new IllegalArgumentException("User not found");
        }
        InterestEntity interest = interestDao.findInterestByName(interestName);
        if(interest == null) {
            LoggerUtil.logError(log,"Interest not found to remove from user",email,null);
            throw new IllegalArgumentException("Interest not found");
        }
        UserInterestEntity userInterest = userInterestDao.userHasInterest(user, interest);
        if(userInterest == null) {
            LoggerUtil.logError(log,"User dont have this interest",email,null);
            throw new IllegalArgumentException("User dont have this interest");
        }
        userInterest.setActive(false);
        // Remove o interesse do array de interesses do user
        user.getInterests().remove(userInterest);
        interest.getUserInterests().remove(userInterest); // Remove o user do interesse
        userDao.merge(user);
        interestDao.merge(interest);

        userInterestDao.merge(userInterest);
        LoggerUtil.logInfo(log,"Interest removed from user",email,null);
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

    public List<InterestDto> getProjectInterests(String projectName) {
        ProjectEntity project = projectDao.findProjectByName(projectName);
        if(project == null) {
            return new ArrayList<>();
        }
        List<InterestEntity> interestEntities = interestDao.getProjectInterests(project.getId());
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
        String log = "Attempting to add interest";
        if(token == null) {
            LoggerUtil.logError(log,"Token is required",null,null);
            throw new IllegalArgumentException("Token is required");
        }
        String user = sessionDao.findSessionByToken(token).getUser().getEmail();
        if(user == null) {
            LoggerUtil.logError(log,"User not found",null,token);
            throw new IllegalArgumentException("User not found");
        }
        if(interestDto == null) {
            LoggerUtil.logError(log,"Interest is required",user,token);
            throw new IllegalArgumentException("Interest is required");
        }
        sessionBean.validateUserToken(token);
        if(interestDto.getName() == null) {
            LoggerUtil.logError(log,"Interest name is required",user,token);
            throw new IllegalArgumentException("Interest name is required");
        }
        InterestEntity interest = interestDao.findInterestByName(interestDto.getName());
        if(interest == null) {
            createInterestIfNotExists(interestDto.getName());
            LoggerUtil.logInfo(log,"Interest created: "+interestDto.getName(),user,token);
        }

        return addInterestToUser(user, interestDto.getName());
    }

    public void deleteInterest(String token, InterestDto interestDto) {
        String log = "Attempting to delete interest";
        if(token == null) {
            LoggerUtil.logError(log,"Token is required",null,null);
            throw new IllegalArgumentException("Token is required");
        }
        if(interestDto == null) {
            LoggerUtil.logError(log,"Interest is required",null,token);
            throw new IllegalArgumentException("Interest is required");
        }
        sessionBean.validateUserToken(token);
        if(interestDto.getName() == null) {
            throw new IllegalArgumentException("Interest name is required");
        }
        SessionEntity session = sessionDao.findSessionByToken(token);
        if(session == null) {
            throw new IllegalArgumentException("Session not found");
        }

        UserEntity user = session.getUser();
        removeInterestFromUser(user.getEmail(), interestDto.getName());
    }

    public List<InterestDto> getProjectInterests(String auth, String projectName) {
        sessionBean.validateUserToken(auth);
        return getProjectInterests(projectName);
    }
}
