package com.nsalazar.quicktask.tasklist.application.exception;

/**
 * Exception thrown when attempting to create or update a TaskList with a name
 * that already exists.
 *
 * <p>This exception enforces the business rule that no two TaskLists can have the same name.
 *
 * @author nsalazar
 * @see com.nsalazar.quicktask.tasklist.application.TaskListService
 */
public class DuplicateNameException extends RuntimeException {

    /**
     * Serial version UID for serialization compatibility.
     */
    private static final long serialVersionUUID = 1L;

    /**
     * Constructs a new DuplicateNameException with the specified detail message.
     *
     * @param message the detail message explaining why the duplicate name was rejected
     */
    public DuplicateNameException(String message) {
        super(message);
    }

    /**
     * Constructs a new DuplicateNameException with the specified detail message and cause.
     *
     * @param message the detail message explaining why the duplicate name was rejected
     * @param cause the cause of the exception
     */
    public DuplicateNameException(String message, Throwable cause) {
        super(message, cause);
    }

}

