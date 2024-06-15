package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.ProjectDao;
import aor.project.innovationlab.dao.TaskDao;
import aor.project.innovationlab.dao.TaskExecutorDao;
import aor.project.innovationlab.dao.UserDao;
import aor.project.innovationlab.dto.task.TaskDto;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.LogType;
import aor.project.innovationlab.enums.TaskStatus;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
public class TaskBean {

    @EJB
    TaskDao taskDao;

    @EJB
    UserDao userDao;

    @EJB
    ProjectDao projectDao;

    @EJB
    TaskExecutorDao taskExecutorDao;

    @Inject
    LogBean logBean;


    /**
     * Converte um DTO numa entidade.
     * @param taskDto
     * @return
     */
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
                addPrerequisite(taskDto.getId(),prerequisiteId);
            }
        }
        return taskEntity;
    }

    /**
     * Converte uma entidade num DTO.
     * @param taskEntity
     * @return
     */
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

        return taskDto;
    }

    public TaskEntity getTaskById(long taskId) {
        return taskDao.findTaskById(taskId);
    }

    /**
     * Cria tarefas iniciais.
     */
    public void createInitialData() {
        createTaskIfNotExists(1,"Task 1", "Description 1", "admin@admin", TaskStatus.fromValue(50), "2021-01-01", "PT1H",null, "Project1");
        createTaskIfNotExists(2,"Task 2", "Description 1", "admin@admin", TaskStatus.IN_PROGRESS, "2021-01-01", "PT1H",null, "Project1");
        createTaskIfNotExists(3,"Task 3", "Description 3", "admin@admin", TaskStatus.PLANNED, "2021-01-01", "PT1H", null, "Project1");
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
                                      String duration, Set<Long> prerequisiteIds, String project) {
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
            taskEntity.setActive(true);
            taskEntity.setProject(projectDao.findProjectByName(project));

            if (prerequisiteIds != null) {
                for (Long prerequisiteId : prerequisiteIds) {
                    TaskEntity prerequisiteTask = taskDao.findTaskById(prerequisiteId);
                    addPrerequisite(id, prerequisiteTask.getId());
                }
            }

            taskDao.persist(taskEntity);
            addExecutorToTask(id, "admin@admin");
            addExecutorToTask(id, "ricardo@ricardo");
            addAdditionalExecutorToTask(id, "Executor 3");
            addAdditionalExecutorToTask(id, "Executor 4");
            taskDao.merge(taskEntity);

            //TESTE - adicionar log de criação de tarefa
            logBean.addNewTask(projectDao.findProjectByName(project).getId(), userDao.findUserByEmail("admin@admin").getId(), id, LogType.TASK_CREATE);
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

//    public Set<TaskEntity> getPrerequisiteTasksForTask(long taskId) {
//        TaskEntity taskEntity = taskDao.findTaskById(taskId);
//        if (taskEntity != null) {
//            return taskEntity.getPrerequisites();
//        }
//        return null;
//    }

    /**
     * Retorna as tarefas que têm a tarefa com taskId como pré-requisito.
     * @param taskId - id da tarefa que é pré-requisito
     * @return
     */
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

    /**
     * Converte as tarefas de pré-requisito em DTO.
     * @param taskEntity
     * @return
     */
    private Set<TaskDto> convertPrerequisiteTasksToDto(TaskEntity taskEntity) {
        Set<TaskPrerequisiteEntity> prerequisiteTaskEntities = taskEntity.getPrerequisiteForTasks();
        Set<TaskEntity> prerequisiteTasks = prerequisiteTaskEntities.stream()
                .map(TaskPrerequisiteEntity::getTask)
                .collect(Collectors.toSet());
        return prerequisiteTasks.stream()
                .map(this::toDto)
                .collect(Collectors.toSet());
    }

    /**
     * Adiciona um pré-requisito a uma tarefa.
     * @param taskId - id da tarefa
     * @param prerequisiteId - id do pré-requisito
     */
    public void addPrerequisite(long taskId, long prerequisiteId) {
        TaskEntity task = taskDao.findTaskById(taskId);
        if(task == null) {
            return;
        }
        TaskEntity prerequisite = taskDao.findTaskById(prerequisiteId);
        if(prerequisite == null) {
            return;
        }
        for (TaskPrerequisiteEntity existingPrerequisite : task.getPrerequisites()) {
            if (existingPrerequisite.getPrerequisite().equals(prerequisite)) {
                return; // O pré-requisito já está no array, então retorna imeadiatamente
            }
        }
        TaskPrerequisiteEntity taskPrerequisiteEntity = new TaskPrerequisiteEntity();
        taskPrerequisiteEntity.setTask(task);
        taskPrerequisiteEntity.setPrerequisite(prerequisite);
        taskPrerequisiteEntity.setActive(true);
        task.getPrerequisites().add(taskPrerequisiteEntity);
        prerequisite.getPrerequisiteForTasks().add(taskPrerequisiteEntity);
        taskDao.merge(task);
    }


    /**
     * Remove um pré-requisito de uma tarefa.
     * @param taskId - id da tarefa
     * @param prerequisiteId - id do pré-requisito
     */
    public void removePrerequisite(long taskId, long prerequisiteId) {
        TaskEntity task = taskDao.findTaskById(taskId);
        TaskEntity prerequisite = taskDao.findTaskById(prerequisiteId);
        if(task == null || prerequisite == null) {
            return;
        }
        TaskPrerequisiteEntity taskPrerequisiteEntity = findPrerequisiteTaskEntityByPrerequisite(task, prerequisite);
        if (taskPrerequisiteEntity != null) {
            taskPrerequisiteEntity.setActive(false);
            task.getPrerequisites().remove(taskPrerequisiteEntity);
            taskDao.merge(task);
        }
    }

    private TaskPrerequisiteEntity findPrerequisiteTaskEntityByPrerequisite(TaskEntity task, TaskEntity prerequisite) {
        for (TaskPrerequisiteEntity taskPrerequisiteEntity : task.getPrerequisites()) {
            if (taskPrerequisiteEntity.getPrerequisite().equals(prerequisite) && taskPrerequisiteEntity.isActive()) {
                return taskPrerequisiteEntity;
            }
        }
        return null;
    }

    /**
     * Adiciona um executor a uma tarefa.
     * @param taskId - id da tarefa
     * @param executorEmail - email do executor
     */
    public void addExecutorToTask(long taskId, String executorEmail) {
        TaskEntity task = taskDao.findTaskById(taskId);
        UserEntity executor = userDao.findUserByEmail(executorEmail);
        if (task != null && executor != null) {
            TaskExecutorEntity taskExecutor = new TaskExecutorEntity();
            taskExecutor.setTask(task);
            taskExecutor.setExecutor(executor);
            taskExecutor.setActive(true);
            taskExecutorDao.merge(taskExecutor);
            task.getExecutors().add(taskExecutor);
            taskDao.merge(task);
        }
    }

    /**
     * Remove um executor de uma tarefa.
     * @param taskId - id da tarefa
     * @param executorEmail - email do executor
     */
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
                taskExecutorDao.merge(taskExecutor);
                taskDao.merge(task);
            }
        }
    }

    /**
     * Adiciona um executor adicional a uma tarefa.
     * @param taskId - id da tarefa
     * @param additionalExecutorName - nome do executor adicional
     */
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

    /**
     * Remove um executor adicional de uma tarefa.
     * @param taskId - id da tarefa
     * @param additionalExecutorName - nome do executor adicional
     */
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

