package ru.shiraku.taskmanagementsystem.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Модель регистрации пользователя")
public class RegisterRequest {
    @Schema(description = "Имя пользователя", example = "john")
    @NotBlank(message = "Имя пользователя не должно быть пустым.")
    private String name;
    @Schema(description = "Фамилия пользователя", example = "doe")
    @NotBlank(message = "Фамилия пользователя не должно быть пустым.")
    private String lastName;
    @Schema(description = "Электронная почта пользователя", example = "john.doe@example.com")
    @NotBlank(message = "Электронная почта не должна быть пустой.")
    private String email;
    @NotBlank(message = "Пароль не должен быть пустым.")
    @Schema(description = "Пароль пользователя", example = "password123")
    private String password;
}
