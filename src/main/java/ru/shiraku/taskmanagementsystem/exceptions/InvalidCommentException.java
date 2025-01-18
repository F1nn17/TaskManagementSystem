package ru.shiraku.taskmanagementsystem.exceptions;

public class InvalidCommentException extends RuntimeException {
    public InvalidCommentException(String message) {
        super(message);
    }
}
