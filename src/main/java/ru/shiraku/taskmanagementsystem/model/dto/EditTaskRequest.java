package ru.shiraku.taskmanagementsystem.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Модель редактирования задачи")
public class EditTaskRequest {
    @Schema(description = "Имя задачи")
    private String taskTitle;
    @Schema(description = "Описание задачи")
    private String taskDescription;
}
