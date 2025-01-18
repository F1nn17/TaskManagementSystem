package ru.shiraku.taskmanagementsystem.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.shiraku.taskmanagementsystem.model.Priority;

@Data
@AllArgsConstructor
@Schema(description = "Модель передачи приоритета задачи")
public class PriorityRequest {
    @Schema(description = "Новый приоритет задачи", example = "LOW")
    @NotBlank(message = "Приоритет не должен быть пустым.")
    private Priority priority;
}
