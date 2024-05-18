package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.TaskDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.dto.TaskDto;
import aor.project.innovationlab.entity.TaskEntity;
import aor.project.innovationlab.entity.UserEntity;
import aor.project.innovationlab.enums.TaskStatus;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class TaskBean {

    @EJB
    TaskDao taskDao;
    @EJB
    UserDao userDao;


    public TaskEntity toEntity(TaskDto taskDto) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskDto.getId());
        taskEntity.setTitle(taskDto.getTitle());
        taskEntity.setDescription(taskDto.getDescription());
        taskEntity.setInitialDate(taskDto.getInitialDate());
        taskEntity.setDuration(taskDto.getDuration());
        UserEntity responsible = userDao.findUserByEmail(taskDto.getResponsible());
        taskEntity.setResponsible(responsible);
        taskEntity.setAdditionalExecutors(taskDto.getAdditionalExecutors());
        taskEntity.setStatus(taskDto.getStatus());

        // Adiciona os pré-requisitos à tarefa se existirem
        if (taskDto.getPrerequisiteIds() != null) {
            for (Long prerequisiteId : taskDto.getPrerequisiteIds()) {
                TaskEntity prerequisiteTask = getTaskById(prerequisiteId);
                taskEntity.addPrerequisite(prerequisiteTask);
            }
        }
        return taskEntity;
    }

    public TaskDto toDto(TaskEntity taskEntity) {
        TaskDto taskDto = new TaskDto();
        taskDto.setId(taskEntity.getId());
        taskDto.setTitle(taskEntity.getTitle());
        taskDto.setDescription(taskEntity.getDescription());
        taskDto.setInitialDate(taskEntity.getInitialDate());
        taskDto.setDuration(taskEntity.getDuration());
        taskDto.setResponsible(taskEntity.getResponsible().getEmail());
        taskDto.setAdditionalExecutors(taskEntity.getAdditionalExecutors());
        taskDto.setStatus(taskEntity.getStatus());

        // Adiciona os IDs dos pré-requisitos à tarefa se existirem
        Set<Long> prerequisiteIds = taskEntity.getPrerequisites().stream()
                .map(TaskEntity::getId)
                .collect(Collectors.toSet());
        taskDto.setPrerequisiteIds(prerequisiteIds);
        return taskDto;
    }


//    public void addExecutorToTask(String email, long taskId) {
//        // Encontre a entidade UserEntity pelo username
//        UserEntity userEntity = userDao.findUserByEmail(email);
//
//        // Encontre a entidade TaskEntity pelo taskId
//        TaskEntity taskEntity = taskDao.findTaskById(taskId);
//
//        // Adicione o executor à tarefa
//        taskEntity.addExecutor(userEntity);
//
//        // Persista as alterações no banco de dados
//        taskDao.merge(taskEntity);
//    }
//
//    public void removeExecutorFromTask(String email, long taskId) {
//        // Encontre a entidade UserEntity pelo username
//        UserEntity userEntity = userDao.findUserByEmail(email);
//
//        // Encontre a entidade TaskEntity pelo taskId
//        TaskEntity taskEntity = taskDao.findTaskById(taskId);
//
//        // Remova o executor da tarefa
//        taskEntity.removeExecutor(userEntity);
//
//        // Persista as alterações no banco de dados
//        taskDao.merge(taskEntity);
//    }
//
//    public TaskEntity getPreRequisiteByTaskId(long taskId) {
//        TaskEntity taskEntity = taskDao.findTaskById(taskId);
//        if (taskEntity != null) {
//            return taskEntity.getPrerequisite();
//        }
//        return null;
//    }
//
//    public TaskEntity getPreRequisiteByTaskEntity(TaskEntity taskEntity) {
//        if (taskEntity != null) {
//            return taskEntity.getPrerequisite();
//        }
//        return null;
//    }

    public TaskEntity getTaskById(long taskId) {
        return taskDao.findTaskById(taskId);
    }

    public void createInitialData() {
        createTaskIfNotExists(1,"Task 1", "Description 1", "admin@admin", TaskStatus.IN_PROGRESS, "2021-01-01", "PT1H", null, "Service 1");
        createTaskIfNotExists(2,"Task 2", "Description 1", "admin@admin", TaskStatus.FINISHED, "2021-01-01", "PT1H", null, "Service 1");
        Set<Long> prerequisiteIds = Arrays.stream("1,2".split(","))
                .map(Long::parseLong)
                .collect(Collectors.toSet());
        createTaskIfNotExists(3,"Task 3", "Description 3", "admin@admin", TaskStatus.PLANNED, "2021-01-01", "PT1H", prerequisiteIds, "AoR");
    }

    public void createTaskIfNotExists(long id, String name, String description, String responsible, TaskStatus status, String initialDate, String duration, Set<Long> prerequisiteIds, String additionalExecutors) {
        TaskEntity taskEntity = taskDao.findTaskById(id);
        if (taskEntity == null) {
            taskEntity = new TaskEntity();
            taskEntity.setTitle(name);
            taskEntity.setDescription(description);
            taskEntity.setResponsible(userDao.findUserByEmail(responsible));
            taskEntity.setStatus(TaskStatus.valueOf(status.toString()));
            taskEntity.setInitialDate(java.time.LocalDate.parse(initialDate));
            taskEntity.setDuration(java.time.Duration.parse(duration));
            if (prerequisiteIds != null) {
                for (Long prerequisiteId : prerequisiteIds) {
                    TaskEntity prerequisiteTask = taskDao.findTaskById(prerequisiteId);
                    taskEntity.addPrerequisite(prerequisiteTask);
                }
            }
            if (additionalExecutors != null) {
                taskEntity.addAdditionalExecutor(additionalExecutors);
            }
            taskDao.persist(taskEntity);
        }
    }
}
