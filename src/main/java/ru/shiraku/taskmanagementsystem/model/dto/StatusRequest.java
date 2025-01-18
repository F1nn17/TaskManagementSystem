package ru.shiraku.taskmanagementsystem.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.shiraku.taskmanagementsystem.model.Status;

@Data
@AllArgsConstructor
@Schema(description = "Модель передачи статуса задачи")
public class StatusRequest {
    @Schema(description = "Статус задачи", example = "IN_PROCESS")
    @NotBlank(message = "Статус задачи не должен быть пустым.")
    private Status status;
}
