package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.LabDao;
import aor.project.innovationlab.dto.LabDto;
import aor.project.innovationlab.entity.LabEntity;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LabBean {

    @EJB
    private LabDao labDao;


    public LabEntity toEntity(LabDto dto) {
        LabEntity entity = new LabEntity();
//        entity.setName(dto.getName());

        entity.setLocation(dto.getLocation());

        return entity;
    }

    public LabDto toDto(LabEntity entity) {
        LabDto dto = new LabDto();
        dto.setId(entity.getId());
//        dto.setName(entity.getName());

        dto.setLocation(entity.getLocation());

        return dto;
    }

    public void createInitialData() {
        createLabIfNotExists("Coimbra");
        createLabIfNotExists("Leiria");
        createLabIfNotExists("Porto");
        createLabIfNotExists("Lisboa");
        System.out.println("Initial labs created");
    }

    private void createLabIfNotExists(String location) {
        if (labDao.findLabByLocation(location) == null) {
            LabDto dto = new LabDto();
            dto.setName(location);

            dto.setLocation(location);

            labDao.persist(toEntity(dto));
        }
    }
}
