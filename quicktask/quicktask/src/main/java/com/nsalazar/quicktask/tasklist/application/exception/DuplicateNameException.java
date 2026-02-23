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

    private static final long serialVersionUUID = 1L;

    public DuplicateNameException(String message) {
        super(message);
    }

    public DuplicateNameException(String message, Throwable cause) {
        super(message, cause);
    }

}

