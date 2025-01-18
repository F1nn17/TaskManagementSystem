package ru.shiraku.taskmanagementsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.shiraku.taskmanagementsystem.controller.AdminController;
import ru.shiraku.taskmanagementsystem.model.Priority;
import ru.shiraku.taskmanagementsystem.model.Role;
import ru.shiraku.taskmanagementsystem.model.Status;
import ru.shiraku.taskmanagementsystem.model.dto.TaskResponse;
import ru.shiraku.taskmanagementsystem.model.dto.UserResponse;
import ru.shiraku.taskmanagementsystem.service.TaskService;
import ru.shiraku.taskmanagementsystem.service.UserService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootConfiguration
public class AdminControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private AdminController adminController;

    @Mock
    private UserService userService;
    @Mock
    private TaskService taskService;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }


    @Test
    void getAllUsers_shouldReturnListOfUsers() throws Exception {
        List<UserResponse> mockUsers = List.of(
                new UserResponse("John", "Doe", "john.doe@example.com", Role.USER),
                new UserResponse("Jane", "Doe", "jane.doe@example.com", Role.ADMIN)
        );

        when(userService.getAllUsers()).thenReturn(mockUsers);

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[0].role").value("USER"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getAllTask_shouldReturnListOfTasks() throws Exception {
        List<TaskResponse> mockTasks = List.of(
                new TaskResponse(1L, "Task 1", "Description 1", Priority.HIGH, Status.TODO,
                        "Author 1", "author1@example.com", "Executor 1", "executor1@example.com", List.of()),
                new TaskResponse(2L, "Task 2", "Description 2", Priority.MEDIUM, Status.IN_PROCESS,
                        "Author 2", "author2@example.com", "Executor 2", "executor2@example.com", List.of())
        );

        when(taskService.getAllTasks()).thenReturn(mockTasks);

        mockMvc.perform(get("/api/admin/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].taskId").value(1))
                .andExpect(jsonPath("$[0].taskTitle").value("Task 1"))
                .andExpect(jsonPath("$[0].taskDescription").value("Description 1"))
                .andExpect(jsonPath("$[1].taskId").value(2))
                .andExpect(jsonPath("$[1].taskTitle").value("Task 2"))
                .andExpect(jsonPath("$[1].taskDescription").value("Description 2"));

        verify(taskService, times(1)).getAllTasks();
    }
}
