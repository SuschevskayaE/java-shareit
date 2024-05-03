package ru.practicum.shareit.exeption;

public class DuplicateException extends RuntimeException {
    public DuplicateException(final String message) {
        super(message);
    }
}
