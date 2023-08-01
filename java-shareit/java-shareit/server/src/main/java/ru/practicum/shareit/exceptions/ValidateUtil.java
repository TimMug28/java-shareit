package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidateUtil {
    public static void validNumberNotNull(Number id, String message) {
        if (id == null) {
            log.info(message);
            throw new ValidationException(message);
        }
    }

    public static void throwNotFound(String message) {
        log.info(message);
        throw new NotFoundException(message);
    }
}