package ru.shiraku.taskmanagementsystem.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Schema(description = "Модель комментариев")
@Table(name = "comments")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commentsId", updatable = false, nullable = false)
    @Schema(description = "Уникальный идентификатор комментария")
    private Long id;

    @Column(name = "content", nullable = false)
    @Schema(description = "Содержание комментария")
    private String content;

    @JoinColumn(name = "author", nullable = false)
    @Schema(description = "Автор комментария")
    @ManyToOne
    private UserEntity author;

    @JoinColumn(name = "task", nullable = false)
    @Schema(description = "комментарий задачи")
    @ManyToOne
    private TaskEntity task;

    @Column(name = "timestamp", nullable = false)
    @Schema(description = "Время отправки комментария")
    private LocalDateTime timestamp;
}
