package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.dto.response.IdNameDto;
import aor.project.innovationlab.dto.response.LabelValueDto;
import aor.project.innovationlab.dto.task.*;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.NotificationType;
import aor.project.innovationlab.enums.ProjectStatus;
import aor.project.innovationlab.enums.TaskStatus;
import aor.project.innovationlab.enums.UserType;
import aor.project.innovationlab.utils.Color;
import aor.project.innovationlab.utils.logs.LoggerUtil;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;
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

    @EJB
    TaskExecutorAdditionalDao taskExecutorAdditionalDao;

    @EJB
    ExecutorDao executorDao;

    @EJB
    TaskPrerequisiteDao taskPrerequisiteDao;

    @Inject
    LogBean logBean;

    @EJB
    ProjectUserDao projectUserDao;

    @EJB
    SessionDao sessionDao;

    @Inject
    UserBean userBean;

    @Inject
    NotificationBean notificationBean;


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
            additionalExecutor.setExecutor(executorDao.findExecutorByName(additionalExecutorName));
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
                .map(executorEntity -> executorEntity.getExecutor().getName())
                .collect(Collectors.toSet());
        taskDto.setAdditionalExecutors(additionalExecutors);

        taskDto.setStatus(taskEntity.getStatus());

        Set<String> executors = taskEntity.getExecutors().stream()
                .filter(TaskExecutorEntity::isActive)
                .map(executorEntity -> executorEntity.getExecutor().getEmail())
                .collect(Collectors.toSet());
        taskDto.setExecutors(executors);

        Set<Long> prerequisiteIds = taskEntity.getPrerequisites().stream()
                .filter(TaskPrerequisiteEntity::isActive)
                .map(prerequisiteEntity -> prerequisiteEntity.getPrerequisite().getId())
                .collect(Collectors.toSet());
        taskDto.setPrerequisiteIds(prerequisiteIds);

        return taskDto;
    }

    public TaskEntity getTaskById(long taskId) {
        return taskDao.findTaskById(taskId);
    }

    /**
     * Cria tarefas iniciais.
     */
    public void createInitialData() {
        System.out.println(Color.CYAN + "Creating initial data for tasks" + Color.CYAN);
        createTaskIfNotExists(5,"Task 5", "Description 5", "admin@admin", TaskStatus.fromValue(50), "2024-04-10", "PT24H",null, "Project1");
        createTaskIfNotExists(6,"Task 6", "Description 6", "ricardo@ricardo", TaskStatus.IN_PROGRESS, "2024-04-10", "PT24H",null, "Project1");
        createTaskIfNotExists(7,"Task 7", "Description 7", "joao@joao", TaskStatus.PLANNED, "2024-04-10", "PT24H", null, "Project1");
        addPrerequisite(7, 5);
        addPrerequisite(5, 6);

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
            taskEntity.setDuration(Period.ofDays(1));
            taskEntity.setCreator(userDao.findUserByEmail(responsible));
            taskEntity.setSystemTitle(taskSystemNameGenerator(name));
            taskEntity.setActive(true);
            taskEntity.setProject(projectDao.findProjectByName(project));
            taskEntity.setFinalDate(taskEntity.getInitialDate().plusDays(taskEntity.getDuration().getDays()));

            if (prerequisiteIds != null) {
                for (Long prerequisiteId : prerequisiteIds) {
                    TaskEntity prerequisiteTask = taskDao.findTaskById(prerequisiteId);
                    addPrerequisite(id, prerequisiteTask.getId());
                }
            }

            taskDao.persist(taskEntity);
            if(responsible.equals("admin@admin")){
                addExecutorToTask(id,"ricardo@ricardo");
                addExecutorToTask(id,"joana@joana");
                addExecutorToTask(id,"joao@joao");
            }else if(responsible.equals("ricardo@ricardo")) {
                addExecutorToTask(id, "admin@admin");
                addExecutorToTask(id, "joana@joana");
                addExecutorToTask(id, "joao@joao");
            }else if(responsible.equals("joana@joana")) {
                addExecutorToTask(id, "admin@admin");
                addExecutorToTask(id, "ricardo@ricardo");
                addExecutorToTask(id, "joao@joao");
            }else if(responsible.equals("joao@joao")) {
                addExecutorToTask(id, "admin@admin");
                addExecutorToTask(id, "ricardo@ricardo");
                addExecutorToTask(id, "joana@joana");
            }

            addAdditionalExecutorToTask(id, "Executor 3");
            addAdditionalExecutorToTask(id, "Executor 4");
            taskDao.merge(taskEntity);

            //TESTE - adicionar log de criação de tarefa
            logBean.addNewTask(projectDao.findProjectByName(project).getId(), userDao.findUserByEmail("admin@admin").getId(), id);
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

    /**
     * Find a TaskPrerequisiteEntity by the prerequisite task.
     * @param task - task
     * @param prerequisite - prerequisite task
     * @return - TaskPrerequisiteEntity
     */
    public TaskPrerequisiteEntity findPrerequisiteTaskEntityByPrerequisite(TaskEntity task, TaskEntity prerequisite) {
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
        String log = "Attempting to add executor to task";
        TaskEntity task = taskDao.findTaskById(taskId);
        UserEntity executor = userDao.findUserByEmail(executorEmail);
        if(task == null || executor == null) {
            LoggerUtil.logInfo(log, "Task or executor not found", executorEmail, null);
            return;
        }
        if (task != null && executor != null) {
            TaskExecutorEntity taskExecutor = new TaskExecutorEntity();
            taskExecutor.setTask(task);
            taskExecutor.setExecutor(executor);
            taskExecutor.setActive(true);
            taskExecutorDao.merge(taskExecutor);
            task.getExecutors().add(taskExecutor);
            taskDao.merge(task);
        }
        LoggerUtil.logInfo(log, "Executor added to task: "+executorEmail, null, null);
        notificationBean.sendNotification("admin@admin", executorEmail, "You have been added as an executor to the task: "+task.getTitle(), NotificationType.TASK_EXECUTOR_CHANGED, task.getProject().getId());
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
        ExecutorEntity additionalExecutor = executorDao.findExecutorByName(additionalExecutorName);
        if(additionalExecutor == null) {
            additionalExecutor = new ExecutorEntity();
            additionalExecutor.setName(additionalExecutorName);
            executorDao.persist(additionalExecutor);
        }
        if (task != null) {
            TaskExecutorAdditionalEntity additionalExecutorEntity = new TaskExecutorAdditionalEntity();
            additionalExecutorEntity.setExecutor(additionalExecutor);
            additionalExecutorEntity.setTask(task);
            additionalExecutorEntity.setActive(true);
            task.getAdditionalExecutors().add(additionalExecutorEntity);
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
                    .filter(executor -> executor.getExecutor().getName().equals(additionalExecutorName) && executor.isActive())
                    .findFirst()
                    .orElse(null);
            if (additionalExecutor != null) {
                additionalExecutor.setActive(false);
                taskDao.merge(task);
            }
        }
    }

    /**
     * Convert TaskEntity to IdNameDto, basic info like id and name
     * @param te - task entity
     * @return - id name dto
     */
    public IdNameDto idNameDto(TaskEntity te){
        IdNameDto dto = new IdNameDto();
        dto.setId(te.getId());
        dto.setName(te.getTitle());
        return dto;
    }

    /**
     * Get tasks of a project, just basic info like id and name, to use in dropdown task dependency
     * @param token - user token
     * @param id - project id
     * @param dtoType - type of dto
     * @return - list of tasks
     */
    public List<Object> getProjectTasks(String token, Long id, String dtoType) {
        String log= "Attempting to get project tasks for notes";
        SessionEntity se = sessionDao.findSessionByToken(token);
        ProjectEntity pe = projectDao.findProjectById(id);
        if(se == null || pe == null) {
            LoggerUtil.logInfo(log, "Invalid token or project", null, token);
            throw new IllegalArgumentException("Invalid token or project");
        }
        UserEntity user = se.getUser();
        ProjectUserEntity pue = projectUserDao.findProjectUserByProjectIdAndUserId(id, user.getId());

        if(pue == null || !pue.isActive()) {
            LoggerUtil.logInfo(log, "User is not a participant in the project", user.getEmail(), token);
            throw new IllegalArgumentException("User is not a participant in the project");
        }

        List<TaskEntity> tasks = taskDao.findTasksByProjectId(id);

        if(tasks == null || tasks.isEmpty()) {
            LoggerUtil.logInfo(log, "No tasks found for project", user.getEmail(), token);
            return new ArrayList<>();
        }

        if(dtoType == null || dtoType.isEmpty()) {
            dtoType = "IdNameDto";
        }
        if(!dtoType.equalsIgnoreCase("IdNameDto") && !dtoType.equalsIgnoreCase("TaskDto")){
            throw new IllegalArgumentException("Invalid dto type");
        }

        switch (dtoType) {
            case "IdNameDto":
                return tasks.stream()
                        .map(this::idNameDto)
                        .collect(Collectors.toList());
            case "TaskDto":
                return tasks.stream()
                        .map(this::toDto)
                        .collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }
    }


    /**
     * Get tasks of a project to gantt chart
     * @param token - user token
     * @param projectId - project id
     * @param dtoType - type of dto
     * @return - list of tasks
     */
    public List<Object> getTasks(String token, Long projectId, String dtoType) {
        String log = "Attempting to get tasks";
        SessionEntity se = sessionDao.findSessionByToken(token);
        if(se == null) {
            LoggerUtil.logInfo(log, "Invalid token", null, token);
            throw new IllegalArgumentException("Invalid token");
        }
        UserEntity user = se.getUser();
        ProjectEntity project = projectDao.findProjectById(projectId);
        if(project == null) {
            LoggerUtil.logInfo(log, "Project not found", user.getEmail(), token);
            throw new IllegalArgumentException("Project not found");
        }

        ProjectUserEntity pue = projectUserDao.findProjectUserByProjectIdAndUserId(projectId, user.getId());

        if(pue == null || !pue.isActive()) {
            LoggerUtil.logInfo(log, "User is not a participant in the project", user.getEmail(), token);
            throw new IllegalArgumentException("User is not a participant in the project");
        }

        List<TaskEntity> tasks = taskDao.findTasksByProjectId(projectId);
        if(tasks == null || tasks.isEmpty()) {
            LoggerUtil.logInfo(log, "No tasks found for project", user.getEmail(), token);
            return new ArrayList<>();
        }

        if(dtoType == null || dtoType.isEmpty()) {
            dtoType = "TaskGanttDto";
        }

        if(!dtoType.equalsIgnoreCase("TaskGanttDto") && !dtoType.equalsIgnoreCase("TaskDto")){
            throw new IllegalArgumentException("Invalid dto type");
        }

        switch (dtoType) {
            case "TaskGanttDto":
                return tasks.stream()
                        .map(this::toGanttDto)
                        .collect(Collectors.toList());
            case "TaskDto":
                 return tasks.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            default:
                return new ArrayList<>();
        }
    }

    /**
     * Generate a unique system name for a task to use GanttChart
     * @param originalName - original name of the task
     * @return - unique system name
     */
    public String taskSystemNameGenerator(String originalName) {
        String systemTitle = originalName.replaceAll("\\s+", "").toLowerCase();
        int i = 0;
        String uniqueSystemTitle;

        do {
            uniqueSystemTitle = systemTitle + (i == 0 ? "" : i);
            i++;
        } while (taskDao.findTaskBySystemTitle(uniqueSystemTitle) != null);

        return uniqueSystemTitle;
    }

    /**
     * Convert TaskEntity to TaskGanttDto to GanttChart with all info needed
     * @param taskEntity - task entity
     * @return - tasks gantt dto
     */
    TaskGanttDto toGanttDto(TaskEntity taskEntity) {

        TaskGanttDto taskGanttDto = new TaskGanttDto();
        taskGanttDto.setId(taskEntity.getId());
        taskGanttDto.setTitle(taskEntity.getTitle());
        taskGanttDto.setSystemTitle(taskEntity.getSystemTitle());
        taskGanttDto.setDescription(taskEntity.getDescription());
        taskGanttDto.setStatus(taskEntity.getStatus().toString());
        taskGanttDto.setInitialDate(taskEntity.getInitialDate());
        taskGanttDto.setFinalDate(taskEntity.getInitialDate().plusDays(taskEntity.getDuration().getDays()));

        TaskGanttDto projectTask = new TaskGanttDto();
        projectTask.setId(0L);
        projectTask.setTitle(taskEntity.getProject().getName());
        projectTask.setSystemTitle(taskEntity.getProject().getSystemName());
        projectTask.setDescription(taskEntity.getDescription());
        projectTask.setStatus(taskEntity.getProject().getStatus().name());
        projectTask.setInitialDate(taskEntity.getProject().getStartDate());
        projectTask.setFinalDate(taskEntity.getProject().getEndDate());

        taskGanttDto.setProjectTask(projectTask);
        // Converter executors e additionalExecutors para MemberDto
        List<TaskExecutorEntity> executors = taskExecutorDao.findActiveTaskExecutorByTaskId(taskEntity.getId());
        List<MemberDto> membersOfTask = new ArrayList<>();
        executors.forEach(executor -> {
            MemberDto member = new MemberDto();
            member.setId(executor.getExecutor().getId()); // Ajuste conforme necessário
            member.setName(executor.getExecutor().getFirstname() + " <"+executor.getExecutor().getEmail()+">"); // Ajuste conforme necessário
            member.setSystemUsername(executor.getExecutor().getEmail()); // Ajuste conforme necessário
            member.setType("MEMBER"); // Defina o tipo conforme necessário
            membersOfTask.add(member);
        });

        UserEntity responsible = taskEntity.getResponsible();
        MemberDto responsibleDto = new MemberDto();
        responsibleDto.setId(responsible.getId()); // Ajuste conforme necessário
        responsibleDto.setName(responsible.getFirstname() + " <"+responsible.getEmail()+">");
        responsibleDto.setSystemUsername(responsible.getEmail()); // Ajuste conforme necessário
        responsibleDto.setType("RESPONSIBLE"); // Defina o tipo conforme necessário
        membersOfTask.add(responsibleDto);

//        UserEntity creator = taskEntity.getResponsible();
//        MemberDto creatorDto = new MemberDto();
//        creatorDto.setId(creator.getId()); // Ajuste conforme necessário
//        creatorDto.setName(creator.getFirstname() + " <"+creator.getEmail()+">");
//        creatorDto.setSystemUsername(creator.getEmail()); // Ajuste conforme necessário
//        creatorDto.setType("CREATOR"); // Defina o tipo conforme necessário
//        membersOfTask.add(creatorDto);

        // Repita para additionalExecutors se necessário
        taskGanttDto.setMembersOfTask(membersOfTask);
        List<TaskExecutorAdditionalEntity> additionalExecutors = taskExecutorAdditionalDao.findActiveTaskExecutorAdditionalByTaskId(taskEntity.getId());
        List<MemberDto> additionalMembersOfTask = new ArrayList<>();
        additionalExecutors.forEach(additionalExecutor -> {
            MemberDto additionalMember = new MemberDto();
            additionalMember.setId(additionalExecutor.getExecutor().getId()); // Ajuste conforme necessário
            additionalMember.setName(additionalExecutor.getExecutor().getName()); // Ajuste conforme necessário
            additionalMember.setSystemUsername(additionalExecutor.getExecutor().getName()); // Ajuste conforme necessário
            additionalMember.setType("ADDITIONAL_MEMBER"); // Defina o tipo conforme necessário
            additionalMembersOfTask.add(additionalMember);
        });

        taskGanttDto.setAdditionalMembersOfTask(additionalMembersOfTask);

        // Converter prerequisites para DependentTaskDto
        List<DependentTaskDto> dependentTasks = new ArrayList<>();
        List<TaskPrerequisiteEntity> prerequisites = taskPrerequisiteDao.findActiveTaskPrerequisiteByTaskId(taskEntity.getId());
        prerequisites.forEach(prerequisite -> {
            DependentTaskDto dependentTask = new DependentTaskDto();
            dependentTask.setId(prerequisite.getPrerequisite().getId());
            dependentTask.setTitle(prerequisite.getPrerequisite().getTitle());
            dependentTask.setSystemTitle(prerequisite.getPrerequisite().getSystemTitle());
            dependentTask.setDescription(prerequisite.getPrerequisite().getDescription());
            dependentTask.setStatus(prerequisite.getPrerequisite().getStatus().toString());
            dependentTask.setInitialDate(prerequisite.getPrerequisite().getInitialDate());
            dependentTask.setFinalDate(prerequisite.getPrerequisite().getInitialDate().plusDays(prerequisite.getPrerequisite().getDuration().getDays()));

            dependentTasks.add(dependentTask);
        });
        taskGanttDto.setDependentTasks(dependentTasks);

        return taskGanttDto;
    }

    /**
     * Update task date and dependent tasks date
     * @param token - user token
     * @param taskId - task id
     * @param dto - task date update dto (inicialDate and finalDate)
     * @return - updated task
     */
    public TaskGanttDto updateTaskDate(String token, Long taskId, TaskDateUpdateDto dto) {
        String log = "Attempting to update task date";
        SessionEntity se = sessionDao.findSessionByToken(token);
        if(se == null) {
            LoggerUtil.logInfo(log, "Invalid token", null, token);
            throw new IllegalArgumentException("Invalid token");
        }
        UserEntity user = se.getUser();
        TaskEntity task = taskDao.findTaskById(taskId);

        if(task == null) {
            LoggerUtil.logInfo(log, "Task not found", user.getEmail(), token);
            throw new IllegalArgumentException("Task not found");
        }

        if(task.getStatus() == TaskStatus.PRESENTATION) {
            LoggerUtil.logInfo(log, "Task is a presentation task", user.getEmail(), token);
            throw new IllegalArgumentException("Task is a presentation task");
        }

        long projectId = task.getProject().getId();

        ProjectUserEntity pue = projectUserDao.findProjectUserByProjectIdAndUserId(projectId, user.getId());

        if(pue == null || !pue.isActive()) {
            LoggerUtil.logInfo(log, "User is not a participant in the project", user.getEmail(), token);
            throw new IllegalArgumentException("User is not a participant in the project");
        }

        UserType userType = pue.getRole();

        if(userType != UserType.NORMAL && userType != UserType.MANAGER) {
            LoggerUtil.logInfo(log, "User does not have permission to update task date", user.getEmail(), token);
            throw new IllegalArgumentException("User does not have permission to update task date");
        }

        if(dto == null || dto.getInitialDate() == null || dto.getFinalDate() == null) {
            LoggerUtil.logInfo(log, "Invalid date", user.getEmail(), token);
            throw new IllegalArgumentException("Invalid date");
        }

        // Ensure the task has at least 1 day duration
        if (dto.getInitialDate().isEqual(dto.getFinalDate())) {
            dto.setFinalDate(dto.getInitialDate().plusDays(1));
        }

        // Validate dates
        if (dto.getFinalDate().isBefore(dto.getInitialDate())) {
            throw new IllegalArgumentException("Final date must be after initial date.");
        }

        // Calculate original duration
        int originalDuration = (int) ChronoUnit.DAYS.between(dto.getInitialDate(), dto.getFinalDate());

        // Adjust initial date based on prerequisite tasks
        LocalDate latestPrerequisiteFinalDate = task.getPrerequisites().stream()
                .filter(TaskPrerequisiteEntity::isActive)
                .map(TaskPrerequisiteEntity::getPrerequisite)
                .map(TaskEntity::getFinalDate)
                .max(LocalDate::compareTo)
                .orElse(dto.getInitialDate());

        if (!dto.getInitialDate().isAfter(latestPrerequisiteFinalDate)) {
            dto.setInitialDate(latestPrerequisiteFinalDate);
        }

        // Update the task's dates
        task.setInitialDate(dto.getInitialDate());
        task.setFinalDate(dto.getInitialDate().plusDays(originalDuration));
        task.setDuration(Period.ofDays(originalDuration));

        // Persist the changes
        taskDao.merge(task);

        // Log the task date change

        // Update dependent tasks dates
        updateDependentTasksDate(task, new HashSet<>());

        // Update the presentation task
        updatePresentationTask(projectId);
        logBean.addNewTaskchange(projectId, user.getId(), taskId);

        return toGanttDto(task);
    }

    /**
     * Update task date and dependent tasks date
     * @param task - task to update
     */
    private void updateDependentTasksDate(TaskEntity task, Set<Long> visitedTaskIds) {
        if (visitedTaskIds.contains(task.getId())) {
            // Cycle detected, abort further processing to prevent infinite recursion
            return;
        }
        visitedTaskIds.add(task.getId());

        LocalDate taskFinalDate = task.getInitialDate().plusDays(task.getDuration().getDays());
        Set<TaskEntity> dependentTasks = getDependentTasks(task.getId());

        for (TaskEntity dependentTask : dependentTasks) {
            boolean hasActiveDependency = dependentTask.getPrerequisites().stream()
                    .anyMatch(prerequisite -> prerequisite.isActive() && prerequisite.getPrerequisite().equals(task));

            if (hasActiveDependency) {
                Period originalDuration = dependentTask.getDuration();

                // Adjust only the initial date if necessary
                if (dependentTask.getInitialDate().isBefore(taskFinalDate.plusDays(1))) {
                    dependentTask.setInitialDate(taskFinalDate);
                    dependentTask.setFinalDate(dependentTask.getInitialDate().plusDays(originalDuration.getDays()));
                    taskDao.merge(dependentTask);
//                    logBean.addNewTaskchange(dependentTask.getProject().getId(), dependentTask.getResponsible().getId(), dependentTask.getId());
                }

                // Recursive call with visit tracking for dependent tasks
                updateDependentTasksDate(dependentTask, visitedTaskIds);
            }
        }
    }

    /**
     * Get dependent tasks of a task
     * @param taskId - id of the task
     * @return - set of dependent tasks
     */
    public Set<TaskEntity> getDependentTasks(Long taskId) {
        TaskEntity taskEntity = taskDao.findTaskById(taskId);
        if (taskEntity == null) {
            return Collections.emptySet();
        }
        return taskEntity.getPrerequisiteForTasks().stream()
                .map(TaskPrerequisiteEntity::getTask)
                .collect(Collectors.toSet());
    }

    /**
     * Update the presentation task of a project to be the last task, updating project end Date
     * @param projectId - id of the project
     */
    void updatePresentationTask(Long projectId) {
        // Procura a tarefa de apresentação pelo projeto e status
        TaskEntity presentationTask = taskDao.findTaskByProjectIdAndStatus(projectId, TaskStatus.PRESENTATION);
        if (presentationTask != null) {
            System.out.println("Presentation task found: " + presentationTask.getId());
            // Obtém a data final mais recente entre todas as tarefas do projeto, exceto a tarefa de apresentação
            LocalDate latestFinalDate = getLatestFinalDate(projectId, presentationTask.getId());

            // Atualiza a data inicial e final da tarefa de apresentação
            presentationTask.setInitialDate(latestFinalDate);
            presentationTask.setFinalDate(latestFinalDate.plusDays(presentationTask.getDuration().getDays()));
            // Salva as alterações na base de dados
            taskDao.merge(presentationTask);
            // Adiciona um log indicando a mudança da tarefa
            ProjectEntity project = projectDao.findProjectById(projectId);
            project.setEndDate(presentationTask.getFinalDate());
            projectDao.merge(project);
//            logBean.addNewTaskchange(projectId, presentationTask.getResponsible().getId(), presentationTask.getId());
        }
    }

    /**
     * Get the latest final date among all tasks of a project, excluding a specific task
     * @param projectId - id of the project
     * @param excludeTaskId - id of the task to exclude
     * @return - latest final date
     */
    LocalDate getLatestFinalDate(Long projectId, Long excludeTaskId) {
        List<TaskEntity> tasks = taskDao.findTasksByProjectId(projectId);
        LocalDate latestFinalDate = LocalDate.MIN; // Inicializa com a data mínima

        for (TaskEntity task : tasks) {
            if (!task.getId().equals(excludeTaskId)) { // Exclui a tarefa especificada
                // Atualiza a data final mais recente, se necessário
                latestFinalDate = updateLatestFinalDate(task, latestFinalDate, new HashSet<>());
            }
        }

        return latestFinalDate;
    }

    /**
     * Update the latest final date among all dependent tasks of a specific task
     * @param task - task to analyze
     * @param latestFinalDate - current latest final date
     * @param visitedTaskIds - set of visited task ids to prevent infinite recursion
     * @return - updated latest final date
     */
    LocalDate updateLatestFinalDate(TaskEntity task, LocalDate latestFinalDate, Set<Long> visitedTaskIds) {
        String log = "Attempting to update latest final date";
        if (visitedTaskIds.contains(task.getId())) {
            // Evita ciclos infinitos retornando a data atual
            return latestFinalDate;
        }
        // Marca a tarefa como visitada
        visitedTaskIds.add(task.getId());
        // Calcula a data final da tarefa atual
        LocalDate taskFinalDate = task.getInitialDate().plusDays(task.getDuration().getDays());
        if (taskFinalDate.isAfter(latestFinalDate)) {
            // Atualiza a data final mais recente, se necessário
            latestFinalDate = taskFinalDate;
        }
        // Obtém as tarefas dependentes
        Set<TaskEntity> dependentTasks = getDependentTasks(task.getId());

        for (TaskEntity dependentTask : dependentTasks) {
            // Chamada recursiva para obter a data final mais recente das tarefas dependentes
            latestFinalDate = updateLatestFinalDate(dependentTask, latestFinalDate, visitedTaskIds);
        }
//        logBean.addNewTaskchange(task.getProject().getId(), task.getResponsible().getId(), task.getId());

        return latestFinalDate;
    }

    /**
     * Add users to a task as executors
     * @param task - tarefa
     * @param additionalExecutorsNames - lista de nomes dos executores adicionais
     */
    void addAdditionalExecutorsToTask(TaskEntity task, List<String> additionalExecutorsNames, String token) {
        String log = "Attempting to add additional executors to task";
        if (additionalExecutorsNames != null) {
            for (String additionalExecutorName : additionalExecutorsNames) {
                ExecutorEntity additionalExecutor = executorDao.findExecutorByName(additionalExecutorName);
                if (additionalExecutor == null) {
                    additionalExecutor = new ExecutorEntity();
                    additionalExecutor.setName(additionalExecutorName);
                    executorDao.persist(additionalExecutor);
                    LoggerUtil.logInfo(log, "Executor not found, then will created: "+additionalExecutor.getName(), null, token);
                } else if (!additionalExecutor.isActive()) {
                    additionalExecutor.setActive(true);
                    executorDao.merge(additionalExecutor);
                    LoggerUtil.logInfo(log, "Executor is inactive, then will activated: "+additionalExecutor.getName(), null, token);
                }

                //verifica se a task já tem associado o executor adicional
                TaskExecutorAdditionalEntity teae = taskExecutorAdditionalDao.findTaskExecutorAdditionalByTaskIdAndExecutorNAme(task.getId(), additionalExecutor.getName());
                if(teae == null) {
                    TaskExecutorAdditionalEntity additionalExecutorEntity = new TaskExecutorAdditionalEntity();
                    additionalExecutorEntity.setExecutor(additionalExecutor);
                    additionalExecutorEntity.setTask(task);
                    additionalExecutorEntity.setActive(true);
                    task.getAdditionalExecutors().add(additionalExecutorEntity);
                    taskDao.merge(task);
                    LoggerUtil.logInfo(log, "Executor added to task: "+additionalExecutor.getName(), null, token);
                }else if(!teae.isActive()){
                    teae.setActive(true);
                    taskDao.merge(task);
                    LoggerUtil.logInfo(log, "Executor reactivated to task: "+additionalExecutor.getName(), null, token);
                }
            }
        }
    }

    /**
     * Update additional executors for a task. Create them if they do not exist in the database.
     * @param task - tarefa
     * @param additionalExecutorsNames - lista de nomes dos executores adicionais
     */
    private void updateAdditionalExecutorsForTask(TaskEntity task, List<String> additionalExecutorsNames) {
        // Map to keep track of current associations
        Map<String, TaskExecutorAdditionalEntity> currentAssociations = new HashMap<>();

        // Load current associations from the database
        List<TaskExecutorAdditionalEntity> existingAssociations = taskExecutorAdditionalDao.findTaskExecutorAdditionalByTaskId(task.getId());
        for (TaskExecutorAdditionalEntity association : existingAssociations) {
            currentAssociations.put(association.getExecutor().getName(), association);
        }

        // Process the incoming list of additional executors
        if (additionalExecutorsNames != null) {
            for (String additionalExecutorName : additionalExecutorsNames) {
                ExecutorEntity additionalExecutor = executorDao.findExecutorByName(additionalExecutorName);
                if (additionalExecutor == null) {
                    additionalExecutor = new ExecutorEntity();
                    additionalExecutor.setName(additionalExecutorName);
                    executorDao.persist(additionalExecutor);
                } else if (!additionalExecutor.isActive()) {
                    additionalExecutor.setActive(true);
                    executorDao.merge(additionalExecutor);
                }

                if (currentAssociations.containsKey(additionalExecutorName)) {
                    // Reactivate existing association if necessary
                    TaskExecutorAdditionalEntity teae = currentAssociations.get(additionalExecutorName);
                    if (!teae.isActive()) {
                        teae.setActive(true);
                        taskDao.merge(task);
                    }
                    // Remove processed executor from the map
                    currentAssociations.remove(additionalExecutorName);
                } else {
                    // Create new association
                    TaskExecutorAdditionalEntity additionalExecutorEntity = new TaskExecutorAdditionalEntity();
                    additionalExecutorEntity.setExecutor(additionalExecutor);
                    additionalExecutorEntity.setTask(task);
                    additionalExecutorEntity.setActive(true);
                    task.getAdditionalExecutors().add(additionalExecutorEntity);
                    taskDao.merge(task);
                }
            }
        }

        // Deactivate associations that are no longer in the incoming list
        for (TaskExecutorAdditionalEntity teae : currentAssociations.values()) {
            if (teae.isActive()) {
                teae.setActive(false);
                taskDao.merge(task);
            }
        }
    }

    /**
     * Update users for a task. Create them if they do not exist in the database.
     * @param task
     * @param userIds
     * @param projectId
     */
    private void addUsersToTask(TaskEntity task, List<Long> userIds, Long projectId) {
        String log = "Attempting to add users to task";
        ProjectEntity pe = projectDao.findProjectById(projectId);
        if(pe == null) {
            LoggerUtil.logInfo(log, "Project not found", null, null);
            throw new IllegalArgumentException("Project not found");
        }
        if (userIds != null) {
            for (Long userId : userIds) {
                UserEntity user = userDao.findUserById(userId);
                if (user != null) {
                    TaskExecutorEntity taskExecutor = taskExecutorDao.findTaskExecutorByTaskIdAndExecutorId(task.getId(), user.getId());
                    if (taskExecutor == null) {
                        taskExecutor = new TaskExecutorEntity();
                        taskExecutor.setTask(task);
                        taskExecutor.setExecutor(user);
                        taskExecutor.setActive(true);
                        taskExecutorDao.merge(taskExecutor);
                    } else if (!taskExecutor.isActive()) {
                        taskExecutor.setActive(true);
                        taskExecutorDao.merge(taskExecutor);
                    }
                }
            }
        }
    }

    /**
     * Update users for a task. Create them if they do not exist in the database.
     * @param task
     * @param userIds
     * @param projectId
     */
    private void updateUsersForTask(TaskEntity task, List<Long> userIds, Long projectId) {
        String log = "Attempting to update users for task";
        ProjectEntity pe = projectDao.findProjectById(projectId);
        if (pe == null) {
            LoggerUtil.logInfo(log, "Project not found", null, null);
            throw new IllegalArgumentException("Project not found");
        }

        // Map to keep track of current associations
        Map<Long, TaskExecutorEntity> currentAssociations = new HashMap<>();
        List<TaskExecutorEntity> existingAssociations = taskExecutorDao.findTaskExecutorsByTaskId(task.getId());
        for (TaskExecutorEntity association : existingAssociations) {
            currentAssociations.put(association.getExecutor().getId(), association);
        }

        if (userIds != null) {
            for (Long userId : userIds) {
                UserEntity user = userDao.findUserById(userId);
                if (user != null) {
                    if (currentAssociations.containsKey(userId)) {
                        // Reactivate existing association if necessary
                        TaskExecutorEntity taskExecutor = currentAssociations.get(userId);
                        if (!taskExecutor.isActive()) {
                            taskExecutor.setActive(true);
                            taskExecutorDao.merge(taskExecutor);
                        }
                        // Remove processed user from the map
                        currentAssociations.remove(userId);
                    } else {
                        // Create new association
                        TaskExecutorEntity taskExecutor = new TaskExecutorEntity();
                        taskExecutor.setTask(task);
                        taskExecutor.setExecutor(user);
                        taskExecutor.setActive(true);
                        taskExecutorDao.merge(taskExecutor);
                    }
                }
            }
        }

        // Deactivate associations that are no longer in the incoming list
        for (TaskExecutorEntity taskExecutor : currentAssociations.values()) {
            if (taskExecutor.isActive()) {
                taskExecutor.setActive(false);
                taskExecutorDao.merge(taskExecutor);
            }
        }
    }


    /**
     * Update task dependencies with the specified data
     * @param task
     * @param prerequisiteIds
     * @param projectId
     */
    private void addTaskDependencies(TaskEntity task, List<Long> prerequisiteIds, Long projectId) {
        ProjectEntity pe = projectDao.findProjectById(projectId);
        if(pe == null) {
            throw new IllegalArgumentException("Project not found");
        }
        if (prerequisiteIds != null) {
            for (Long prerequisiteId : prerequisiteIds) {
                TaskEntity prerequisite = taskDao.findTaskById(prerequisiteId);
                if (prerequisite != null && prerequisite.getProject().getId() == projectId) {
                    boolean foundAndActivated = false;
                    for (TaskPrerequisiteEntity existingPrerequisite : task.getPrerequisites()) {
                        if (existingPrerequisite.getPrerequisite().getId().equals(prerequisiteId)) {
                            if (!existingPrerequisite.isActive()) {
                                existingPrerequisite.setActive(true);
                                taskDao.merge(task);
                            }
                            foundAndActivated = true;
                            break;
                        }
                    }
                    if (!foundAndActivated) {
                        TaskPrerequisiteEntity taskPrerequisite = new TaskPrerequisiteEntity();
                        taskPrerequisite.setTask(task);
                        taskPrerequisite.setPrerequisite(prerequisite);
                        taskPrerequisite.setActive(true);
                        task.getPrerequisites().add(taskPrerequisite);
                        prerequisite.getPrerequisiteForTasks().add(taskPrerequisite);
                        taskDao.merge(task);
                    }
                } else {
                    System.out.println("Prerequisite task does not belong to the project");
                }
            }
        }
    }

    /**
     * Update task dependencies. Create them if they do not exist in the database.
     * @param task
     * @param prerequisiteIds
     * @param projectId
     */
    private void updateTaskDependencies(TaskEntity task, List<Long> prerequisiteIds, long projectId) {
        ProjectEntity pe = projectDao.findProjectById(projectId);
        if (pe == null) {
            throw new IllegalArgumentException("Project not found");
        }

        Map<Long, TaskPrerequisiteEntity> currentAssociations = new HashMap<>();
        Set<TaskPrerequisiteEntity> existingAssociations = task.getPrerequisites();
        for (TaskPrerequisiteEntity association : existingAssociations) {
            currentAssociations.put(association.getPrerequisite().getId(), association);
        }

        Set<Long> newPrerequisiteIds = prerequisiteIds != null ? new HashSet<>(prerequisiteIds) : Collections.emptySet();

        // Remove old dependencies that are no longer in the new list
        for (TaskPrerequisiteEntity association : new HashSet<>(existingAssociations)) {
            if (!newPrerequisiteIds.contains(association.getPrerequisite().getId())) {
                association.setActive(false);
                taskPrerequisiteDao.merge(association);
                task.getPrerequisites().remove(association);
                association.getPrerequisite().getPrerequisiteForTasks().remove(association);
            }
        }

        // Add or reactivate new dependencies
        for (Long prerequisiteId : newPrerequisiteIds) {
            TaskEntity prerequisite = taskDao.findTaskById(prerequisiteId);
            if (prerequisite != null && prerequisite.getProject().getId() == projectId) {
                if (createsDependencyCycle(task, prerequisite)) {
                    System.out.println("Cannot add dependency: it creates a cycle");
                    continue; // Skip this dependency
                }

                if (currentAssociations.containsKey(prerequisiteId)) {
                    // Reactivate existing association if necessary
                    TaskPrerequisiteEntity existingPrerequisite = currentAssociations.get(prerequisiteId);
                    if (!existingPrerequisite.isActive()) {
                        existingPrerequisite.setActive(true);
                        taskPrerequisiteDao.merge(existingPrerequisite);
                    }
                    currentAssociations.remove(prerequisiteId);
                } else {
                    // Create new association
                    TaskPrerequisiteEntity taskPrerequisite = new TaskPrerequisiteEntity();
                    taskPrerequisite.setTask(task);
                    taskPrerequisite.setPrerequisite(prerequisite);
                    taskPrerequisite.setActive(true);
                    task.getPrerequisites().add(taskPrerequisite);
                    prerequisite.getPrerequisiteForTasks().add(taskPrerequisite);
                    taskPrerequisiteDao.persist(taskPrerequisite);
                }
            } else {
                System.out.println("Prerequisite task does not belong to the project");
            }
        }

        // Deactivate associations that are no longer in the incoming list
        for (TaskPrerequisiteEntity taskPrerequisite : currentAssociations.values()) {
            if (taskPrerequisite.isActive()) {
                taskPrerequisite.setActive(false);
                taskPrerequisiteDao.merge(taskPrerequisite);
            }
        }

        taskDao.merge(task);
    }



    /**
     * Método createsDependencyCycle:
     * Este método verifica se adicionar uma dependência criaria um ciclo.
     * Usa uma busca em profundidade (DFS) para verificar se a tarefa já está presente nas dependências do potentialPrerequisite.
     * Verificação de Ciclos:
     * Antes de adicionar uma nova dependência, a função updateTaskDependencies verifica se isso criaria um ciclo usando o método createsDependencyCycle.
     * Se um ciclo é detectado, a dependência é ignorada e um aviso é impresso.
     * Processamento das Dependências:
     * As dependências são processadas, reativando associações existentes, criando novas associações, e desativando aquelas que não são necessarias .
     * @param task
     * @param potentialPrerequisite
     * @return
     */
    boolean createsDependencyCycle(TaskEntity task, TaskEntity potentialPrerequisite) {
        Set<TaskEntity> visitedTasks = new HashSet<>();
        Deque<TaskEntity> stack = new ArrayDeque<>();
        stack.push(potentialPrerequisite);

        while (!stack.isEmpty()) {
            TaskEntity currentTask = stack.pop();
            if (currentTask.equals(task)) {
                return true; // Cycle detected
            }
            if (!visitedTasks.add(currentTask)) {
                continue; // Already visited, skip
            }
            // Iterate over active prerequisites to check for cycles
            for (TaskPrerequisiteEntity prerequisite : currentTask.getPrerequisites()) {
                if (prerequisite.isActive()) {
                    stack.push(prerequisite.getPrerequisite());
                }
            }
        }
        return false; // No cycle found
    }


    public TaskContributorsDto getTaskCreateInfo(String token, Long projectId) {
        SessionEntity se = sessionDao.findSessionByToken(token);
        ProjectEntity pe = projectDao.findProjectById(projectId);

        if(se == null || pe == null) {
            throw new IllegalArgumentException("Invalid token or project");
        }

        UserEntity user = se.getUser();
        ProjectUserEntity pue = projectUserDao.findProjectUserByProjectIdAndUserId(projectId, user.getId());

        if(pue == null || !pue.isActive()) {
            throw new IllegalArgumentException("User is not a participant in the project");
        }

        TaskContributorsDto dto = new TaskContributorsDto();

        List<UserEntity> users = userDao.findUsersByProjectIdActives(projectId);
        List<LabelValueDto> userList = users.stream()
                .map(u -> new LabelValueDto(u.getFirstname()+" <"+u.getEmail()+"> ",u.getId()))
                .collect(Collectors.toList());
        List<TaskEntity> tasks = taskDao.findTasksByProjectIdNoPresentation(projectId);
        List<LabelValueDto> taskList = tasks.stream()
                .map(t -> new LabelValueDto(t.getTitle()+" ("+t.getResponsible().getFirstname()+")", t.getId()))
                .collect(Collectors.toList());
        List<ExecutorEntity> executors = executorDao.findAllExecutors();
        List<LabelValueDto> executorList = executors.stream()
                .map(e -> new LabelValueDto(e.getName(), e.getId()))
                .collect(Collectors.toList());
        dto.setUsers(userList);
        dto.setDependentTasks(taskList);
        dto.setExecutors(executorList);
        System.out.println(userList.size());

        return dto;
    }

    /**
     * Create a task with the specified data and add it to the database
     * @param token - user token
     * @param dto - task creation data
     */
    public TaskGanttDto createTask(String token, TaskCreateDto dto) {
        String log = "Attempting to create task";
        SessionEntity se = sessionDao.findSessionByToken(token);
        if(se == null) {
            LoggerUtil.logInfo(log, "Invalid token", null, token);
            throw new IllegalArgumentException("Invalid token");
        }

        UserEntity user = se.getUser();
        validateTaskCreateDto(dto, user, token);

        LocalDate initialDate= dto.getInitialDate();
        LocalDate finalDate = dto.getFinalDate();
        Period duration = Period.between(initialDate, finalDate);

        LocalDate maxDependentFinalDate = findMaxDependentFinalDate(dto.getDependentTasksIds());

        if(maxDependentFinalDate != null && initialDate.isBefore(maxDependentFinalDate.plusDays(1))) {
            initialDate = maxDependentFinalDate;
            finalDate = initialDate.plus(duration);
        }


        TaskEntity task = new TaskEntity();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setInitialDate(initialDate);
        task.setDuration(duration);
        task.setFinalDate(finalDate);
        task.setResponsible(userDao.findUserById(dto.getResponsibleId()));
        task.setCreator(user);
        task.setSystemTitle(taskSystemNameGenerator(dto.getTitle()));
        task.setActive(true);
        task.setProject(projectDao.findProjectById(dto.getProjectId()));
        task.setStatus(TaskStatus.PLANNED);
        taskDao.persist(task);

        addAdditionalExecutorsToTask(task, dto.getAdditionalExecutorsNames(), token);
        addUsersToTask(task, dto.getUsersIds().stream().filter(id -> !id.equals(dto.getResponsibleId())).collect(Collectors.toList()), dto.getProjectId());
        addTaskDependencies(task, dto.getDependentTasksIds(), dto.getProjectId());

        updateDependentTasksDate(task, new HashSet<>());
        LoggerUtil.logInfo(log, "Task created successfully", user.getEmail(), token);
        logBean.addNewTask(task.getProject().getId(), user.getId(), task.getId());
        return toGanttDto(task);
    }


//    public TaskGanttDto updateTaskService(String token, Long taskId, TaskCreateDto dto) {
//        TaskGanttDto response = updateTask(token, taskId, dto);
//
//        Long userI = sessionDao.findSessionByToken(token).getUser().getId();
//        Long taskI = taskDao.findTaskById(taskId).getId();
//        Long projectI = projectDao.findProjectById(dto.getProjectId()).getId();
//        System.out.println("Task updated successfully");
//        logBean.addNewTaskchange(projectI, userI, taskI);
//        return response;
//    }

    /**
     * Update a task with the specified data
     * @param token - user token
     * @param taskId - task id
     * @param dto - task update data
     * @return - updated task
     */
    public TaskGanttDto updateTask(String token, Long taskId, TaskCreateDto dto) {
        String log = "Attempting to update task";
        SessionEntity se = sessionDao.findSessionByToken(token);
        TaskEntity task = taskDao.findTaskById(taskId);
        if (se == null || task == null) {
            LoggerUtil.logInfo(log, "Invalid token or task", null, token);
            throw new IllegalArgumentException("Invalid token or task");
        }
        validateTaskCreateDto(dto, se.getUser(), token);

        // Verify if dto.status is one of the TaskStatus
        if(dto.getStatus() != null && !TaskStatus.contains(dto.getStatus())) {
            LoggerUtil.logInfo(log, "Invalid status", se.getUser().getEmail(), token);
            throw new IllegalArgumentException("Invalid status");
        }

        TaskStatus newTaskStatus = TaskStatus.fromValue(dto.getStatus());
        TaskStatus oldTaskStatus = task.getStatus();

        // Find the maximum final date of dependent tasks
        LocalDate maxDependentFinalDate = findMaxDependentFinalDate(dto.getDependentTasksIds());

        // Adjust the initial date if necessary
        LocalDate initialDate = dto.getInitialDate();
        LocalDate finalDate = dto.getFinalDate();
        Period dtoDuration = Period.between(initialDate, finalDate);

        if (maxDependentFinalDate != null && initialDate.isBefore(maxDependentFinalDate.plusDays(1))) {
            initialDate = maxDependentFinalDate;
            finalDate = initialDate.plus(dtoDuration);
        }


        UserEntity responsible = userDao.findUserById(dto.getResponsibleId());

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setInitialDate(initialDate);
        task.setDuration(Period.between(initialDate, finalDate));
        task.setFinalDate(finalDate);
        task.setResponsible(responsible);
        task.setSystemTitle(taskSystemNameGenerator(dto.getTitle()));
        task.setStatus(TaskStatus.fromValue(dto.getStatus()));
        task.setActive(true);
        taskDao.merge(task);

        updateAdditionalExecutorsForTask(task, dto.getAdditionalExecutorsNames());
        updateUsersForTask(task, dto.getUsersIds().stream().filter(id -> !id.equals(dto.getResponsibleId())).collect(Collectors.toList()), dto.getProjectId());
        updateTaskDependencies(task, dto.getDependentTasksIds(), dto.getProjectId());

        updateDependentTasksDate(task, new HashSet<>());
        if (!oldTaskStatus.equals(newTaskStatus)) {
            logBean.addNewTaskStateChange(task.getProject().getId(), se.getUser().getId(), task.getId(), newTaskStatus,oldTaskStatus);
            System.out.println("Task status updated successfully");
        }else {
            System.out.println("task dont change status");
        }
        return toGanttDto(task);
    }

    /**
     * Find the maximum final date of dependent tasks
     * @param dependentTaskIds - list of dependent task IDs
     * @return the maximum final date, or null if there are no dependencies
     */
    LocalDate findMaxDependentFinalDate(List<Long> dependentTaskIds) {
        if (dependentTaskIds == null || dependentTaskIds.isEmpty()) {
            return null;
        }
        LocalDate maxFinalDate = null;
        for (Long taskId : dependentTaskIds) {
            TaskEntity dependentTask = taskDao.findTaskById(taskId);

            if (dependentTask != null && dependentTask.isActive()) {
                LocalDate finalDate = dependentTask.getFinalDate();
                if (maxFinalDate == null || finalDate.isAfter(maxFinalDate)) {
                    maxFinalDate = finalDate;
                }
            }
        }
        return maxFinalDate;
    }

    /**
     * Generate a system title for a task based on the title
     * @param dto - task creation data
     * @param user - user creating the task
     * @param token - user token
     */
    void validateTaskCreateDto(TaskCreateDto dto, UserEntity user, String token){
        String log= "Attempting to create/update task";
        if(dto == null || dto.getProjectId() == null || dto.getTitle() == null || dto.getInitialDate() == null || dto.getFinalDate() == null || dto.getResponsibleId() == null) {
            LoggerUtil.logInfo(log, "Invalid dto", user.getEmail(), token);
            throw new IllegalArgumentException("Invalid creation data");
        }

        if(dto.getInitialDate().isAfter(dto.getFinalDate())) {
            throw new IllegalArgumentException("Invalid dates, initial date is after final date");
        }
        if(dto.getTitle().isEmpty() || dto.getTitle().isBlank() || dto.getDescription() == null || dto.getDescription().isEmpty() || dto.getDescription().isBlank()){
            throw new IllegalArgumentException("Invalid title or description");
        }
        UserEntity responsible = userDao.findUserById(dto.getResponsibleId());
        if (responsible == null) {
            LoggerUtil.logInfo(log, "Responsible user not found", user.getEmail(), token);
            throw new IllegalArgumentException("Responsible user not found");
        }
        ProjectEntity project = projectDao.findProjectById(dto.getProjectId());
        if (project == null) {
            LoggerUtil.logInfo(log, "Project not found", user.getEmail(), token);
            throw new IllegalArgumentException("Project not found");
        }

        ProjectUserEntity pue = projectUserDao.findProjectUserByProjectIdAndUserId(dto.getProjectId(), user.getId());
        ProjectUserEntity pueR = projectUserDao.findProjectUserByProjectIdAndUserId(dto.getProjectId(), responsible.getId());
        if (pue == null || !pue.isActive() || pueR == null || !pueR.isActive()) {
            LoggerUtil.logInfo(log, "User is not a participant in the project", user.getEmail(), token);
            throw new IllegalArgumentException("User is not a participant in the project");
        }
//
//        if(Objects.equals(TaskStatus.fromValue(dto.getStatus()), TaskStatus.PRESENTATION)){
//            LoggerUtil.logInfo(log, "Task is a presentation task", user.getEmail(), token);
//            throw new IllegalArgumentException("Task is a presentation task, cannot be updated by user");
//        }
    }

    /**
     * Generate a system title for a task based on the title
     * @param token - user token
     * @param taskId - task id
     */
    public void deleteTask(String token, Long taskId) {
        String log = "Attempting to delete task";
        SessionEntity se = sessionDao.findSessionByToken(token);
        TaskEntity task = taskDao.findTaskById(taskId);

        if (se == null || task == null) {
            LoggerUtil.logInfo(log, "Invalid token or task", null, token);
            throw new IllegalArgumentException("Invalid token or task");
        }

        // Mark the task as inactive
        task.setActive(false);
        taskDao.merge(task);

        // Mark task dependencies as inactive
        markTaskDependenciesAsInactive(task);
        logBean.addNewTaskDelete(task.getProject().getId(), se.getUser().getId(), task.getId());
        LoggerUtil.logInfo(log, "Task marked as inactive successfully", se.getUser().getEmail(), token);
    }

    /**
     * Mark all dependencies of a task as inactive
     * @param task - task to mark dependencies as inactive
     */
    private void markTaskDependenciesAsInactive(TaskEntity task) {
        // Mark all TaskPrerequisiteEntity associations where this task is either a prerequisite or a dependent as inactive
        List<TaskPrerequisiteEntity> prerequisites = taskPrerequisiteDao.findByTaskId(task.getId());
        for (TaskPrerequisiteEntity prerequisite : prerequisites) {
            prerequisite.setActive(false);
            taskPrerequisiteDao.merge(prerequisite);
        }

        List<TaskPrerequisiteEntity> dependents = taskPrerequisiteDao.findByPrerequisiteTaskId(task.getId());
        for (TaskPrerequisiteEntity dependent : dependents) {
            dependent.setActive(false);
            taskPrerequisiteDao.merge(dependent);
        }
    }

}

