package ru.shiraku.taskmanagementsystem.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.shiraku.taskmanagementsystem.model.Priority;
import ru.shiraku.taskmanagementsystem.model.Status;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@Data
@Schema(description = "Модель задач")
@Table(name = "tasks")
@NoArgsConstructor
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "taskId", updatable = false, nullable = false)
    @Schema(description = "Уникальный идентификатор задачи")
    private Long taskId;

    @Column(name = "taskTitle", nullable = false)
    @Schema(description = "Имя задачи", example = "Разработать модуль")
    private String taskTitle;

    @Column(name = "taskDescription", nullable = false)
    @Schema(description = "Описание задачи", example = "Разработать модуль для ...")
    private String taskDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    @Schema(description = "Приоритет задачи", example = "Высокий")
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "taskStatus", nullable = false)
    @Schema(description = "Статус задачи", example = "TODO")
    private Status taskStatus;

    @Column(name = "author", nullable = false)
    @Schema(description = "Автор задачи", example = "Иван")
    private String author;

    @Column(name = "authorEmail", nullable = false)
    @Schema(description = "Адрес электронной почты автора задачи", example = "ivan.A@bk.ru")
    private String authorEmail;

    @Column(name = "executor", nullable = false)
    @Schema(description = "Исполнитель задачи", example = "Николай")
    private String executor;

    @Column(name = "executorEmail", nullable = false)
    @Schema(description = "Адрес электронной почты исполнителя задачи", example = "S.Nikolay@gmail.com")
    private String executorEmail;

    @Column(name = "comments", nullable = false)
    @Schema(description = "Комментарии задачи", example = "Требуется подправить ...")
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> comments = new ArrayList<>();

}
