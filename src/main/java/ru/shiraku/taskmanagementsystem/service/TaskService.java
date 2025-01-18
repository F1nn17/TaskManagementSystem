package ru.shiraku.taskmanagementsystem.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.shiraku.taskmanagementsystem.exceptions.AccessClosed;
import ru.shiraku.taskmanagementsystem.exceptions.InvalidCommentException;
import ru.shiraku.taskmanagementsystem.exceptions.NotFound;
import ru.shiraku.taskmanagementsystem.model.Priority;
import ru.shiraku.taskmanagementsystem.model.Role;
import ru.shiraku.taskmanagementsystem.model.Status;
import ru.shiraku.taskmanagementsystem.model.dto.CommentResponse;
import ru.shiraku.taskmanagementsystem.model.dto.CreateTaskRequest;
import ru.shiraku.taskmanagementsystem.model.dto.EditTaskRequest;
import ru.shiraku.taskmanagementsystem.model.dto.TaskResponse;
import ru.shiraku.taskmanagementsystem.model.entity.CommentEntity;
import ru.shiraku.taskmanagementsystem.model.entity.TaskEntity;
import ru.shiraku.taskmanagementsystem.model.entity.UserEntity;
import ru.shiraku.taskmanagementsystem.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }


    @Transactional
    public TaskEntity createTask(String authorEmail, CreateTaskRequest request) {
        UserEntity author = userService.findByEmail(authorEmail);
        UserEntity executor = userService.findByEmail(request.getExecutorEmail());

        TaskEntity task = new TaskEntity();
        task.setTaskTitle(request.getTaskTitle());
        task.setTaskDescription(request.getTaskDescription());
        task.setPriority(request.getPriority());
        task.setTaskStatus(Status.TODO);
        task.setAuthor(author.getName());
        task.setAuthorEmail(author.getEmail());
        task.setExecutor(executor.getName());
        task.setExecutorEmail(executor.getEmail());

        return taskRepository.save(task);
    }

    @Transactional
    public TaskResponse editTask(Long taskId, EditTaskRequest request){
        Optional<TaskEntity> taskOpt = taskRepository.findById(taskId);
        if(taskOpt.isEmpty()) {
            throw new NotFound("Task not found");
        }
        TaskEntity task = taskOpt.get();
        if (request.getTaskTitle() != null) {
            task.setTaskTitle(request.getTaskTitle());
        }
        if (request.getTaskDescription() != null) {
            task.setTaskDescription(request.getTaskDescription());
        }
        taskRepository.save(task);
        return createTaskResponse(task);
    }

    public TaskResponse getTask(Long taskId, String emailExecutor) {
        Optional<TaskEntity> task = taskRepository.findById(taskId);
        if (task.isEmpty()) {
            throw new NotFound("Task not found.");
        }
        if(task.get().getExecutorEmail().equals(emailExecutor)) {
            return createTaskResponse(task.get());
        }
        UserEntity admin = userService.findByEmail(emailExecutor);
        if(admin.getRole() == Role.ADMIN) {
            return createTaskResponse(task.get());
        }
        throw new AccessClosed("You don't have access to this task.");
    }

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::createTaskResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksByExecutor(String executorEmail) {
        Optional<List<TaskEntity>> tasksOpt = taskRepository.findAllByExecutorEmail(executorEmail);
        return tasksOpt.map(taskEntities -> taskEntities
                .stream()
                .map(this::createTaskResponse)
                .collect(Collectors.toList())).orElseGet(ArrayList::new);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    @Transactional
    public TaskResponse updatePriority(Long taskId, Priority priority) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFound("Task with ID " + taskId + " not found"));
        task.setPriority(priority);
        taskRepository.save(task);
        return createTaskResponse(task);
    }

    @Transactional
    public TaskResponse updateStatus(Long taskId, Status status) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFound("Task with ID " + taskId + " not found"));
        task.setTaskStatus(status);
        taskRepository.save(task);
        return createTaskResponse(task);
    }

    @Transactional
    public TaskResponse updateExecutor(Long taskId, String executorEmail) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFound("Task with ID " + taskId + " not found"));
        UserEntity executor = userService.findByEmail(executorEmail);
        task.setExecutor(executor.getName());
        task.setExecutorEmail(executor.getEmail());
        taskRepository.save(task);
        return createTaskResponse(task);
    }

    @Transactional
    public TaskResponse addComment(Long taskId, String authorEmail ,String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new InvalidCommentException("Comment cannot be empty.");
        }
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFound("Task with ID " + taskId + " not found"));
        UserEntity author = userService.findByEmail(authorEmail);
        CommentEntity comment = new CommentEntity();
        comment.setAuthor(author);
        comment.setContent(message);
        comment.setTask(task);
        comment.setTimestamp(LocalDateTime.now());
        task.getComments().add(comment);
        taskRepository.save(task);
        return createTaskResponse(task);
    }

    public Page<TaskResponse> getTasks(String authorEmail, String executorEmail, String status, String priority, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<TaskEntity> spec = Specification.where(null);

        if (authorEmail != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("authorEmail"), authorEmail)
            );
        }

        if (executorEmail != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("executorEmail"), executorEmail)
            );
        }

        if (status != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("taskStatus"), Status.valueOf(status))
            );
        }

        if (priority != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("priority"), Priority.valueOf(priority))
            );
        }

        Page<TaskEntity> taskPage = taskRepository.findAll(spec, pageable);
        return taskPage.map(this::createTaskResponse);
    }


    public List<CommentResponse> getCommentsByTask(Long taskId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFound("Task not found"));
        return task.getComments().stream().map(this::toDto).toList();
    }

    private TaskResponse createTaskResponse(TaskEntity task) {
        List<CommentResponse> comments = task.getComments().stream()
                .map(this::toDto)
                .toList();
        return new TaskResponse(
                task.getTaskId(),
                task.getTaskTitle(),
                task.getTaskDescription(),
                task.getPriority(),
                task.getTaskStatus(),
                task.getAuthor(),
                task.getAuthorEmail(),
                task.getExecutor(),
                task.getExecutorEmail(),
                comments
        );
    }

    private CommentResponse toDto(CommentEntity comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getAuthor().getEmail(),
                comment.getTimestamp()
        );
    }
}
