package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.SkillDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.dao.UserSkillDao;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.entity.UserSkillEntity;
import aor.project.innovationlab.enums.SkillType;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;

import aor.project.innovationlab.dto.skill.SkillDto;
import aor.project.innovationlab.entity.SkillEntity;

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
    public void addSkillToUser(String email, String skillName) {
        UserEntity user = userDao.findUserByEmail(email);
        if(user == null) {
            return;
        }
        SkillEntity skill = skillDao.findSkillByName(skillName);
        if(skill == null) {
            // Cria uma nova habilidade se ela não existir
            skill = new SkillEntity();
            skill.setName(skillName);
            skillDao.persist(skill);
        }
        // Cria a relação entre o user e a habilidade
        UserSkillEntity userSkill = new UserSkillEntity();
        userSkill.setUser(user);
        userSkill.setSkill(skill);
        userSkillDao.persist(userSkill);

        // Adiciona a habilidade ao array de habilidades do user
        user.getUserSkills().add(userSkill);
        skill.getUserSkills().add(userSkill); // Adiciona o user à habilidade
        userDao.merge(user);
        skillDao.merge(skill);
    }

    /**
     * Remove uma habilidade de um user
     * @param email - email do user
     * @param skillName - nome da habilidade a remover
     */
    public void removeSkillFromUser(String email, String skillName) {
        UserEntity user = userDao.findUserByEmail(email);
        if(user == null) {
            return;
        }
        SkillEntity skill = skillDao.findSkillByName(skillName);
        if(skill == null) {
            return;
        }
        UserSkillEntity userSkill = userDao.findUserSkillIds(user.getId(), skill.getId());
        if(userSkill == null) {
            return;
        }
        userSkill.setActive(false);
        userSkillDao.merge(userSkill);

        // Remove a habilidade do array de habilidades do user
        user.getUserSkills().remove(userSkill);
        skill.getUserSkills().remove(userSkill); // Remove o user da habilidade
        userDao.merge(user);
        skillDao.merge(skill);
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
        List<SkillEntity> skillEntities = skillDao.getUserSkills(user.getId());
        return skillEntities.stream().map(this::toDto).collect(Collectors.toList());
    }


}
