package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.dto.product.ProductToCreateProjectDto;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.SkillType;

import aor.project.innovationlab.utils.logs.LoggerUtil;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;

import aor.project.innovationlab.dto.skill.SkillDto;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class SkillBean {

    @EJB
    private SkillDao skillDao;

    @EJB
    private UserDao userDao;

    @EJB
    private UserSkillDao userSkillDao;

    @EJB
    private SessionDao sessionDao;

    @EJB
    ProjectSkillDao ProjectSkillDao;

    @Inject
    private SessionBean sessionBean;

    @Inject
    private SkillBean skillBean;

    /**
     * Convert dto to entity
     * @param dto
     * @return entity object
     */
    public SkillEntity toEntity(SkillDto dto) {
        SkillEntity entity = new SkillEntity();
        entity.setName(dto.getName());
        entity.setSkillType(SkillType.valueOf(dto.getType()));
        return entity;
    }

    /**
     * Convert entity to dto
     * @param entity
     * @return dto object
     */
    public SkillDto toDto(SkillEntity entity) {
        SkillDto dto = new SkillDto();
        dto.setId(entity.getId());
        dto.setType(entity.getSkillType().name());
        dto.setName(entity.getName());
        return dto;
    }

    /**
     * Create initial data
     */
    public void createInitialData() {
        createSkillIfNotExists("Java", SkillType.KNOWLEDGE);
        createSkillIfNotExists("Assembly", SkillType.HARDWARE);
        createSkillIfNotExists("macOS", SkillType.SOFTWARE);
        createSkillIfNotExists("IntelIJ", SkillType.TOOLS);
    }

    private void createSkillIfNotExists(String name, SkillType type) {
        if(skillDao.findSkillByName(name) == null) {
            SkillDto dto = new SkillDto();
            dto.setName(name);
            dto.setType(type.name());

            SkillEntity entity = toEntity(dto);
            skillDao.persist(entity);
        }
    }

    /**
     * Adiciona uma habilidade a um user
     * @param email - email do user
     * @param skillName - nome da habilidade
     */
    public SkillDto addSkillToUser(String email, String skillName, SkillType type) {
        String log = "Adding skill to user";
        UserEntity user = userDao.findUserByEmail(email);
        if(user == null) {
            LoggerUtil.logError(log, "User not found", email, null);
            throw new IllegalArgumentException("User not found");
        }

        SkillEntity skill = skillDao.findSkillByName(skillName);

        if(skill == null) {
            createSkillIfNotExists(skillName, type);
        }
        // Verifica se já existe uma associação entre o user e a habilidade
        UserSkillEntity userSkill = userSkillDao.userHasSkill(user, skill);
        if(userSkill == null) {
            // Cria a relação entre o user e a habilidade
            userSkill = new UserSkillEntity();
            userSkill.setUser(user);
            userSkill.setSkill(skill);
            LoggerUtil.logInfo(log, "Creating new user skill", email, null);
            userSkillDao.persist(userSkill);

            // Adiciona a habilidade ao array de habilidades do user
            user.getSkills().add(userSkill);
            skill.getUserSkills().add(userSkill); // Adiciona o user à habilidade
        } else {
            // Se a associação já existir, apenas altera o active para true
            userSkill.setActive(true);
            LoggerUtil.logInfo(log, "User already have this skill, just turn active", email, null);
        }
        userDao.merge(user);
        skillDao.merge(skill);
        userSkillDao.merge(userSkill);
        LoggerUtil.logInfo(log, "Skill: "+ skillName +"added to user", email, null);
        return toDto(skill);
    }

    /**
     * Remove uma habilidade de um user
     * @param email - email do user
     * @param skillName - nome da habilidade a remover
     */
    public void removeSkillFromUser(String email, String skillName) {
        String log = "Removing skill from user";
        UserEntity user = userDao.findUserByEmail(email);
        if(user == null) {
            LoggerUtil.logError(log, "User not found", email, null);
            throw new IllegalArgumentException("User not found");
        }
        SkillEntity skill = skillDao.findSkillByName(skillName);
        if(skill == null) {
            LoggerUtil.logError(log, "Skill not found", email, null);
            throw new IllegalArgumentException("Skill not found");
        }
        UserSkillEntity userSkill = userSkillDao.userHasSkill(user, skill);
        if(userSkill == null) {
            LoggerUtil.logError(log, "User dont have this skill", email, null);
            throw new IllegalArgumentException("User dont have this skill");
        }
        userSkill.setActive(false);
        // Remove o skill do array de skills do user
        user.getSkills().remove(userSkill);
        skill.getUserSkills().remove(userSkill); // Remove o user da habilidade
        userDao.merge(user);
        skillDao.merge(skill);

        userSkillDao.merge(userSkill); // Atualiza a userSkill no banco de dados
        LoggerUtil.logInfo(log, "Skill: "+ skillName +"removed from user", email, null);
    }

    public List<SkillDto> getProjectSkills (String token, long projectId) {

        String log = "Attempt to get products for project info";
        SessionEntity sessionEntity = sessionDao.findSessionByToken(token);
        if(sessionEntity == null){
            LoggerUtil.logError(log,"Session not found.",null,token);
            throw new IllegalArgumentException("Session not found.");
        }

        List<SkillEntity> products = ProjectSkillDao.findSkillsByProjectId(projectId);
        if(products == null) {
            return new ArrayList<>();
        }
        return products.stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Return a list of skills for a user in dto format
     * @param email - user email
     * @return
     */
    public List<SkillDto> getUserSkills(String email) {
        UserEntity user = userDao.findUserByEmail(email);
        if(user == null) {
            return new ArrayList<>();
        }
        List<SkillEntity> skillEntities = userSkillDao.getUserSkills(user.getId());
        return skillEntities.stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<SkillDto> getUserSkills(String token, String email) {
        sessionBean.validateUserToken(token);
        SessionEntity se = sessionDao.findSessionByToken(token);
        if(se == null) {
            throw new IllegalArgumentException("Invalid token");
        }
        if(email == null) {
            String mail = se.getUser().getEmail();
            getUserSkills(mail);
        }
        boolean privateProfile = se.getUser().getPrivateProfile();
        if(privateProfile && !se.getUser().getEmail().equals(email)) {
            return new ArrayList<>();
        }
        return getUserSkills(email);
    }

    /**
     * Add a new skill to a user
     * @param token
     * @param skillDto
     */
    public SkillDto addSkill(String token, SkillDto skillDto) {
        String log = "Adding skill to user";
        if(token == null) {
            LoggerUtil.logError(log, "Token is required", null, null);
            throw new IllegalArgumentException("Token is required");
        }
        if(skillDto == null) {
            LoggerUtil.logError(log, "Skill is required", null, null);
            throw new IllegalArgumentException("Skill is required");
        }
        sessionBean.validateUserToken(token);
        if(skillDto.getName() == null|| skillDto.getType() == null  ){
            LoggerUtil.logError(log, "Skill name and type are required", null, null);
            throw new IllegalArgumentException("Skill name and type are required");
        }
        SkillEntity skill = skillDao.findSkillByName(skillDto.getName());
        if(skill == null) {
            createSkillIfNotExists(skillDto.getName(), SkillType.valueOf(skillDto.getType()));
        }
        SessionEntity session = sessionDao.findSessionByToken(token);
        if(session == null) {
            LoggerUtil.logError(log, "Invalid token", null, token);
            throw new IllegalArgumentException("Invalid token");
        }
        UserEntity user = session.getUser();
        if(user == null) {
            LoggerUtil.logError(log, "User not found", null, token);
            throw new IllegalArgumentException("User not found");
        }
        LoggerUtil.logInfo(log, "Adding skill to user", user.getEmail(), token);
        return addSkillToUser(user.getEmail(), skillDto.getName(), SkillType.valueOf(skillDto.getType()));
    }

    /**
     * Return all skill types
     * @param token
     * @return
     */
    public List<String> getAllSkillType(String token) {
        sessionBean.validateUserToken(token);
        return skillDao.getAllSkillType();
    }

    /**
     * Return all skills
     * @param token
     * @return
     */
    public Object getAllSkills(String token, String skillType) {
        if(token == null) {
            throw new IllegalArgumentException("Token is required.");
        }
        sessionBean.validateUserToken(token);
        List<SkillEntity> skills = skillDao.findAll();
        return skills.stream().map(this::toDto).collect(Collectors.toList());
    }

    /**
     * Delete a skill from a user, inactive the skill
     * @param token
     * @param skillDto
     */
    public void deleteSkill(String token, SkillDto skillDto) {
        String log = "Deleting skill from user";
        if(token == null) {
            LoggerUtil.logError(log, "Token is required", null, null);
            throw new IllegalArgumentException("Token is required");
        }
        if(skillDto == null) {
            LoggerUtil.logError(log, "Skill is required", null, token);
            throw new IllegalArgumentException("Skill is required");
        }
        sessionBean.validateUserToken(token);
        if(skillDto.getName() == null || skillDto.getType() == null){
            LoggerUtil.logError(log, "Skill name and type are required", null, token);
            throw new IllegalArgumentException("Skill name and type are required");
        }
        SessionEntity session = sessionDao.findSessionByToken(token);
        if(session == null) {
            LoggerUtil.logError(log, "Invalid token", null, token);
            throw new IllegalArgumentException("Invalid token");
        }

        UserEntity user = session.getUser();
        removeSkillFromUser(user.getEmail(), skillDto.getName());
    }
    public List<?> getSkills(String token, String name, String type, String userEmail, String projectName) {
        sessionBean.validateUserToken(token);
        List<SkillEntity> skills = skillDao.findSkills(name, type, userEmail, projectName);
        return skills.stream().map(this::toDto).collect(Collectors.toList());
    }
}
