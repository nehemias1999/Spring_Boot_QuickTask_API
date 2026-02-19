package com.nsalazar.quicktask.task.infrastructure.restcontroller;

import com.nsalazar.quicktask.shared.exception.ResourceNotFoundException;
import com.nsalazar.quicktask.task.application.ITaskService;
import com.nsalazar.quicktask.task.application.dto.request.TaskDTOCreateRequest;
import com.nsalazar.quicktask.task.application.dto.request.TaskDTOUpdateRequest;
import com.nsalazar.quicktask.task.application.dto.response.TaskDTOResponse;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for managing tasks.
 *
 * <p>This controller provides REST API endpoints for performing CRUD (Create, Read, Update, Delete)
 * operations on tasks. It handles HTTP requests and delegates business logic to the
 * {@link ITaskService} service layer.
 *
 * <p><strong>Base URL:</strong> {@code /api/v1/tasks}
 *
 * <p><strong>Supported Operations:</strong>
 * <ul>
 *   <li>GET {@code /api/v1/tasks} - Retrieve paginated list of tasks</li>
 *   <li>GET {@code /api/v1/tasks/{id}} - Retrieve a specific task by ID</li>
 *   <li>POST {@code /api/v1/tasks} - Create a new task</li>
 *   <li>PUT {@code /api/v1/tasks/{id}} - Update an existing task</li>
 *   <li>DELETE {@code /api/v1/tasks/{id}} - Delete a task</li>
 * </ul>
 *
 * <p><strong>Request/Response Format:</strong> JSON
 *
 * <p><strong>Error Handling:</strong> Exceptions are handled globally and return appropriate HTTP
 * status codes and error messages. Resource not found errors return HTTP 404, validation errors
 * return HTTP 400.
 *
 * @author nsalazar
 * @see ITaskService
 * @see TaskDTOResponse
 * @see TaskDTOCreateRequest
 * @see TaskDTOUpdateRequest
 */
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    /**
     * Service for handling task business logic.
     * Injected via constructor using Lombok's {@code @RequiredArgsConstructor}.
     */
    private final ITaskService taskService;

    /**
     * Retrieves a paginated list of all tasks.
     *
     * <p><strong>HTTP Method:</strong> GET
     * <p><strong>Endpoint:</strong> {@code GET /api/v1/tasks}
     * <p><strong>Response Status:</strong> 200 OK
     *
     * <p>This endpoint retrieves tasks from the database with pagination and sorting support.
     * By default, tasks are paginated with a page size of 20 items per page and sorted by task ID
     * in ascending order.
     *
     * <p><strong>Pagination Parameters (optional):</strong>
     * <ul>
     *   <li>{@code page} - Zero-indexed page number (default: 0)</li>
     *   <li>{@code size} - Number of items per page (default: 20)</li>
     *   <li>{@code sort} - Sort criteria in format: {@code property,asc|desc} (default: {@code id,asc})</li>
     * </ul>
     *
     * <p><strong>Example Request:</strong><br>
     * {@code GET /api/v1/tasks?page=0&size=10&sort=createdAt,desc}
     *
     * @param pageable the pagination and sorting information, including page number, page size, and sort criteria.
     *                 Default: page=0, size=20, sort=id ascending
     * @return a {@link ResponseEntity} containing a {@link Page} of {@link TaskDTOResponse} objects
     *         with HTTP status 200 OK
     * @throws ResourceNotFoundException if no tasks exist in the database
     * @see Page
     * @see Pageable
     * @see TaskDTOResponse
     */
    @GetMapping
    public ResponseEntity<Page<TaskDTOResponse>> getAll(
            @PageableDefault(size = 20)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.ASC)
            })
            Pageable pageable) {
        return ResponseEntity.ok(taskService.getAll(pageable));
    }

    /**
     * Retrieves a single task by its unique identifier.
     *
     * <p><strong>HTTP Method:</strong> GET
     * <p><strong>Endpoint:</strong> {@code GET /api/v1/tasks/{id}}
     * <p><strong>Response Status:</strong> 200 OK
     *
     * <p>This endpoint retrieves a specific task from the database using its UUID. The task data
     * is returned as a JSON response in the {@link TaskDTOResponse} format.
     *
     * <p><strong>Path Parameters:</strong>
     * <ul>
     *   <li>{@code id} - UUID of the task to retrieve (required)</li>
     * </ul>
     *
     * <p><strong>Example Request:</strong><br>
     * {@code GET /api/v1/tasks/f47ac10b-58cc-4372-a567-0e02b2c3d479}
     *
     * @param id the unique identifier (UUID) of the task to retrieve. Cannot be null.
     * @return a {@link ResponseEntity} containing the {@link TaskDTOResponse} with HTTP status 200 OK
     * @throws ResourceNotFoundException if no task exists with the provided ID
     * @throws IllegalArgumentException if the provided ID is null
     * @see TaskDTOResponse
     * @see UUID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTOResponse> getById(@PathVariable @NonNull UUID id) {
        return ResponseEntity.ok(taskService.getById(id));
    }

    /**
     * Creates a new task.
     *
     * <p><strong>HTTP Method:</strong> POST
     * <p><strong>Endpoint:</strong> {@code POST /api/v1/tasks}
     * <p><strong>Response Status:</strong> 201 Created
     *
     * <p>This endpoint creates a new task in the database. The request body must contain a valid
     * {@link TaskDTOCreateRequest} with required fields. The created task is returned with an
     * auto-generated UUID and timestamps.
     *
     * <p><strong>Request Body Format:</strong>
     * <pre>
     * {
     *   "title": "Task title",
     *   "description": "Detailed task description"
     * }
     * </pre>
     *
     * <p><strong>Example Request:</strong><br>
     * {@code POST /api/v1/tasks}
     *
     * <p><strong>Validation Rules:</strong>
     * <ul>
     *   <li>Title is required and must not be empty</li>
     *   <li>Description is required and must not be empty</li>
     * </ul>
     *
     * @param createTaskDTO the task creation request containing the title and description.
     *                      Must be valid and non-null. Validated with {@code @Valid} annotation.
     * @return a {@link ResponseEntity} containing the created {@link TaskDTOResponse}
     *         with HTTP status 201 Created. The response includes the generated UUID,
     *         creation timestamp, and completion status set to false.
     * @throws IllegalArgumentException if the creation request is invalid or contains null/empty required fields
     * @see TaskDTOCreateRequest
     * @see TaskDTOResponse
     */
    @PostMapping
    public ResponseEntity<TaskDTOResponse> create(@Valid @RequestBody TaskDTOCreateRequest createTaskDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.create(createTaskDTO));
    }

    /**
     * Updates an existing task.
     *
     * <p><strong>HTTP Method:</strong> PUT
     * <p><strong>Endpoint:</strong> {@code PUT /api/v1/tasks/{id}}
     * <p><strong>Response Status:</strong> 200 OK
     *
     * <p>This endpoint updates an existing task identified by its UUID. The request body must
     * contain a valid {@link TaskDTOUpdateRequest} with updated field values. The update timestamp
     * is automatically set to the current time.
     *
     * <p><strong>Path Parameters:</strong>
     * <ul>
     *   <li>{@code id} - UUID of the task to update (required)</li>
     * </ul>
     *
     * <p><strong>Request Body Format:</strong>
     * <pre>
     * {
     *   "title": "Updated task title",
     *   "description": "Updated task description",
     *   "completed": true
     * }
     * </pre>
     *
     * <p><strong>Example Request:</strong><br>
     * {@code PUT /api/v1/tasks/f47ac10b-58cc-4372-a567-0e02b2c3d479}
     *
     * <p><strong>Validation Rules:</strong>
     * <ul>
     *   <li>Task ID must be a valid UUID</li>
     *   <li>Title is required and must not be empty</li>
     *   <li>Description is required and must not be empty</li>
     *   <li>Completed status is optional and defaults to the current value</li>
     * </ul>
     *
     * @param id the unique identifier (UUID) of the task to update. Cannot be null.
     * @param updateTaskDTO the task update request containing the new title, description, and
     *                      completion status. Must be valid and non-null. Validated with {@code @Valid} annotation.
     * @return a {@link ResponseEntity} containing the updated {@link TaskDTOResponse}
     *         with HTTP status 200 OK. The response includes the updated data and the new
     *         modification timestamp.
     * @throws ResourceNotFoundException if no task exists with the provided ID
     * @throws IllegalArgumentException if the update request is invalid or contains null/empty required fields
     * @see TaskDTOUpdateRequest
     * @see TaskDTOResponse
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTOResponse> update(
            @PathVariable @NonNull UUID id,
            @Valid @RequestBody TaskDTOUpdateRequest updateTaskDTO) {
        return ResponseEntity.ok(taskService.update(id, updateTaskDTO));
    }

    /**
     * Deletes a task by its unique identifier.
     *
     * <p><strong>HTTP Method:</strong> DELETE
     * <p><strong>Endpoint:</strong> {@code DELETE /api/v1/tasks/{id}}
     * <p><strong>Response Status:</strong> 204 No Content
     *
     * <p>This endpoint deletes a task from the database. The request must include the UUID of the
     * task to be deleted. Upon successful deletion, an empty response with HTTP status 204 is returned.
     *
     * <p><strong>Path Parameters:</strong>
     * <ul>
     *   <li>{@code id} - UUID of the task to delete (required)</li>
     * </ul>
     *
     * <p><strong>Example Request:</strong><br>
     * {@code DELETE /api/v1/tasks/f47ac10b-58cc-4372-a567-0e02b2c3d479}
     *
     * @param id the unique identifier (UUID) of the task to delete. Cannot be null.
     * @return a {@link ResponseEntity} with HTTP status 204 No Content upon successful deletion
     * @throws ResourceNotFoundException if no task exists with the provided ID
     * @throws IllegalArgumentException if the provided ID is null
     * @see UUID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable @NonNull UUID id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
