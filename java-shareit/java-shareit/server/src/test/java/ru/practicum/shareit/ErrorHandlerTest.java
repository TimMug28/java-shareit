package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.handler.ErrorHandler;
import ru.practicum.shareit.handler.ErrorResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleValidationException_ReturnsBadRequest() {
        ValidationException exception = new ValidationException("Validation error");

        ErrorResponse response = errorHandler.handlerValidateException(exception);

        assertThat(response).isNotNull();
    }

    @Test
    void handleNotFoundException_ReturnsNotFound() {
        NotFoundException exception = new NotFoundException("Resource not found");

        ErrorResponse response = errorHandler.handleNotFoundException(exception);

        assertThat(response).isNotNull();
    }

    @Test
    void handleConflictException_ReturnsInternalServerError() {
        ConflictException exception = new ConflictException("Conflict occurred");

        ErrorResponse response = errorHandler.handleForUnsupportedStatus(exception);

        assertThat(response).isNotNull();
    }

    @Test
    void handleRuntimeException_ReturnsInternalServerErrorWithDefaultMessage() {
        RuntimeException exception = new RuntimeException();

        ErrorResponse response = errorHandler.handlerException(exception);

        assertThat(response).isNotNull();
    }
}

