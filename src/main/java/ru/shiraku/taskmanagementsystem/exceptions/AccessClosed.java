package ru.shiraku.taskmanagementsystem.exceptions;

public class AccessClosed extends RuntimeException {
    public AccessClosed(String message) {
        super(message);
    }
}
