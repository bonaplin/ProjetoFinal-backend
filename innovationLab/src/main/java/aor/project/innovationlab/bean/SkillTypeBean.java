package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.SkillTypeDao;
import aor.project.innovationlab.dto.SkillTypeDto;
import aor.project.innovationlab.entity.SkillTypeEntity;
import aor.project.innovationlab.enums.SkillType;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ApplicationPath;

@ApplicationScoped
public class SkillTypeBean {

    @EJB
    private SkillTypeDao skillTypeDao;

    /**
     * Convert dto to entity
     * @param dto
     * @return entity object
     */
    public SkillTypeEntity toEntity(SkillTypeDto dto) {
        SkillTypeEntity entity = new SkillTypeEntity();
        entity.setId(dto.getId());
        entity.setName(SkillType.valueOf(dto.getName()));
        return entity;
    }

    /**
     * Convert entity to dto
     * @param entity
     * @return dto object
     */
    public SkillTypeDto toDto(SkillTypeEntity entity) {
        SkillTypeDto dto = new SkillTypeDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName().name());
        return dto;
    }

    /**
     * Create initial data
     */
    public void createInitialData() {
        createSkillTypeIfNotExists(SkillType.KNOWLEDGE.name());
        createSkillTypeIfNotExists(SkillType.SOFTWARE.name());
        createSkillTypeIfNotExists(SkillType.HARDWARE.name());
        createSkillTypeIfNotExists(SkillType.TOOLS.name());
    }

    private void createSkillTypeIfNotExists(String name) {
        SkillType skillType = SkillType.valueOf(name);
        if(skillTypeDao.findSkillTypeByName(skillType) == null) {
            SkillTypeDto dto = new SkillTypeDto();
            dto.setName(name);
            SkillTypeEntity entity = toEntity(dto);
            skillTypeDao.persist(entity);
        }
    }
}
