package com.nsalazar.quicktask.task.domain.repository;

import com.nsalazar.quicktask.task.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for the Task domain model.
 *
 * <p>This interface defines the contract for data access operations on Task domain objects.
 * It abstracts the persistence mechanism and provides a domain-centric view of data access,
 * separating business logic from infrastructure concerns.
 *
 * <p><strong>Design Pattern:</strong>
 * This interface follows the Repository pattern, which acts as a mediator between the domain
 * layer and the data mapping layer. It provides an in-memory collection-like interface for
 * accessing and persisting Task domain objects.
 *
 * <p><strong>Implementation:</strong>
 * This interface is typically implemented by {@link com.nsalazar.quicktask.task.infrastructure.database.TaskRepository},
 * which delegates to {@link com.nsalazar.quicktask.task.infrastructure.database.IJPATaskRepository}
 * for JPA operations and uses {@link com.nsalazar.quicktask.task.infrastructure.database.mapper.ITaskEntityMapper}
 * to convert between domain and persistence layers.
 *
 * <p><strong>Separation of Concerns:</strong>
 * <ul>
 *   <li>Domain Layer: Works with Task domain objects through this interface</li>
 *   <li>Service Layer: Uses this repository for data operations</li>
 *   <li>Infrastructure Layer: Implements this interface using Spring Data JPA</li>
 * </ul>
 *
 * <p><strong>CRUD Operations Provided:</strong>
 * <ul>
 *   <li>Create: {@link #save(Task)}</li>
 *   <li>Read: {@link #findById(UUID)}, {@link #findAll(Pageable)}</li>
 *   <li>Update: {@link #save(Task)} (idempotent)</li>
 *   <li>Delete: {@link #delete(UUID)}</li>
 *   <li>Check Existence: {@link #existsById(UUID)}</li>
 * </ul>
 *
 * @author nsalazar
 * @see Task
 * @see com.nsalazar.quicktask.task.infrastructure.database.TaskRepository
 * @see com.nsalazar.quicktask.task.infrastructure.database.IJPATaskRepository
 * @see com.nsalazar.quicktask.task.application.ITaskService
 */
public interface ITaskRepository {

    /**
     * Retrieves all tasks from the database with pagination and sorting support.
     *
     * <p><strong>Behavior:</strong>
     * <ul>
     *   <li>Fetches a page of Task domain objects from the database</li>
     *   <li>Applies pagination parameters to limit the number of results returned</li>
     *   <li>Applies sorting criteria to order results by specified fields</li>
     *   <li>Returns all columns for each task in the result set</li>
     * </ul>
     *
     * <p><strong>Pagination:</strong>
     * The {@code pageable} parameter controls:
     * <ul>
     *   <li>Page number (zero-indexed) - which page of results to return</li>
     *   <li>Page size - how many tasks to return per page</li>
     *   <li>Sort direction - ascending or descending order</li>
     *   <li>Sort field - which property to sort by (e.g., id, createdAt, title)</li>
     * </ul>
     *
     * <p><strong>Return Value:</strong>
     * The {@link Page} object contains:
     * <ul>
     *   <li>Content: The list of Task objects for the requested page</li>
     *   <li>Total elements: The total number of tasks in the database</li>
     *   <li>Total pages: The total number of pages available</li>
     *   <li>Current page number: The zero-indexed page number of this result</li>
     *   <li>Page size: Number of items in this page</li>
     * </ul>
     *
     * <p><strong>Common Use Cases:</strong>
     * <ul>
     *   <li>Display paginated lists of tasks in a UI</li>
     *   <li>Implement table pagination with sorting</li>
     *   <li>Load tasks with specific sort orders</li>
     * </ul>
     *
     * <p><strong>Performance Considerations:</strong>
     * <ul>
     *   <li>Uses LIMIT/OFFSET for efficient pagination</li>
     *   <li>Should be called with appropriate page size to avoid loading too much data</li>
     *   <li>Consider indexing on frequently used sort fields for better performance</li>
     * </ul>
     *
     * @param pageable the pagination and sorting information, including page number, page size,
     *                 and sort criteria. Must not be null.
     * @return a {@link Page} of {@link Task} domain objects for the requested page.
     *         Never null; may be empty if no tasks exist or page is beyond available data.
     * @throws IllegalArgumentException if pageable is null
     *
     * @see Page
     * @see Pageable
     * @see Task
     */
    Page<Task> findAll(Pageable pageable);

    /**
     * Retrieves a single task by its unique identifier.
     *
     * <p><strong>Behavior:</strong>
     * <ul>
     *   <li>Performs a single-row query to find a task with the specified UUID</li>
     *   <li>Returns an Optional that contains the task if found, or is empty if not found</li>
     *   <li>Uses UUID as the primary key for efficient lookup</li>
     * </ul>
     *
     * <p><strong>UUID Matching:</strong>
     * The UUID is matched exactly against the task's id field in the database.
     * UUID matching is case-insensitive and value-based.
     *
     * <p><strong>Return Value Handling:</strong>
     * Callers should check whether the Optional is present before accessing the Task:
     * <pre>
     * Optional&lt;Task&gt; taskOptional = repository.findById(uuid);
     * if (taskOptional.isPresent()) {
     *     Task task = taskOptional.get();
     *     // Use task...
     * } else {
     *     // Task not found
     * }
     * </pre>
     *
     * <p><strong>Common Use Cases:</strong>
     * <ul>
     *   <li>Retrieve a task for display details</li>
     *   <li>Check if a task exists before updating or deleting it</li>
     *   <li>Validate task existence in the database</li>
     *   <li>Load a task to verify its current state</li>
     * </ul>
     *
     * <p><strong>Performance:</strong>
     * This is one of the fastest operations since it uses the primary key (UUID).
     * The query typically returns in constant time, O(1).
     *
     * @param id the unique identifier (UUID) of the task to retrieve. Must not be null.
     * @return an {@link Optional} containing the {@link Task} if found, or an empty Optional
     *         if no task exists with the provided ID
     * @throws IllegalArgumentException if the id parameter is null
     *
     * @see Optional
     * @see Task
     * @see UUID
     */
    Optional<Task> findById(UUID id);

    /**
     * Saves a new task to the database or updates an existing task.
     *
     * <p><strong>Behavior - Create Operation:</strong>
     * When saving a new task (id is null or generated):
     * <ul>
     *   <li>Inserts a new row in the tasks table</li>
     *   <li>Generates and assigns a new UUID if the id is null</li>
     *   <li>Stores all fields: id, title, description, completed, createdAt, updatedAt</li>
     *   <li>Returns the saved task with the generated id populated</li>
     * </ul>
     *
     * <p><strong>Behavior - Update Operation:</strong>
     * When saving an existing task (id already exists):
     * <ul>
     *   <li>Updates the row with the matching id</li>
     *   <li>Overwrites all fields with the provided values</li>
     *   <li>Preserves the id field (cannot be modified)</li>
     *   <li>Returns the updated task with all current values</li>
     * </ul>
     *
     * <p><strong>Idempotency:</strong>
     * This method is idempotent - calling it multiple times with the same task produces the
     * same result as calling it once. The operation is safe to retry on failure.
     *
     * <p><strong>Timestamp Handling:</strong>
     * <ul>
     *   <li>The service layer is responsible for setting timestamps before calling save()</li>
     *   <li>createdAt should be set once and never modified</li>
     *   <li>updatedAt should be set to current time on updates</li>
     * </ul>
     *
     * <p><strong>Common Use Cases:</strong>
     * <ul>
     *   <li>Save a newly created task to the database</li>
     *   <li>Update an existing task with new values</li>
     *   <li>Persist changes made to a task domain object</li>
     * </ul>
     *
     * <p><strong>Transactional Behavior:</strong>
     * The save operation is typically wrapped in a transaction by the service layer.
     * If the transaction is rolled back, the changes are not persisted.
     *
     * @param task the {@link Task} domain object to save. Must not be null.
     *             For new tasks, the id field may be null (will be generated).
     *             For updates, the id field must match an existing task.
     * @return the saved {@link Task} with all fields populated including any generated ids
     *         or database-assigned values
     * @throws IllegalArgumentException if the task parameter is null
     *
     * @see Task
     */
    Task save(Task task);

    /**
     * Deletes a task from the database by its unique identifier.
     *
     * <p><strong>Behavior:</strong>
     * <ul>
     *   <li>Removes the task row from the database that matches the provided UUID</li>
     *   <li>If the task exists, it is permanently deleted</li>
     *   <li>If the task does not exist, the operation completes without error (idempotent)</li>
     *   <li>Returns no value (void operation)</li>
     * </ul>
     *
     * <p><strong>Deletion Details:</strong>
     * <ul>
     *   <li>The delete is permanent and cannot be undone</li>
     *   <li>The id becomes available for reuse (though UUIDs are globally unique)</li>
     *   <li>Any foreign key references to this task may cause constraint violations</li>
     *   <li>Cascading deletes are not configured for tasks</li>
     * </ul>
     *
     * <p><strong>Idempotency:</strong>
     * This operation is idempotent - calling delete() multiple times with the same UUID
     * is safe and produces the same result. It does not throw an error if the task
     * does not exist.
     *
     * <p><strong>Common Use Cases:</strong>
     * <ul>
     *   <li>Remove a task that is no longer needed</li>
     *   <li>Delete completed or archived tasks</li>
     *   <li>Clean up the database by removing obsolete records</li>
     * </ul>
     *
     * <p><strong>Transactional Behavior:</strong>
     * The delete operation is typically wrapped in a transaction by the service layer.
     * If the transaction is rolled back, the deletion is not persisted.
     *
     * <p><strong>Related Method:</strong>
     * To verify a task exists before deleting, use {@link #existsById(UUID)} first.
     *
     * @param id the unique identifier (UUID) of the task to delete. Must not be null.
     * @throws IllegalArgumentException if the id parameter is null
     *
     * @see UUID
     */
    void delete(UUID id);

    /**
     * Checks whether a task exists in the database by its unique identifier.
     *
     * <p><strong>Behavior:</strong>
     * <ul>
     *   <li>Checks if a task with the specified UUID exists in the database</li>
     *   <li>Returns true if the task is found, false otherwise</li>
     *   <li>Uses an efficient existence check (typically COUNT query)</li>
     *   <li>Does not load the full task data, only checks presence</li>
     * </ul>
     *
     * <p><strong>Performance:</strong>
     * This operation is more efficient than {@link #findById(UUID)} when you only need to
     * check existence without loading the full task data. The database typically optimizes
     * this to a simple existence check using COUNT or EXISTS queries.
     *
     * <p><strong>Common Use Cases:</strong>
     * <ul>
     *   <li>Verify a task exists before attempting to update or delete it</li>
     *   <li>Validate that a provided task ID references a real task</li>
     *   <li>Check preconditions before processing operations</li>
     *   <li>Implement guard clauses in service methods</li>
     * </ul>
     *
     * <p><strong>Usage Pattern:</strong>
     * <pre>
     * if (repository.existsById(taskId)) {
     *     // Task exists, safe to proceed with delete
     *     repository.delete(taskId);
     * } else {
     *     // Task not found, handle appropriately
     *     throw new ResourceNotFoundException("Task not found");
     * }
     * </pre>
     *
     * @param id the unique identifier (UUID) of the task to check for existence. Must not be null.
     * @return {@code true} if a task with the specified UUID exists in the database,
     *         {@code false} otherwise
     * @throws IllegalArgumentException if the id parameter is null
     *
     * @see UUID
     * @see #findById(UUID)
     */
    boolean existsById(UUID id);

    /**
     * Finds a task by its title when the task is incomplete (completed = false).
     *
     * <p><strong>Purpose:</strong>
     * This method searches for a task with the specified title that has not been completed yet.
     * It enforces the business rule that prevents duplicate titles for incomplete tasks.
     *
     * <p><strong>Query Logic:</strong>
     * <ul>
     *   <li>Searches by exact title match (case-sensitive)</li>
     *   <li>Only returns tasks where completed = false</li>
     *   <li>Returns at most one task (Optional)</li>
     *   <li>Uses indexed lookup for efficient performance</li>
     * </ul>
     *
     * <p><strong>Business Rule Enforcement:</strong>
     * This method is used to enforce the constraint that no two incomplete tasks can have the same title.
     * Before creating or updating a task, the service layer calls this method to validate that the
     * title is not already in use by another incomplete task.
     *
     * <p><strong>Use Cases:</strong>
     * <ul>
     *   <li>Validate title uniqueness during task creation</li>
     *   <li>Check for duplicates when updating a task's title</li>
     *   <li>Prevent duplicate titles for incomplete tasks only</li>
     *   <li>Allow multiple complete tasks to have the same title</li>
     * </ul>
     *
     * <p><strong>Example Usage in Service:</strong>
     * <pre>
     * // When creating a new task
     * if (repository.findByTitleAndNotCompleted(title).isPresent()) {
     *     throw new DuplicateTitleException("A task with this title already exists");
     * }
     * Task newTask = new Task(title, description);
     * repository.save(newTask);
     *
     * // When updating a task
     * Task existing = repository.findById(id).orElseThrow(...);
     * if (repository.findByTitleAndNotCompleted(newTitle).isPresent()) {
     *     throw new DuplicateTitleException("Another incomplete task already has this title");
     * }
     * existing.setTitle(newTitle);
     * repository.save(existing);
     * </pre>
     *
     * <p><strong>Important Notes:</strong>
     * <ul>
     *   <li>Only searches for incomplete tasks (completed = false)</li>
     *   <li>Title matching is case-sensitive</li>
     *   <li>This method queries the database for validation</li>
     *   <li>The service layer is responsible for handling the result</li>
     *   <li>Completed tasks with the same title do not cause a conflict</li>
     * </ul>
     *
     * @param title the title to search for. Must not be null or empty.
     * @return an {@link Optional} containing the {@link Task} if found, or empty Optional if no incomplete task
     *         exists with the given title
     * @throws IllegalArgumentException if title is null or empty
     *
     * @see Optional
     * @see Task
     */
    Optional<Task> findByTitleAndNotCompleted(String title);

}
