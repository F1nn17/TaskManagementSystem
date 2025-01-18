package ru.shiraku.taskmanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import ru.shiraku.taskmanagementsystem.model.dto.RegisterAdminRequest;
import ru.shiraku.taskmanagementsystem.service.TaskService;
import ru.shiraku.taskmanagementsystem.service.UserService;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Администраторы", description = "Управление администраторами")
public class AdminController {
    private final UserService userService;
    private final TaskService taskService;

    @Value("${ADMIN_SECRET_KEY}")
    private String adminSecretKey;

    public AdminController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    @Operation(summary = "Регистрация администрации",
            description = "Повышение прав пользователя до администрации")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно повышен до администратора"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody RegisterAdminRequest request,
                                         @RequestHeader("X-Admin-Key") String adminKey) {
        if (!adminSecretKey.equals(adminKey)) {
            throw new AccessDeniedException("Invalid admin key");
        }
        userService.registerAdmin(request);
        return ResponseEntity.ok("Set role: \"Admin\" successfully");
    }

    @Operation(summary = "Список всех пользователей",
            description = "Возвращает список всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список успешно выведен")
    })
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Список всех задач",
            description = "Возвращает список всех задач")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список задач успешно выведен")
    })
    @GetMapping("/tasks")
    public ResponseEntity<?> getAllTask() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }


}
