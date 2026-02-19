package com.nsalazar.quicktask.shared.exception;

/**
 * Exception thrown when a requested resource is not found.
 * This exception extends RuntimeException to allow unchecked throwing.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining why the resource was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message explaining why the resource was not found
     * @param cause the cause of the exception
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
