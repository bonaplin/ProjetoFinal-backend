package aor.project.innovationlab.bean;

import aor.project.innovationlab.dao.*;
import aor.project.innovationlab.dto.response.IdNameDto;
import aor.project.innovationlab.dto.task.*;
import aor.project.innovationlab.entity.*;
import aor.project.innovationlab.enums.LogType;
import aor.project.innovationlab.enums.TaskStatus;
import aor.project.innovationlab.enums.UserType;
import aor.project.innovationlab.utils.Color;
import aor.project.innovationlab.utils.logs.LoggerUtil;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Duration;
import java.time.LocalDate;
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

    @Inject
    LogBean logBean;

    @EJB
    ProjectUserDao projectUserDao;

    @EJB
    SessionDao sessionDao;


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
        createTaskIfNotExists(6,"Task 6", "Description 6", "admin@admin", TaskStatus.IN_PROGRESS, "2024-04-10", "PT24H",null, "Project1");
        createTaskIfNotExists(7,"Task 7", "Description 7", "admin@admin", TaskStatus.PLANNED, "2024-04-10", "PT24H", null, "Project1");
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
            taskEntity.setDuration(Duration.ofDays(1));
            taskEntity.setCreator(userDao.findUserByEmail(responsible));
            taskEntity.setSystemTitle(taskSystemNameGenerator(name));
            taskEntity.setActive(true);
            taskEntity.setProject(projectDao.findProjectByName(project));
            taskEntity.setFinalDate(taskEntity.getInitialDate().plusDays(taskEntity.getDuration().toDays()));

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

    public IdNameDto idNameDto(TaskEntity te){
        IdNameDto dto = new IdNameDto();
        dto.setId(te.getId());
        dto.setName(te.getTitle());
        return dto;
    }

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

    private TaskGanttDto toGanttDto(TaskEntity taskEntity) {

        TaskGanttDto taskGanttDto = new TaskGanttDto();
        taskGanttDto.setId(taskEntity.getId());
        taskGanttDto.setTitle(taskEntity.getTitle());
        taskGanttDto.setSystemTitle(taskEntity.getSystemTitle());
        taskGanttDto.setDescription(taskEntity.getDescription());
        taskGanttDto.setStatus(taskEntity.getStatus().toString());
        taskGanttDto.setInitialDate(taskEntity.getInitialDate());
        taskGanttDto.setFinalDate(taskEntity.getInitialDate().plusDays(taskEntity.getDuration().toDays()));

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
        List<MemberDto> membersOfTask = new ArrayList<>();
        taskEntity.getExecutors().forEach(executor -> {
            MemberDto member = new MemberDto();
            member.setId(executor.getExecutor().getId()); // Ajuste conforme necessário
            member.setName(executor.getExecutor().getEmail()); // Ajuste conforme necessário
            member.setSystemUsername(executor.getExecutor().getEmail()); // Ajuste conforme necessário
            member.setType("MEMBER"); // Defina o tipo conforme necessário
            membersOfTask.add(member);
        });

        UserEntity responsible = taskEntity.getResponsible();
        MemberDto creator = new MemberDto();
        creator.setId(responsible.getId()); // Ajuste conforme necessário
        creator.setName(responsible.getEmail()); // Ajuste conforme necessário
        creator.setSystemUsername(responsible.getEmail()); // Ajuste conforme necessário
        creator.setType("CREATOR"); // Defina o tipo conforme necessário
        membersOfTask.add(creator);

        // Repita para additionalExecutors se necessário
        taskGanttDto.setMembersOfTask(membersOfTask);

        List<MemberDto> additionalMembersOfTask = new ArrayList<>();
        taskEntity.getAdditionalExecutors().forEach(additionalExecutor -> {
            MemberDto additionalMember = new MemberDto();
            additionalMember.setId(additionalExecutor.getId()); // Ajuste conforme necessário
            additionalMember.setName(additionalExecutor.getName()); // Ajuste conforme necessário
            additionalMember.setSystemUsername(additionalExecutor.getName()); // Ajuste conforme necessário
            additionalMember.setType("ADDITIONAL"); // Defina o tipo conforme necessário
            additionalMembersOfTask.add(additionalMember);
        });

        taskGanttDto.setAdditionalMembersOfTask(additionalMembersOfTask);

        // Converter prerequisites para DependentTaskDto
        List<DependentTaskDto> dependentTasks = new ArrayList<>();
        taskEntity.getPrerequisites().forEach(prerequisite -> {
            DependentTaskDto dependentTask = new DependentTaskDto();
            dependentTask.setId(prerequisite.getPrerequisite().getId());
            dependentTask.setTitle(prerequisite.getPrerequisite().getTitle());
            dependentTask.setSystemTitle(prerequisite.getPrerequisite().getSystemTitle());
            dependentTask.setDescription(prerequisite.getPrerequisite().getDescription());
            dependentTask.setStatus(prerequisite.getPrerequisite().getStatus().toString());
            dependentTask.setInitialDate(prerequisite.getPrerequisite().getInitialDate());
            dependentTask.setFinalDate(prerequisite.getPrerequisite().getInitialDate().plusDays(prerequisite.getPrerequisite().getDuration().toDays()));

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

        long originalDuration = dto.getInitialDate().until(dto.getFinalDate(), ChronoUnit.DAYS);
        System.out.println("Original duration: " + originalDuration);

        // Ensure the task has at least 1 day duration
        if (dto.getInitialDate().isEqual(dto.getFinalDate())) {
            dto.setFinalDate(dto.getInitialDate().plusDays(1));
        }

        // Validação de datas
        if (dto.getFinalDate().isBefore(dto.getInitialDate())) {
            throw new IllegalArgumentException("Final date must be after initial date.");
        }

        System.out.println("Initial date: " + dto.getInitialDate());
        System.out.println("Final date: " + dto.getFinalDate());

        // Ajuste da data inicial baseado nas tarefas das quais é dependente
        LocalDate latestPrerequisiteFinalDate = task.getPrerequisites().stream()
                .map(TaskPrerequisiteEntity::getPrerequisite)
                .map(prerequisite -> prerequisite.getInitialDate().plusDays(prerequisite.getDuration().toDays()))
                .max(LocalDate::compareTo)
                .orElse(dto.getInitialDate());


        if (dto.getInitialDate().isBefore(latestPrerequisiteFinalDate) || dto.getInitialDate().isEqual(latestPrerequisiteFinalDate)) {
            dto.setInitialDate(latestPrerequisiteFinalDate);
        }

        if(originalDuration > 0) {
            task.setDuration(Duration.ofDays(originalDuration));
        }

        // Atualiza as propriedades da tarefa
        task.setInitialDate(dto.getInitialDate());
        task.setFinalDate(dto.getInitialDate().plusDays(task.getDuration().toDays()));

        // Persiste as mudanças
        taskDao.merge(task);

        //TESTE - adicionar log de alteração de data de tarefa
        logBean.addNewTaskchange(projectId, user.getId(), taskId);

        // Atualizar datas das tarefas dependentes usando HashSet para evitar ciclos infinitos
        updateDependentTasksDate(task, new HashSet<>());
        // Verificar se já existe uma tarefa com status PRESENTATION no projeto
        // Atualizar a tarefa de apresentação para ser a última
        updatePresentationTask(projectId);
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

        LocalDate taskFinalDate = task.getInitialDate().plusDays(task.getDuration().toDays());
        Set<TaskEntity> dependentTasks = getDependentTasks(task.getId());

        for (TaskEntity dependentTask : dependentTasks) {
            Duration originalDuration = dependentTask.getDuration();

            // Adjust only the initial date if necessary
            if (dependentTask.getInitialDate().isBefore(taskFinalDate)) {
                dependentTask.setInitialDate(taskFinalDate);
                dependentTask.setDuration(originalDuration);
                taskDao.merge(dependentTask);
                logBean.addNewTaskchange(dependentTask.getProject().getId(), dependentTask.getResponsible().getId(), dependentTask.getId());
            }

            // Recursive call with visit tracking for dependent tasks
            updateDependentTasksDate(dependentTask, visitedTaskIds);
        }
    }

    /**
     * Get dependent tasks of a task
     * @param taskId - id of the task
     * @return - set of dependent tasks
     */
    private Set<TaskEntity> getDependentTasks(Long taskId) {
        TaskEntity taskEntity = taskDao.findTaskById(taskId);
        if (taskEntity == null) {
            return Collections.emptySet();
        }
        return taskEntity.getPrerequisiteForTasks().stream()
                .map(TaskPrerequisiteEntity::getTask)
                .collect(Collectors.toSet());
    }

    /**
     * Update the presentation task of a project to be the last task
     * @param projectId - id of the project
     */
    public void updatePresentationTask(Long projectId) {
        // Procura a tarefa de apresentação pelo projeto e status
        TaskEntity presentationTask = taskDao.findTaskByProjectIdAndStatus(projectId, TaskStatus.PRESENTATION);
        if (presentationTask != null) {
            System.out.println("Presentation task found: " + presentationTask.getId());
            // Obtém a data final mais recente entre todas as tarefas do projeto, exceto a tarefa de apresentação
            LocalDate latestFinalDate = getLatestFinalDate(projectId, presentationTask.getId());

            // Atualiza a data inicial da tarefa de apresentação para ser a data final mais recente
            presentationTask.setInitialDate(latestFinalDate);
            // Atualiza a data final da tarefa de apresentação com base na nova data inicial e na duração
            presentationTask.setFinalDate(presentationTask.getInitialDate().plusDays(presentationTask.getDuration().toDays()));
            // Salva as alterações na base de dados
            taskDao.merge(presentationTask);
            // Adiciona um log indicando a mudança da tarefa
            logBean.addNewTaskchange(projectId, presentationTask.getResponsible().getId(), presentationTask.getId());
        }
    }

    /**
     * Get the latest final date among all tasks of a project, excluding a specific task
     * @param projectId - id of the project
     * @param excludeTaskId - id of the task to exclude
     * @return - latest final date
     */
    private LocalDate getLatestFinalDate(Long projectId, Long excludeTaskId) {
        List<TaskEntity> tasks = taskDao.findTasksByProjectId(projectId);
        LocalDate latestFinalDate = LocalDate.MIN; // Inicializa com a data mínima

        for (TaskEntity task : tasks) {
            if (!task.getId().equals(excludeTaskId)) { // Exclui a tarefa especificada
                // Calcula a data final da tarefa atual
                LocalDate taskFinalDate = task.getInitialDate().plusDays(task.getDuration().toDays());
                if (taskFinalDate.isAfter(latestFinalDate)) {
                    // Atualiza a data final mais recente, se necessário
                    latestFinalDate = taskFinalDate;
                }
                // Obtém a data final mais recente das tarefas dependentes
                LocalDate dependentFinalDate = getLatestDependentFinalDate(task, new HashSet<>());
                if (dependentFinalDate.isAfter(latestFinalDate)) {
                    // Atualiza a data final mais recente, se necessário
                    latestFinalDate = dependentFinalDate;
                }
            }
        }

        return latestFinalDate;
    }

    /**
     * Get the latest final date among all dependent tasks of a specific task
     * @param task - task to analyze
     * @param visitedTaskIds - set of visited task ids to prevent infinite recursion
     * @return - latest final date
     */
    private LocalDate getLatestDependentFinalDate(TaskEntity task, Set<Long> visitedTaskIds) {
        if (visitedTaskIds.contains(task.getId())) {
            // Evita ciclos infinitos retornando a data mínima se a tarefa já foi visitada
            return LocalDate.MIN;
        }
        // Marca a tarefa como visitada
        visitedTaskIds.add(task.getId());
        // Inicializa com a data final da tarefa atual
        LocalDate latestFinalDate = task.getInitialDate().plusDays(task.getDuration().toDays());
        // Obtém as tarefas dependentes
        Set<TaskEntity> dependentTasks = getDependentTasks(task.getId());

        for (TaskEntity dependentTask : dependentTasks) {
            // Calcula a data final da tarefa dependente
            LocalDate dependentTaskFinalDate = dependentTask.getInitialDate().plusDays(dependentTask.getDuration().toDays());
            if (dependentTaskFinalDate.isAfter(latestFinalDate)) {
                // Atualiza a data final mais recente, se necessário
                latestFinalDate = dependentTaskFinalDate;
            }
            // Chamada recursiva para obter a data final mais recente das tarefas dependentes aninhadas
            LocalDate nestedFinalDate = getLatestDependentFinalDate(dependentTask, visitedTaskIds);
            if (nestedFinalDate.isAfter(latestFinalDate)) {
                // Atualiza a data final mais recente, se necessário
                latestFinalDate = nestedFinalDate;
            }
        }

        return latestFinalDate;
    }
}

