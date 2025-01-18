package ru.shiraku.taskmanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.shiraku.taskmanagementsystem.model.dto.*;
import ru.shiraku.taskmanagementsystem.model.entity.TaskEntity;
import ru.shiraku.taskmanagementsystem.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Задачи", description = "Управление задачами")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Создание новой задачи (ADMIN)",
            description = "Создание новой задачи на основе входных данных")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Задача успешно создана"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные")
    })
    @PostMapping("/create")
    public ResponseEntity<TaskEntity> createTask(@Valid @RequestBody CreateTaskRequest request) {
        String authorEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(authorEmail, request));
    }

    @Operation(summary = "Редактирование задачи (ADMIN)",
            description = "Редактирование задачи на основе входных данных")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача успешно изменена"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @PatchMapping("/{taskId}/edit")
    public ResponseEntity<?> editTask(@PathVariable Long taskId, @RequestBody EditTaskRequest request) {
        return ResponseEntity.ok(taskService.editTask(taskId, request));
    }

    @Operation(summary = "Открыть задачу",
            description = "Открытие задачи для просмотра")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача успешно найдена"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long taskId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(taskService.getTask(taskId, email));
    }

    @Operation(summary = "Удалить задачу (ADMIN)",
            description = "Удалить задачу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задача успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @DeleteMapping("/{taskId}/delete")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok("Task deleted!");
    }

    @Operation(summary = "Обновление приоритета (ADMIN)",
            description = "Обновление приоритета у задачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Приоритет успешно изменен"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @PatchMapping("/{taskId}/update-priority")
    public ResponseEntity<TaskResponse> updatePriority(@PathVariable Long taskId,
                                                       @Valid @RequestBody PriorityRequest request) {
        return ResponseEntity.ok(taskService.updatePriority(taskId, request.getPriority()));
    }

    @Operation(summary = "Обновление статуса",
            description = "Обновление статуса у задачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус успешно изменен"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @PatchMapping("/{taskId}/update-status")
    public ResponseEntity<TaskResponse> updateStatus(@PathVariable Long taskId,
                                                     @Valid @RequestBody StatusRequest request) {
        return ResponseEntity.ok(taskService.updateStatus(taskId, request.getStatus()));
    }

    @Operation(summary = "Изменение исполнителя (ADMIN)",
            description = "Изменение исполнителя задачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Исполнитель успешно изменен"),
            @ApiResponse(responseCode = "404", description = "Задача или Исполнитель не найден")
    })
    @PatchMapping("/{taskId}/update-executor")
    public ResponseEntity<TaskResponse> updateTaskExecutor(@PathVariable Long taskId,
                                                           @Valid @RequestBody UpdateExecutorRequest request) {
        return ResponseEntity.ok(taskService.updateExecutor(taskId, request.getExecutorEmail()));
    }

    @Operation(summary = "Добавить комментарий",
            description = "Добавление комментария в задаче")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Комментарий успешно добавлен"),
            @ApiResponse(responseCode = "404", description = "Задача или Автор не найден")
    })
    @PostMapping("/{taskId}/add-comment")
    public ResponseEntity<TaskResponse> addCommentToTask(@PathVariable Long taskId,
                                                         @Valid @RequestBody AddCommentRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(taskService.addComment(taskId, email, request.getComment()));
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @RequestParam(required = false) String authorEmail,
            @RequestParam(required = false) String executorEmail,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<TaskResponse> tasks = taskService.getTasks(authorEmail, executorEmail, status, priority, page, size);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{taskId}/comments")
    public ResponseEntity<List<CommentResponse>> getCommentsByTask(@PathVariable Long taskId) {
        List<CommentResponse> comments = taskService.getCommentsByTask(taskId);
        return ResponseEntity.ok(comments);
    }
}
