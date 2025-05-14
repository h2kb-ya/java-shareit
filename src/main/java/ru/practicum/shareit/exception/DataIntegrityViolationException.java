package ru.practicum.shareit.exception;

public class DataIntegrityViolationException extends RuntimeException {

    public DataIntegrityViolationException(final String message) {
        super(message);
    }

}
