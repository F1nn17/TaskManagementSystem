package ru.shiraku.taskmanagementsystem.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Модель повышения роли пользователя")
public class RegisterAdminRequest {
    @Schema(description = "Электронная почта пользователя", example = "john.doe@example.com")
    @NotBlank(message = "Электронная почта не должна быть пустой.")
    private String email;
}
