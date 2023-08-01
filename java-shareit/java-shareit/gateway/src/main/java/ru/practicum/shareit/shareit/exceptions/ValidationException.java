package ru.practicum.shareit.shareit.exceptions;


// для кода 400
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}

