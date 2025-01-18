package ru.shiraku.taskmanagementsystem.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Модель входа пользователя")
public class LoginRequest {
    @Schema(description = "Электронная почта пользователя", example = "john.doe@example.com")
    @NotBlank(message = "Электронная почта не должно быть пустой.")
    private String email;
    @Schema(description = "Пароль пользователя", example = "password123")
    @NotBlank(message = "Пароль не должен быть пустым.")
    private String password;
}
