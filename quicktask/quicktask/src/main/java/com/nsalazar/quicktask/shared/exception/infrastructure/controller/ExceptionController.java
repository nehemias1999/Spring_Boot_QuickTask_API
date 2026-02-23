package com.nsalazar.quicktask.shared.exception.infrastructure.controller;

import com.nsalazar.quicktask.shared.exception.ResourceNotFoundException;
import com.nsalazar.quicktask.task.application.exception.DuplicateTitleException;
import com.nsalazar.quicktask.tasklist.application.exception.DuplicateNameException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

/**
 * Global exception handler controller for the application.
 *
 * <p>This class intercepts exceptions thrown across all {@code @RequestMapping} methods
 * and transforms them into user-friendly {@link ErrorDTOResponse} objects with appropriate
 * HTTP status codes.
 *
 * <p><strong>Handled Exceptions:</strong>
 * <ul>
 *   <li>{@link ResourceNotFoundException} → 404 Not Found</li>
 *   <li>{@link DuplicateTitleException} → 409 Conflict</li>
 *   <li>{@link MethodArgumentNotValidException} → 400 Bad Request (validation errors)</li>
 *   <li>{@link MethodArgumentTypeMismatchException} → 400 Bad Request (type conversion errors)</li>
 *   <li>{@link Exception} → 500 Internal Server Error (fallback)</li>
 * </ul>
 *
 * @author nsalazar
 * @see ErrorDTOResponse
 * @see ResourceNotFoundException
 * @see DuplicateTitleException
 */
@Slf4j
@RestControllerAdvice
public class ExceptionController {

    /**
     * Handles {@link ResourceNotFoundException} when a requested resource is not found.
     *
     * @param ex the exception thrown when a resource is not found
     * @return a {@link ResponseEntity} with HTTP 404 and an {@link ErrorDTOResponse} describing the error
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDTOResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        ErrorDTOResponse errorResponse = ErrorDTOResponse.builder()
                .error("Resource Not Found")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handles {@link DuplicateTitleException} when a task with a duplicate title is detected.
     *
     * @param ex the exception thrown when a duplicate title is detected
     * @return a {@link ResponseEntity} with HTTP 409 and an {@link ErrorDTOResponse} describing the error
     */
    @ExceptionHandler(DuplicateTitleException.class)
    public ResponseEntity<ErrorDTOResponse> handleDuplicateTitleException(DuplicateTitleException ex) {
        log.warn("Duplicate title conflict: {}", ex.getMessage());
        ErrorDTOResponse errorResponse = ErrorDTOResponse.builder()
                .error("Duplicate Title")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handles {@link DuplicateNameException} when a task list with a duplicate name is detected.
     *
     * @param ex the exception thrown when a duplicate name is detected
     * @return a {@link ResponseEntity} with HTTP 409 and an {@link ErrorDTOResponse} describing the error
     */
    @ExceptionHandler(DuplicateNameException.class)
    public ResponseEntity<ErrorDTOResponse> handleDuplicateNameException(DuplicateNameException ex) {
        log.warn("Duplicate name conflict: {}", ex.getMessage());
        ErrorDTOResponse errorResponse = ErrorDTOResponse.builder()
                .error("Duplicate Name")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handles {@link IllegalArgumentException} for invalid request parameters.
     *
     * @param ex the exception thrown when an invalid argument is provided
     * @return a {@link ResponseEntity} with HTTP 400 and an {@link ErrorDTOResponse} describing the error
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDTOResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Bad request - illegal argument: {}", ex.getMessage());
        ErrorDTOResponse errorResponse = ErrorDTOResponse.builder()
                .error("Bad Request")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles {@link MethodArgumentNotValidException} when request body validation fails.
     *
     * <p>Collects all field validation errors and joins them into a single descriptive message.
     *
     * @param ex the exception thrown when validation annotations fail
     * @return a {@link ResponseEntity} with HTTP 400 and an {@link ErrorDTOResponse} describing the validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTOResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("Validation error: {}", details);

        ErrorDTOResponse errorResponse = ErrorDTOResponse.builder()
                .error("Validation Error")
                .message(details)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handles {@link MethodArgumentTypeMismatchException} when a path variable or request
     * parameter cannot be converted to the expected type (e.g., invalid UUID format).
     *
     * @param ex the exception thrown when type conversion fails
     * @return a {@link ResponseEntity} with HTTP 400 and an {@link ErrorDTOResponse} describing the error
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDTOResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = String.format("The parameter '%s' with value '%s' could not be converted to type '%s'",
                ex.getName(), ex.getValue(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

        log.warn("Type mismatch error: {}", message);

        ErrorDTOResponse errorResponse = ErrorDTOResponse.builder()
                .error("Bad Request")
                .message(message)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Fallback handler for any unhandled exceptions.
     *
     * <p>Catches all exceptions not handled by more specific handlers and returns
     * a generic error response to avoid exposing internal details to the client.
     *
     * @param ex the unhandled exception
     * @return a {@link ResponseEntity} with HTTP 500 and a generic {@link ErrorDTOResponse}
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTOResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        ErrorDTOResponse errorResponse = ErrorDTOResponse.builder()
                .error("Internal Server Error")
                .message("An unexpected error occurred. Please try again later.")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

}

