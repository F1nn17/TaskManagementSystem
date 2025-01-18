package ru.shiraku.taskmanagementsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.shiraku.taskmanagementsystem.controller.UserController;
import ru.shiraku.taskmanagementsystem.model.Priority;
import ru.shiraku.taskmanagementsystem.model.Role;
import ru.shiraku.taskmanagementsystem.model.Status;
import ru.shiraku.taskmanagementsystem.model.dto.LoginRequest;
import ru.shiraku.taskmanagementsystem.model.dto.RegisterRequest;
import ru.shiraku.taskmanagementsystem.model.dto.TaskResponse;
import ru.shiraku.taskmanagementsystem.model.dto.UserResponse;
import ru.shiraku.taskmanagementsystem.service.TaskService;
import ru.shiraku.taskmanagementsystem.service.UserService;
import ru.shiraku.taskmanagementsystem.utils.JWTUtils;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootConfiguration
class UserControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private TaskService taskService;

    private JWTUtils jwtUtils;

    @BeforeEach
    void setup() {
        jwtUtils = new JWTUtils();
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void register_shouldReturnCreatedResponse() throws Exception {
        RegisterRequest request = new RegisterRequest("John", "Doe", "john.doe@example.com", "password");
        UserResponse response = new UserResponse("John", "Doe", "john.doe@example.com", Role.USER);

        when(userService.registerUser(Mockito.any(RegisterRequest.class))).thenReturn(response);

        String requestBody = """
                {
                    "name": "John",
                    "lastName": "Doe",
                    "email": "john.doe@example.com",
                    "password": "password"
                }
                """;

        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(userService, times(1)).registerUser(Mockito.any(RegisterRequest.class));
    }

    @Test
    void login_shouldReturnToken() throws Exception {
        LoginRequest request = new LoginRequest("john.doe@example.com", "password");
        String token = "mocked.jwt.token";

        when(userService.loginUser(Mockito.any(LoginRequest.class))).thenReturn(token);

        String requestBody = """
                {
                    "email": "john.doe@example.com",
                    "password": "password"
                }
                """;

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string(token));

        verify(userService, times(1)).loginUser(Mockito.any(LoginRequest.class));
    }

    @Test
    void getTasks_shouldReturnUserTasks_withToken() throws Exception {
        String token = jwtUtils.generateToken(
                UUID.randomUUID(),
                "john.doe@example.com",
                Role.USER
        );
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "john.doe@example.com",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        List<TaskResponse> mockTasks = List.of(
                new TaskResponse(1L, "Task 1", "Описание задачи 1", Priority.HIGH, Status.TODO,
                        "Автор 1", "author1@example.com", "Исполнитель 1", "john.doe@example.com", List.of()),
                new TaskResponse(2L, "Task 2", "Описание задачи 2", Priority.MEDIUM, Status.IN_PROCESS,
                        "Автор 2", "author2@example.com", "Исполнитель 2", "john.doe@example.com", List.of())
        );

        when(taskService.getTasksByExecutor("john.doe@example.com")).thenReturn(mockTasks);

        mockMvc.perform(get("/api/user/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].taskId").value(1L))
                .andExpect(jsonPath("$[0].taskTitle").value("Task 1"))
                .andExpect(jsonPath("$[0].taskDescription").value("Описание задачи 1"))
                .andExpect(jsonPath("$[1].taskId").value(2L))
                .andExpect(jsonPath("$[1].taskTitle").value("Task 2"))
                .andExpect(jsonPath("$[1].taskDescription").value("Описание задачи 2"));

        verify(taskService, times(1)).getTasksByExecutor("john.doe@example.com");
    }


}

