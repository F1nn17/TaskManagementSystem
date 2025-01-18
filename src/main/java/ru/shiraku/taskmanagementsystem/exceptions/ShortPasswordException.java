package ru.shiraku.taskmanagementsystem.exceptions;

public class ShortPasswordException extends RuntimeException{
    public ShortPasswordException(String message) {
        super(message);
    }
}
