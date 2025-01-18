package ru.shiraku.taskmanagementsystem.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Модель передачи комментария")
public class AddCommentRequest {
    @Schema(description = "Комментарий", example = "Как успехи?")
    @NotBlank(message = "Имя не должно быть пустым.")
    String comment;
}
