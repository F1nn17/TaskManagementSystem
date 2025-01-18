package ru.shiraku.taskmanagementsystem;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.shiraku.taskmanagementsystem.controller.TaskController;
import ru.shiraku.taskmanagementsystem.model.Priority;
import ru.shiraku.taskmanagementsystem.model.Status;
import ru.shiraku.taskmanagementsystem.model.dto.*;
import ru.shiraku.taskmanagementsystem.model.entity.TaskEntity;
import ru.shiraku.taskmanagementsystem.service.TaskService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootConfiguration
public class TaskControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private TaskController taskController;

    @Mock
    private TaskService taskService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void createTask_shouldReturnCreated() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest("Task Title", "Task Description",  Priority.HIGH, "TODO");
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "admin@example.com",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);


        TaskEntity response = new TaskEntity(1L, "Task Title", "Task Description", Priority.HIGH, Status.TODO,
                "Admin", "admin@example.com", null, null, List.of());

        when(taskService.createTask(any(String.class), any(CreateTaskRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/tasks/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "taskTitle": "Task Title",
                                    "taskDescription": "Task Description",
                                    "priority": "HIGH",
                                    "taskStatus": "TODO"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.taskId").value(1))
                .andExpect(jsonPath("$.taskTitle").value("Task Title"))
                .andExpect(jsonPath("$.taskDescription").value("Task Description"));

        verify(taskService, times(1)).createTask(any(String.class), any(CreateTaskRequest.class));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void editTask_shouldReturnUpdatedTask() throws Exception {
        EditTaskRequest request = new EditTaskRequest("Updated Title", "Updated Description");
        TaskResponse response = new TaskResponse(1L, "Updated Title", "Updated Description", Priority.HIGH, Status.TODO,
                "Admin", "admin@example.com", null, null, List.of());

        when(taskService.editTask(eq(1L), any(EditTaskRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/tasks/1/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "taskTitle": "Updated Title",
                                    "taskDescription": "Updated Description"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(1))
                .andExpect(jsonPath("$.taskTitle").value("Updated Title"))
                .andExpect(jsonPath("$.taskDescription").value("Updated Description"));

        verify(taskService, times(1)).editTask(eq(1L), any(EditTaskRequest.class));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void deleteTask_shouldReturnSuccessMessage() throws Exception {
        mockMvc.perform(delete("/api/tasks/1/delete"))
                .andExpect(status().isOk())
                .andExpect(content().string("Task deleted!"));

        verify(taskService, times(1)).deleteTask(eq(1L));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void updatePriority_shouldReturnUpdatedTask() throws Exception {
        PriorityRequest request = new PriorityRequest(Priority.HIGH);
        TaskResponse response = new TaskResponse(1L, "Task Title", "Task Description", Priority.HIGH, Status.TODO,
                "Admin", "admin@example.com", null, null, List.of());

        when(taskService.updatePriority(eq(1L), eq(Priority.HIGH))).thenReturn(response);

        mockMvc.perform(patch("/api/tasks/1/update-priority")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "priority": "HIGH"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priority").value("HIGH"));

        verify(taskService, times(1)).updatePriority(eq(1L), eq(Priority.HIGH));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = {"USER"})
    void updateStatus_shouldReturnUpdatedTask() throws Exception {
        StatusRequest request = new StatusRequest(Status.IN_PROCESS);
        TaskResponse response = new TaskResponse(1L, "Task Title", "Task Description", Priority.HIGH, Status.IN_PROCESS,
                "Admin", "admin@example.com", null, null, List.of());

        when(taskService.updateStatus(eq(1L), eq(Status.IN_PROCESS))).thenReturn(response);

        mockMvc.perform(patch("/api/tasks/1/update-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "status": "IN_PROCESS"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskStatus").value("IN_PROCESS"));

        verify(taskService, times(1)).updateStatus(eq(1L), eq(Status.IN_PROCESS));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void addComment_shouldReturnTaskWithComments() throws Exception {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "user@example.com",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        AddCommentRequest request = new AddCommentRequest("New comment");
        TaskResponse response = new TaskResponse(1L, "Task Title", "Task Description", Priority.HIGH, Status.TODO,
                "Admin", "admin@example.com", "User", "user@example.com",
                List.of(new CommentResponse(1L, "New comment", "user@example.com", LocalDateTime.now())));

        when(taskService.addComment(eq(1L), eq("user@example.com"), eq("New comment"))).thenReturn(response);

        mockMvc.perform(post("/api/tasks/1/add-comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "comment": "New comment"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments[0].authorEmail").value("user@example.com"))
                .andExpect(jsonPath("$.comments[0].comment").value("New comment"));

        verify(taskService, times(1)).addComment(eq(1L), eq("user@example.com"), eq("New comment"));
    }
}
