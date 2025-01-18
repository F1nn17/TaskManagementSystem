package ru.shiraku.taskmanagementsystem.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.shiraku.taskmanagementsystem.model.Priority;

@Data
@AllArgsConstructor
@Schema(description = "Модель создания задачи")
public class CreateTaskRequest {
    @Schema(description = "Имя задачи", example = "Разработать модуль")
    @NotBlank(message = "Имя задачи не должно быть пустым.")
    private String taskTitle;

    @Schema(description = "Описание задачи", example = "Разработать модуль для ...")
    private String taskDescription;

    @Schema(description = "Приоритет задачи", example = "HIGH")
    @NotBlank(message = "Приоритет не должен быть пустым.")
    private Priority priority;

    @Schema(description = "Адрес электронной почты исполнителя задачи", example = "S.Nikolay@gmail.com")
    @NotBlank(message = "Адрес электронной почты исполнителя не должен быть пустым.")
    private String executorEmail;
}
