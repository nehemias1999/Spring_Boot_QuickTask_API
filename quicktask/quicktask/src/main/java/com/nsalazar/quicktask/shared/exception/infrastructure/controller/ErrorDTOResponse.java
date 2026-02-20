package com.nsalazar.quicktask.shared.exception.infrastructure.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for error API responses.
 *
 * <p>This class encapsulates error information returned to clients when an exception
 * occurs during request processing. It provides a consistent and user-friendly error
 * response format across all API endpoints.
 *
 * <p><strong>Response Format Example:</strong>
 * <pre>
 * {
 *   "error": "Resource Not Found",
 *   "message": "Task not found with id: f47ac10b-58cc-4372-a567-0e02b2c3d479"
 * }
 * </pre>
 *
 * <p><strong>Fields:</strong>
 * <ul>
 *   <li>{@code error} - A short, human-readable summary of the error type (e.g., "Resource Not Found", "Duplicate Title")</li>
 *   <li>{@code message} - A detailed description of the specific error that occurred</li>
 * </ul>
 *
 * @author nsalazar
 * @see ExceptionController
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDTOResponse {

    /**
     * A short, human-readable summary of the error type.
     *
     * <p>Examples: "Resource Not Found", "Duplicate Title", "Validation Error", "Internal Server Error"
     */
    private String error;

    /**
     * A detailed description of the specific error that occurred.
     *
     * <p>Provides additional context about what went wrong, typically including
     * the specific resource or field that caused the error.
     */
    private String message;

}

