package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.SessionDao;
import aor.project.innovationlab.dao.SkillDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.dao.UserSkillDao;
import aor.project.innovationlab.entity.SessionEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.entity.UserSkillEntity;
import aor.project.innovationlab.enums.SkillType;

import aor.project.innovationlab.utils.Color;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;

import aor.project.innovationlab.dto.skill.SkillDto;
import aor.project.innovationlab.entity.SkillEntity;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class SkillBean {

    @EJB
    private SkillDao skillDao;

    @EJB
    private UserDao userDao;

    @EJB
    private UserSkillDao userSkillDao;

    @EJB
    private SessionDao sessionDao;

    @Inject
    private SessionBean sessionBean;

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

        System.out.println("Initial skills created");

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
        System.out.println(email + " " + skillName + " " + type);
        UserEntity user = userDao.findUserByEmail(email);
        if(user == null) {
            throw new IllegalArgumentException("User not found");
        }
        System.out.println("User found");
        SkillEntity skill = skillDao.findSkillByName(skillName);

        if(skill == null) {
            // Cria a skill se ela não existir
            createSkillIfNotExists(skillName, type);
        }
        // Verifica se já existe uma associação entre o user e a habilidade
        UserSkillEntity userSkill = userSkillDao.userHasSkill(user, skill);
        if(userSkill == null) {
            // Cria a relação entre o user e a habilidade
            userSkill = new UserSkillEntity();
            userSkill.setUser(user);
            userSkill.setSkill(skill);
            userSkillDao.persist(userSkill);

            // Adiciona a habilidade ao array de habilidades do user
            user.getUserSkills().add(userSkill);
            skill.getUserSkills().add(userSkill); // Adiciona o user à habilidade
        } else {
            // Se a associação já existir, apenas altera o active para true
            userSkill.setActive(true);
        }
        userDao.merge(user);
        skillDao.merge(skill);
        userSkillDao.merge(userSkill);

        return toDto(skill);
    }

    /**
     * Remove uma habilidade de um user
     * @param email - email do user
     * @param skillName - nome da habilidade a remover
     */
    public void removeSkillFromUser(String email, String skillName) {
        UserEntity user = userDao.findUserByEmail(email);
        if(user == null) {
            throw new IllegalArgumentException("User not found");
        }
        SkillEntity skill = skillDao.findSkillByName(skillName);
        if(skill == null) {
            throw new IllegalArgumentException("Skill not found");
        }
        UserSkillEntity userSkill = userSkillDao.userHasSkill(user, skill);
        if(userSkill == null) {
            throw new IllegalArgumentException("User dont have this skill");
        }
        userSkill.setActive(false);
        // Remove o skill do array de skills do user
        user.getUserSkills().remove(userSkill);
        skill.getUserSkills().remove(userSkill); // Remove o user da habilidade
        userDao.merge(user);
        skillDao.merge(skill);

        userSkillDao.merge(userSkill); // Atualiza a userSkill no banco de dados
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
        return getUserSkills(email);
    }

    /**
     * Add a new skill to a user
     * @param token
     * @param skillDto
     */
    public SkillDto addSkill(String token, SkillDto skillDto) {
        if(token == null) {
            throw new IllegalArgumentException("Token is required");
        }
        if(skillDto == null) {
            throw new IllegalArgumentException("Skill is required");
        }
        sessionBean.validateUserToken(token);
        if(skillDto.getName() == null|| skillDto.getType() == null  ){
            throw new IllegalArgumentException("Skill name and type are required");
        }
        SkillEntity skill = skillDao.findSkillByName(skillDto.getName());
        if(skill == null) {
            createSkillIfNotExists(skillDto.getName(), SkillType.valueOf(skillDto.getType()));
        }
        SessionEntity session = sessionDao.findSessionByToken(token);
        if(session == null) {
            throw new IllegalArgumentException("Invalid token");
        }
        UserEntity user = session.getUser();
        if(user == null) {
            throw new IllegalArgumentException("User not found");
        }
        System.out.println("Adding skill to user");
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
    public Object getAllSkills(String token) {
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
        if(token == null) {
            throw new IllegalArgumentException("Token is required");
        }
        if(skillDto == null) {
            throw new IllegalArgumentException("Skill is required");
        }
        sessionBean.validateUserToken(token);
        if(skillDto.getName() == null || skillDto.getType() == null){
            throw new IllegalArgumentException("Skill name and type are required");
        }
//        SkillEntity skill = skillDao.findSkillByName(skillDto.getName());
//        if(skill == null) {
//            throw new IllegalArgumentException("Skill not found");
//        }
        SessionEntity session = sessionDao.findSessionByToken(token);
        if(session == null) {
            throw new IllegalArgumentException("Invalid token");
        }

        UserEntity user = session.getUser();
        System.out.println(Color.GREEN + "Deleting skill from user" + Color.GREEN);
        removeSkillFromUser(user.getEmail(), skillDto.getName());
    }
}
