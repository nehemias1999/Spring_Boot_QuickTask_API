package com.nsalazar.quicktask.task.application;

import com.nsalazar.quicktask.task.application.dto.request.TaskDTOCreateRequest;
import com.nsalazar.quicktask.task.application.dto.request.TaskDTOUpdateRequest;
import com.nsalazar.quicktask.task.application.dto.response.TaskDTOResponse;
import com.nsalazar.quicktask.task.application.dto.response.TaskDetailDTOResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for managing tasks.
 *
 * <p>This interface defines the contract for all task-related business operations. It establishes
 * the service layer API that is used by controllers and other components to interact with task data.
 * Implementations of this interface are responsible for coordinating between the REST layer,
 * domain layer, and data persistence layer.
 *
 * <p><strong>Responsibilities:</strong>
 * <ul>
 *   <li>Retrieve tasks (paginated or by ID)</li>
 *   <li>Create new tasks with validation and timestamp initialization</li>
 *   <li>Update existing tasks with validation and timestamp management</li>
 *   <li>Delete tasks with existence verification</li>
 *   <li>Map between domain models and Data Transfer Objects (DTOs)</li>
 *   <li>Enforce business rules and validation constraints</li>
 *   <li>Manage transactional behavior</li>
 * </ul>
 *
 * <p><strong>Transaction Management:</strong>
 * Implementations should handle transactional operations to ensure data consistency.
 * Read operations may use read-only transactions for performance optimization.
 * Write operations (create, update, delete) use standard transactional semantics.
 *
 * <p><strong>Error Handling:</strong>
 * <ul>
 *   <li>{@code ResourceNotFoundException} is thrown when a task with the specified ID cannot be found</li>
 *   <li>{@code IllegalArgumentException} is thrown for invalid or null input parameters</li>
 *   <li>Additional validation errors may be thrown by the DTO validation framework</li>
 * </ul>
 *
 * <p><strong>Implementation Details:</strong>
 * This interface is typically implemented by a single class annotated with {@code @Service}
 * that uses dependency injection to obtain references to repositories and mappers needed
 * to perform its operations.
 *
 * @author nsalazar
 * @see com.nsalazar.quicktask.task.infrastructure.database.TaskRepository
 * @see com.nsalazar.quicktask.task.application.dto.mapper.ITaskDTOMapper
 * @see com.nsalazar.quicktask.task.domain.Task
 * @see TaskDTOCreateRequest
 * @see TaskDTOUpdateRequest
 * @see TaskDTOResponse
 */
public interface ITaskService {

    /**
     * Retrieves a paginated list of all tasks.
     *
     * <p><strong>HTTP Context:</strong> This method backs the GET {@code /api/v1/tasks} endpoint.
     *
     * <p><strong>Behavior:</strong>
     * <ul>
     *   <li>Fetches tasks from the repository with pagination support</li>
     *   <li>Returns results according to the provided pagination and sorting criteria</li>
     *   <li>Performs read-only database access for optimal performance</li>
     *   <li>Converts each domain Task object to a TaskDTOResponse for the API response</li>
     * </ul>
     *
     * <p><strong>Pagination Details:</strong>
     * The {@code pageable} parameter controls which page of results is returned and how results
     * are sorted. Clients can specify:
     * <ul>
     *   <li>Page number (zero-indexed)</li>
     *   <li>Page size (number of items per page)</li>
     *   <li>Sort criteria (field name and direction: ascending or descending)</li>
     * </ul>
     *
     * <p><strong>Common Use Cases:</strong>
     * <ul>
     *   <li>Display a paginated list of tasks in a UI</li>
     *   <li>Fetch tasks sorted by creation date or completion status</li>
     *   <li>Implement infinite scroll or pagination controls in a client application</li>
     * </ul>
     *
     * @param pageable the pagination and sorting information, including page number, page size,
     *                 and sort criteria. Must not be null.
     * @return a {@link Page} of {@link TaskDTOResponse} objects containing the task data for
     *         the requested page. Page metadata includes total number of tasks, total pages,
     *         and whether additional pages exist.
     * @throws com.nsalazar.quicktask.shared.exception.ResourceNotFoundException
     *         if no tasks are found in the database
     * @throws IllegalArgumentException if pageable is null
     *
     * @see Page
     * @see Pageable
     * @see TaskDTOResponse
     */
    Page<TaskDTOResponse> getAll(Pageable pageable);

    /**
     * Retrieves a single task by its unique identifier.
     *
     * <p><strong>HTTP Context:</strong> This method backs the GET {@code /api/v1/tasks/{id}} endpoint.
     *
     * <p><strong>Behavior:</strong>
     * <ul>
     *   <li>Looks up a task in the repository using the provided UUID</li>
     *   <li>Performs read-only database access</li>
     *   <li>Converts the domain Task object to a TaskDTOResponse for the API response</li>
     *   <li>Returns complete task information including all timestamps</li>
     * </ul>
     *
     * <p><strong>Common Use Cases:</strong>
     * <ul>
     *   <li>Fetch detailed information about a specific task</li>
     *   <li>Display full task details in a UI</li>
     *   <li>Validate that a task exists before performing operations on it</li>
     * </ul>
     *
     * @param id the unique identifier (UUID) of the task to retrieve. Must not be null.
     * @return a {@link TaskDTOResponse} containing the complete task data including id, title,
     *         description, completion status, and timestamps
     * @throws com.nsalazar.quicktask.shared.exception.ResourceNotFoundException
     *         if no task exists with the provided ID
     * @throws IllegalArgumentException if the provided ID is null
     *
     * @see TaskDTOResponse
     * @see UUID
     */
    TaskDetailDTOResponse getById(UUID id);

    /**
     * Creates a new task.
     *
     * <p><strong>HTTP Context:</strong> This method backs the POST {@code /api/v1/tasks} endpoint.
     *
     * <p><strong>Behavior:</strong>
     * <ul>
     *   <li>Validates the creation request DTO for required fields and data integrity</li>
     *   <li>Maps the DTO to a domain Task object</li>
     *   <li>Sets server-generated fields: id (UUID), completed status (false), createdAt (current time)</li>
     *   <li>Persists the task to the database</li>
     *   <li>Returns the created task with all generated fields populated</li>
     * </ul>
     *
     * <p><strong>Initial Task State:</strong>
     * Newly created tasks always have:
     * <ul>
     *   <li>Auto-generated UUID as id</li>
     *   <li>Completed status set to false</li>
     *   <li>createdAt timestamp set to the current server time</li>
     *   <li>updatedAt timestamp set to null (no updates yet)</li>
     * </ul>
     *
     * <p><strong>Common Use Cases:</strong>
     * <ul>
     *   <li>Create a new task via a form submission</li>
     *   <li>Bulk create tasks from an import file</li>
     *   <li>Create tasks programmatically from another application</li>
     * </ul>
     *
     * @param createTaskDTO the task creation request containing title and description.
     *                      Must be non-null and contain valid values. Title and description
     *                      must not be blank. Validation is performed before processing.
     * @return a {@link TaskDTOResponse} containing the created task with auto-generated id,
     *         timestamps, and completion status. The response includes all task data ready
     *         for API response serialization.
     * @throws IllegalArgumentException if the creation DTO is null or contains invalid/empty required fields
     * @throws jakarta.validation.ConstraintViolationException if field-level validation fails
     *
     * @see TaskDTOCreateRequest
     * @see TaskDTOResponse
     */
    TaskDetailDTOResponse create(TaskDTOCreateRequest createTaskDTO);

    /**
     * Updates an existing task.
     *
     * <p><strong>HTTP Context:</strong> This method backs the PUT {@code /api/v1/tasks/{id}} endpoint.
     *
     * <p><strong>Behavior:</strong>
     * <ul>
     *   <li>Validates the update request DTO for required fields and data integrity</li>
     *   <li>Retrieves the existing task from the repository using the provided ID</li>
     *   <li>Verifies the task exists; throws exception if not found</li>
     *   <li>Maps the DTO fields to the existing task domain object (partial update)</li>
     *   <li>Updates the updatedAt timestamp to the current server time</li>
     *   <li>Preserves the createdAt timestamp (immutable)</li>
     *   <li>Persists the modified task to the database</li>
     *   <li>Returns the updated task with new data and timestamp</li>
     * </ul>
     *
     * <p><strong>Immutable Fields:</strong>
     * The following fields cannot be modified and are always preserved:
     * <ul>
     *   <li>{@code id} - The unique identifier cannot change</li>
     *   <li>{@code createdAt} - The creation timestamp remains unchanged</li>
     * </ul>
     *
     * <p><strong>Mutable Fields:</strong>
     * The following fields can be modified through this operation:
     * <ul>
     *   <li>{@code title} - Updated from the request DTO</li>
     *   <li>{@code description} - Updated from the request DTO</li>
     *   <li>{@code completed} - Updated from the request DTO</li>
     *   <li>{@code updatedAt} - Automatically set to current time</li>
     * </ul>
     *
     * <p><strong>Common Use Cases:</strong>
     * <ul>
     *   <li>Update task title or description when requirements change</li>
     *   <li>Mark a task as complete when work is finished</li>
     *   <li>Reopen a task (set completed to false) when more work is needed</li>
     *   <li>Bulk update multiple tasks</li>
     * </ul>
     *
     * @param id the unique identifier (UUID) of the task to update. Must not be null
     *           and must correspond to an existing task.
     * @param updateTaskDTO the update request containing new title, description, and
     *                      completion status. Must be non-null and contain valid values.
     *                      Title and description must not be blank. Validation is performed
     *                      before processing.
     * @return a {@link TaskDTOResponse} containing the updated task data with all fields
     *         including the new updatedAt timestamp. The response is ready for API response serialization.
     * @throws com.nsalazar.quicktask.shared.exception.ResourceNotFoundException
     *         if no task exists with the provided ID
     * @throws IllegalArgumentException if the id is null, updateTaskDTO is null, or
     *         if the DTO contains invalid/empty required fields
     * @throws jakarta.validation.ConstraintViolationException if field-level validation fails
     *
     * @see TaskDTOUpdateRequest
     * @see TaskDTOResponse
     * @see UUID
     */
    TaskDetailDTOResponse update(UUID id, TaskDTOUpdateRequest updateTaskDTO);

    /**
     * Deletes a task by its unique identifier.
     *
     * <p><strong>HTTP Context:</strong> This method backs the DELETE {@code /api/v1/tasks/{id}} endpoint.
     *
     * <p><strong>Behavior:</strong>
     * <ul>
     *   <li>Verifies that a task with the specified ID exists in the database</li>
     *   <li>Throws an exception if the task does not exist</li>
     *   <li>Deletes the task from the database</li>
     *   <li>Returns with no content (void operation)</li>
     * </ul>
     *
     * <p><strong>Deletion Details:</strong>
     * The delete operation is permanent and cannot be undone. Once a task is deleted:
     * <ul>
     *   <li>The task data is removed from the database</li>
     *   <li>The task ID becomes available for reuse (though UUIDs are typically unique globally)</li>
     *   <li>Any references to the task from other entities may become invalid</li>
     * </ul>
     *
     * <p><strong>Common Use Cases:</strong>
     * <ul>
     *   <li>Remove completed tasks that are no longer needed</li>
     *   <li>Delete tasks that were created in error</li>
     *   <li>Bulk delete tasks matching certain criteria</li>
     *   <li>Clean up database by removing obsolete tasks</li>
     * </ul>
     *
     * @param id the unique identifier (UUID) of the task to delete. Must not be null
     *           and must correspond to an existing task.
     * @throws com.nsalazar.quicktask.shared.exception.ResourceNotFoundException
     *         if no task exists with the provided ID
     * @throws IllegalArgumentException if the provided ID is null
     *
     * @see UUID
     */
    void delete(UUID id);

}
