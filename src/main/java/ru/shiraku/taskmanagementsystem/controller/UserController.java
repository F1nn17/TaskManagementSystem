package ru.shiraku.taskmanagementsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.shiraku.taskmanagementsystem.model.dto.LoginRequest;
import ru.shiraku.taskmanagementsystem.model.dto.RegisterRequest;
import ru.shiraku.taskmanagementsystem.model.dto.UserResponse;
import ru.shiraku.taskmanagementsystem.service.TaskService;
import ru.shiraku.taskmanagementsystem.service.UserService;


@RestController
@RequestMapping("/api/user")
@Tag(name = "Пользователи", description = "Управление пользователями")
public class UserController {
    private final UserService userService;
    private final TaskService taskService;

    public UserController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    @Operation(summary = "Регистрация нового пользователя",
            description = "Регистрирует пользователя на основе входных данных")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Вход пользователя",
            description = "Выполняет вход пользователя на основе входных данных")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно вошел"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.loginUser(request));
    }

    @Operation(summary = "Задачи пользователя",
            description = "Выполняет поиск задач пользователя на основе входных данных")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Задачи успешно найдены"),
    })
    @GetMapping("/tasks")
    public ResponseEntity<?> getTasks(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(taskService.getTasksByExecutor(email));
    }
}
