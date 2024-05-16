package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.SkillDao;
import aor.project.innovationlab.enums.SkillType;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;

import aor.project.innovationlab.dao.SkillDao;
import aor.project.innovationlab.dto.SkillDto;
import aor.project.innovationlab.entity.SkillEntity;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class SkillBean {

    @EJB
    private SkillDao skillDao;

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
}
