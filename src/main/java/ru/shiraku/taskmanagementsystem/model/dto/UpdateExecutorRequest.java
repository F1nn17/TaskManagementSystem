package ru.shiraku.taskmanagementsystem.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Модель изменения исполнителя задачи")
public class UpdateExecutorRequest {
    @Schema(description = "Адрес электронной почты исполнителя задачи", example = "S.Nikolay@gmail.com")
    @NotBlank(message = "Адрес электронной почты не должно быть пустым.")
    private String executorEmail;
}
