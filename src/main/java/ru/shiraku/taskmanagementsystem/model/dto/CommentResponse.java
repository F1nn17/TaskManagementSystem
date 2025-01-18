package ru.shiraku.taskmanagementsystem.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.shiraku.taskmanagementsystem.model.entity.UserEntity;

import java.time.LocalDateTime;

@Data
@Schema(description = "Модель возвращения комментариев")
@AllArgsConstructor
public class CommentResponse {
    @Schema(description = "Уникальный идентификатор комментария")
    private Long id;
    @Schema(description = "Комментарий", example = "Как успехи?")
    String comment;
    @Schema(description = "Автор комментария")
    private String authorEmail;
    @Schema(description = "Время отправки комментария")
    private LocalDateTime timestamp;
}
