package com.nsalazar.quicktask.task.application.exception;

/**
 * Exception thrown when attempting to create or update a task with a title that already
 * exists for another incomplete task.
 *
 * <p><strong>Purpose:</strong>
 * This exception enforces the business rule that no two incomplete tasks can have the same title.
 * It provides clear feedback to API clients when they attempt to violate this constraint.
 *
 * <p><strong>Use Cases:</strong>
 * <ul>
 *   <li>Creating a new task with a title that already exists for an incomplete task</li>
 *   <li>Updating a task's title to match another incomplete task's title</li>
 *   <li>Attempting to assign a duplicate title during task operations</li>
 * </ul>
 *
 * <p><strong>Behavior:</strong>
 * <ul>
 *   <li>Extends RuntimeException - unchecked exception</li>
 *   <li>Can be caught and handled by global exception handlers</li>
 *   <li>Provides a clear error message to API clients</li>
 *   <li>Should result in a 400 Bad Request or 409 Conflict HTTP response</li>
 * </ul>
 *
 * <p><strong>Example Usage:</strong>
 * <pre>
 * if (repository.findByTitleAndNotCompleted(title).isPresent()) {
 *     throw new DuplicateTitleException("A task with title '" + title + "' already exists");
 * }
 * </pre>
 *
 * <p><strong>Exception Hierarchy:</strong>
 * <pre>
 * RuntimeException
 *   └── DuplicateTitleException
 * </pre>
 *
 * @author nsalazar
 * @see com.nsalazar.quicktask.task.application.TaskService
 * @see com.nsalazar.quicktask.task.domain.repository.ITaskRepository
 */
public class DuplicateTitleException extends RuntimeException {

    /**
     * Serial version UID for serialization compatibility.
     */
    private static final long serialVersionUUID = 1L;

    /**
     * Constructs a new DuplicateTitleException with the specified detail message.
     *
     * <p>The message should clearly explain that a task with the given title already exists
     * and provide guidance on how to resolve the issue (e.g., by choosing a different title).
     *
     * @param message the detail message explaining the duplicate title error.
     *                Typically includes the problematic title value.
     *
     * @see RuntimeException#RuntimeException(String)
     */
    public DuplicateTitleException(String message) {
        super(message);
    }

    /**
     * Constructs a new DuplicateTitleException with the specified detail message and cause.
     *
     * <p>This constructor is useful when wrapping another exception that occurred during
     * the duplicate title check.
     *
     * @param message the detail message explaining the duplicate title error
     * @param cause   the cause of this exception (another Throwable)
     *
     * @see RuntimeException#RuntimeException(String, Throwable)
     */
    public DuplicateTitleException(String message, Throwable cause) {
        super(message, cause);
    }

}

