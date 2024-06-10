package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.LabDao;
import aor.project.innovationlab.dto.lab.LabDto;
import aor.project.innovationlab.entity.LabEntity;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            dto.setLocation(location);

            labDao.persist(toEntity(dto));
        }
    }

    public List<LabDto> findAllLabs() {
        List<LabEntity> labs = labDao.findAllLabs();
        if(labs == null) return new ArrayList<>();

        return labs.stream().map(this::toDto).collect(Collectors.toList());
    }

}
