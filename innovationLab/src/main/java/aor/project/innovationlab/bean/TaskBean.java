package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.TaskDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.dto.TaskDto;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.TaskStatus;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collections;
import java.util.HashSet;
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
        UserEntity creator = userDao.findUserByEmail(taskDto.getCreator());
        taskEntity.setCreator(creator);

        // Adiciona os executores adicionais à tarefa se existirem
        Set<TaskExecutorAdditionalEntity> additionalExecutors = new HashSet<>();
        for (String additionalExecutorName : taskDto.getAdditionalExecutors()) {
            TaskExecutorAdditionalEntity additionalExecutor = new TaskExecutorAdditionalEntity();
            additionalExecutor.setName(additionalExecutorName);
            additionalExecutor.setTask(taskEntity);
            additionalExecutor.setActive(true);
            additionalExecutors.add(additionalExecutor);
        }
        taskEntity.setAdditionalExecutors(additionalExecutors);

        Set<TaskExecutorEntity> executors = new HashSet<>();
        for (String executorEmail : taskDto.getExecutors()) {
            UserEntity executor = userDao.findUserByEmail(executorEmail);
            TaskExecutorEntity taskExecutorEntity = new TaskExecutorEntity();
            taskExecutorEntity.setExecutor(executor);
            taskExecutorEntity.setTask(taskEntity);
            taskExecutorEntity.setActive(true);
            executors.add(taskExecutorEntity);
        }
        taskEntity.setExecutors(executors);

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
        taskDto.setCreator(taskEntity.getCreator().getEmail());

        Set<String> additionalExecutors = taskEntity.getAdditionalExecutors().stream()
                .filter(TaskExecutorAdditionalEntity::isActive)
                .map(TaskExecutorAdditionalEntity::getName)
                .collect(Collectors.toSet());
        taskDto.setAdditionalExecutors(additionalExecutors);

        taskDto.setStatus(taskEntity.getStatus());

        Set<String> executors = taskEntity.getExecutors().stream()
                .filter(TaskExecutorEntity::isActive)
                .map(executorEntity -> executorEntity.getExecutor().getEmail())
                .collect(Collectors.toSet());
        taskDto.setExecutors(executors);

        // Adiciona os IDs dos pré-requisitos à tarefa se existirem
        Set<Long> prerequisiteIds = taskEntity.getPrerequisites().stream()
                .map(TaskEntity::getId)
                .collect(Collectors.toSet());
        taskDto.setPrerequisiteIds(prerequisiteIds);
        return taskDto;
    }

    public TaskEntity getTaskById(long taskId) {
        return taskDao.findTaskById(taskId);
    }

    public void createInitialData() {
        createTaskIfNotExists(1,"Task 1", "Description 1", "admin@admin", TaskStatus.IN_PROGRESS, "2021-01-01", "PT1H",null);
        createTaskIfNotExists(2,"Task 2", "Description 1", "admin@admin", TaskStatus.FINISHED, "2021-01-01", "PT1H",null);
        createTaskIfNotExists(3,"Task 3", "Description 3", "admin@admin", TaskStatus.PLANNED, "2021-01-01", "PT1H", null);
        addPrerequisite(3, 1);
        addPrerequisite(3, 2);
        addPrerequisite(2,1);
    }

    /**
     * Cria uma tarefa se não existir.
     * @param id
     * @param name
     * @param description
     * @param responsible
     * @param status
     * @param initialDate
     * @param duration
     * @param prerequisiteIds
     */
    public void createTaskIfNotExists(long id, String name,
                                      String description, String responsible,
                                      TaskStatus status, String initialDate,
                                      String duration, Set<Long> prerequisiteIds) {
        TaskEntity taskEntity = taskDao.findTaskById(id);
        if (taskEntity == null) {
            taskEntity = new TaskEntity();
            taskEntity.setTitle(name);
            taskEntity.setDescription(description);
            taskEntity.setResponsible(userDao.findUserByEmail(responsible));
            taskEntity.setStatus(TaskStatus.valueOf(status.toString()));
            taskEntity.setInitialDate(java.time.LocalDate.parse(initialDate));
            taskEntity.setDuration(java.time.Duration.parse(duration));
            taskEntity.setCreator(userDao.findUserByEmail(responsible));

            if (prerequisiteIds != null) {
                for (Long prerequisiteId : prerequisiteIds) {
                    TaskEntity prerequisiteTask = taskDao.findTaskById(prerequisiteId);
                    taskEntity.addPrerequisite(prerequisiteTask);
                }
            }

            taskDao.persist(taskEntity);
            addExecutorToTask(id, "admin@admin");
            addExecutorToTask(id, "ricardo@ricardo");
            addAdditionalExecutorToTask(id, "Executor 3");
            addAdditionalExecutorToTask(id, "Executor 4");
            taskDao.merge(taskEntity);
        }
    }

    /**
     * Soft delete de uma tarefa.
     * @param taskId
     */
    //tem de ser removida de todas as tasks que têm esta task como pré-requisito
    public void desactivate(long taskId) {
        TaskEntity taskEntity = taskDao.findTaskById(taskId);
        taskEntity.setActive(false);
        taskDao.merge(taskEntity);
    }

    public Set<TaskEntity> getPrerequisiteTasksForTask(long taskId) {
        TaskEntity taskEntity = taskDao.findTaskById(taskId);
        if (taskEntity != null) {
            return taskEntity.getPrerequisites();
        }
        return null;
    }

    public Set<TaskDto> getTasksForWhichTaskIsPrerequisite(long taskId) {
        TaskEntity taskEntity = findTaskById(taskId);
        if (taskEntity == null) {
            return Collections.emptySet();
        }
        return convertPrerequisiteTasksToDto(taskEntity);
    }

    private TaskEntity findTaskById(long taskId) {
        return taskDao.findTaskById(taskId);
    }

    private Set<TaskDto> convertPrerequisiteTasksToDto(TaskEntity taskEntity) {
        Set<TaskPrerequisiteEntity> prerequisiteTaskEntities = taskEntity.getPrerequisiteForTasks();
        Set<TaskEntity> prerequisiteTasks = prerequisiteTaskEntities.stream()
                .map(TaskPrerequisiteEntity::getTask)
                .collect(Collectors.toSet());
        return prerequisiteTasks.stream()
                .map(this::toDto)
                .collect(Collectors.toSet());
    }

    //testado
    public void addPrerequisite(long taskId, long prerequisiteId) {
        TaskEntity taskEntity = taskDao.findTaskById(taskId);
        TaskEntity prerequisiteTask = taskDao.findTaskById(prerequisiteId);
        if(taskEntity == null || prerequisiteTask == null) {
            return;
        }
        taskEntity.addPrerequisite(prerequisiteTask);
        taskDao.merge(taskEntity);
    }

    //testado
    public void removePrerequisite(long taskId, long prerequisiteId) {
        TaskEntity taskEntity = taskDao.findTaskById(taskId);
        TaskEntity prerequisiteTask = taskDao.findTaskById(prerequisiteId);
        if(taskEntity == null || prerequisiteTask == null) {
            return;
        }
        taskEntity.removePrerequisite(prerequisiteTask);
        taskDao.merge(taskEntity);
    }

//    //testado
//    public void addExecutorToTask(Long taskId, Long executorId) {
//        TaskEntity task = taskDao.findTaskById(taskId);
//        UserEntity executor = userDao.findUserById(executorId);
//        if (task != null && executor != null) {
//            task.addExecutor(executor);
//            taskDao.merge(task);
//        }
//    }
    //testado
    public void addExecutorToTask(Long taskId, String executorEmail) {
        TaskEntity task = taskDao.findTaskById(taskId);
        UserEntity executor = userDao.findUserByEmail(executorEmail);
        if (task != null && executor != null) {
            TaskExecutorEntity taskExecutor = new TaskExecutorEntity();
            taskExecutor.setTask(task);
            taskExecutor.setExecutor(executor);
            taskExecutor.setActive(true);
            task.getExecutors().add(taskExecutor);
            taskDao.merge(task);
        }
    }

//    //testado
//    public void removeExecutorFromTask(Long taskId, Long executorId) {
//        TaskEntity task = taskDao.findTaskById(taskId);
//        UserEntity executor = userDao.findUserById(executorId);
//        if (task != null && executor != null) {
//            task.removeExecutor(executor);
//            taskDao.merge(task);
//        }
//    }
    //testado
    public void removeExecutorFromTask(Long taskId, String executorEmail) {
        TaskEntity task = taskDao.findTaskById(taskId);
        UserEntity executor = userDao.findUserByEmail(executorEmail);
        if (task != null && executor != null) {
            TaskExecutorEntity taskExecutor = task.getExecutors().stream()
                    .filter(executorEntity -> executorEntity.getExecutor().equals(executor) && executorEntity.isActive())
                    .findFirst()
                    .orElse(null);
            if (taskExecutor != null) {
                taskExecutor.setActive(false);
                taskDao.merge(task);
            }
        }
    }

    //testado
    public void addAdditionalExecutorToTask(Long taskId, String additionalExecutorName) {
        TaskEntity task = taskDao.findTaskById(taskId);
        if (task != null) {
            TaskExecutorAdditionalEntity additionalExecutor = new TaskExecutorAdditionalEntity();
            additionalExecutor.setName(additionalExecutorName);
            additionalExecutor.setTask(task);
            additionalExecutor.setActive(true);
            task.getAdditionalExecutors().add(additionalExecutor);
            taskDao.merge(task);
        }
    }

    //testado
    public void removeAdditionalExecutorFromTask(Long taskId, String additionalExecutorName) {
        TaskEntity task = taskDao.findTaskById(taskId);
        if (task != null) {
            TaskExecutorAdditionalEntity additionalExecutor = task.getAdditionalExecutors().stream()
                    .filter(executor -> executor.getName().equals(additionalExecutorName) && executor.isActive())
                    .findFirst()
                    .orElse(null);
            if (additionalExecutor != null) {
                additionalExecutor.setActive(false);
                taskDao.merge(task);
            }
        }
    }


}

