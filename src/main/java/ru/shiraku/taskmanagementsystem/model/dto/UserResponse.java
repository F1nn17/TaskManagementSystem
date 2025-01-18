package ru.shiraku.taskmanagementsystem.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.shiraku.taskmanagementsystem.model.Role;

@Data
@AllArgsConstructor
@Schema(description = "Модель отправки данных пользователя")
public class UserResponse {
    @Schema(description = "Имя пользователя", example = "john")
    private String name;
    @Schema(description = "Фамилия пользователя", example = "doe")
    private String lastName;
    @Schema(description = "Электронная почта пользователя", example = "john.doe@example.com")
    private String email;
    @Schema(description = "Роль пользователя", example = "USER")
    private Role role;
}
