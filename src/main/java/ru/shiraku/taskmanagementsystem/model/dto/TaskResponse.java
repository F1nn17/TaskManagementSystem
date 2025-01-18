package ru.shiraku.taskmanagementsystem.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.shiraku.taskmanagementsystem.model.Priority;
import ru.shiraku.taskmanagementsystem.model.Status;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class TaskResponse {
    @Schema(description = "Уникальный идентификатор задачи")
    private Long taskId;
    @Schema(description = "Имя задачи", example = "Разработать модуль")
    private String taskTitle;
    @Schema(description = "Описание задачи", example = "Разработать модуль для ...")
    private String taskDescription;
    @Schema(description = "Приоритет задачи", example = "Высокий")
    private Priority priority;
    @Schema(description = "Статус задачи", example = "WAITING")
    private Status taskStatus;
    @Schema(description = "Автор задачи", example = "Иван")
    private String author;
    @Schema(description = "Адрес электронной почты автора задачи", example = "ivan.A@bk.ru")
    private String authorEmail;
    @Schema(description = "Исполнитель задачи", example = "Николай")
    private String executor;
    @Schema(description = "Адрес электронной почты исполнителя задачи", example = "S.Nikolay@gmail.com")
    private String executorEmail;
    @Schema(description = "Комментарии задачи", example = "Требуется подправить ...")
    private List<CommentResponse> comments;
}
