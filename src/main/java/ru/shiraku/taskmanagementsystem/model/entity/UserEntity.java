package ru.shiraku.taskmanagementsystem.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import ru.shiraku.taskmanagementsystem.model.Role;

import java.util.UUID;

@Entity
@Data
@Schema(description = "Модель пользователя")
@Table(name = "users")
public class UserEntity {
    @Id
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID")
    @Schema(description = "Уникальный идентификатор пользователя")
    private UUID id;

    @Column(name = "name", nullable = false)
    @Schema(description = "Имя пользователя", example = "john")
    private String name;

    @Column(name = "last_name", nullable = false)
    @Schema(description = "Фамилия пользователя", example = "doe")
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    @Schema(description = "Электронная почта пользователя", example = "john.doe@example.com")
    private String email;

    @Column(name = "password", nullable = false)
    @Schema(description = "Пароль пользователя", example = "password123")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @Schema(description = "Роль пользователя", example = "USER")
    private Role role;

    public UserEntity() {
        if (id == null) {
            this.id = UUID.randomUUID();
        }
    }
}
