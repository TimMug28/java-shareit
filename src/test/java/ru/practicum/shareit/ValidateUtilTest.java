package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidateUtil;
import ru.practicum.shareit.exceptions.ValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidateUtilTest {

    @Test
    void validNumberNotNull_WithNullValue_ThrowsValidationException() {
        String message = "ID must not be null";

        assertThrows(ValidationException.class, () -> ValidateUtil.validNumberNotNull(null, message));
    }

    @Test
    void validNumberNotNull_WithNonNullValue_DoesNotThrowException() {
        String message = "ID must not be null";

        ValidateUtil.validNumberNotNull(1L, message);
    }

    @Test
    void throwNotFound_ThrowsNotFoundException() {
        String message = "Resource not found";

        assertThrows(NotFoundException.class, () -> ValidateUtil.throwNotFound(message));
    }
}

