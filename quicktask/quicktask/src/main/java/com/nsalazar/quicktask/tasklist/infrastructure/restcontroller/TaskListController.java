package com.nsalazar.quicktask.tasklist.infrastructure.restcontroller;

import com.nsalazar.quicktask.tasklist.application.ITaskListService;
import com.nsalazar.quicktask.tasklist.application.dto.request.TaskListDTOCreateRequest;
import com.nsalazar.quicktask.tasklist.application.dto.request.TaskListDTOUpdateRequest;
import com.nsalazar.quicktask.tasklist.application.dto.response.TaskListDTOResponse;
import com.nsalazar.quicktask.tasklist.application.dto.response.TaskListDetailDTOResponse;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
 * REST controller for managing TaskLists.
 *
 * <p>Provides REST API endpoints for CRUD operations on task lists.
 * Task assignment and movement between lists is managed through the Task endpoints.
 *
 * <p><strong>Base URL:</strong> {@code /api/v1/task-lists}
 *
 * <p><strong>Supported Operations:</strong>
 * <ul>
 *   <li>GET {@code /api/v1/task-lists} - Retrieve paginated list of task lists</li>
 *   <li>GET {@code /api/v1/task-lists/{id}} - Retrieve a specific task list by ID</li>
 *   <li>POST {@code /api/v1/task-lists} - Create a new task list</li>
 *   <li>PUT {@code /api/v1/task-lists/{id}} - Update an existing task list</li>
 *   <li>DELETE {@code /api/v1/task-lists/{id}} - Delete a task list</li>
 * </ul>
 *
 * @author nsalazar
 * @see ITaskListService
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/task-lists")
@RequiredArgsConstructor
public class TaskListController {

    /**
     * Service for handling task list business logic.
     * Injected via constructor using Lombok's {@code @RequiredArgsConstructor}.
     */
    private final ITaskListService taskListService;

    /**
     * Retrieves a paginated list of all task lists.
     *
     * <p><strong>HTTP Method:</strong> GET
     * <p><strong>Endpoint:</strong> {@code GET /api/v1/task-lists}
     * <p><strong>Response Status:</strong> 200 OK
     *
     * <p><strong>Pagination Parameters (optional):</strong>
     * <ul>
     *   <li>{@code page} - Zero-indexed page number (default: 0)</li>
     *   <li>{@code size} - Number of items per page (default: 20)</li>
     *   <li>{@code sort} - Sort criteria in format: {@code property,asc|desc} (default: {@code id,asc})</li>
     * </ul>
     *
     * <p><strong>Example Request:</strong><br>
     * {@code GET /api/v1/task-lists?page=0&size=10&sort=name,asc}
     *
     * @param pageable the pagination and sorting information. Default: page=0, size=20, sort=id ascending
     * @return a {@link ResponseEntity} containing a {@link Page} of {@link TaskListDTOResponse} objects
     *         with HTTP status 200 OK
     * @throws com.nsalazar.quicktask.shared.exception.ResourceNotFoundException if no task lists exist
     */
    @GetMapping
    public ResponseEntity<Page<TaskListDTOResponse>> getAll(
            @PageableDefault(size = 20)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.ASC)
            })
            Pageable pageable) {
        log.info("GET /api/v1/task-lists - Retrieving all task lists | page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<TaskListDTOResponse> result = taskListService.getAll(pageable);
        log.info("GET /api/v1/task-lists - Successfully retrieved {} task lists (page {} of {})",
                result.getNumberOfElements(), result.getNumber() + 1, result.getTotalPages());
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves a single task list by its ID with full task details.
     *
     * <p><strong>HTTP Method:</strong> GET
     * <p><strong>Endpoint:</strong> {@code GET /api/v1/task-lists/{id}}
     * <p><strong>Response Status:</strong> 200 OK
     *
     * <p><strong>Example Request:</strong><br>
     * {@code GET /api/v1/task-lists/a1b2c3d4-e5f6-7890-abcd-ef1234567890}
     *
     * @param id the unique identifier (UUID) of the task list to retrieve. Cannot be null.
     * @return a {@link ResponseEntity} containing the {@link TaskListDetailDTOResponse} with HTTP status 200 OK
     * @throws com.nsalazar.quicktask.shared.exception.ResourceNotFoundException if no task list exists with the provided ID
     * @throws IllegalArgumentException if the provided ID is null
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskListDetailDTOResponse> getById(@PathVariable @NonNull UUID id) {
        log.info("GET /api/v1/task-lists/{} - Retrieving task list by ID", id);
        TaskListDetailDTOResponse result = taskListService.getById(id);
        log.info("GET /api/v1/task-lists/{} - Successfully retrieved task list: '{}'", id, result.getName());
        return ResponseEntity.ok(result);
    }

    /**
     * Creates a new task list.
     *
     * <p><strong>HTTP Method:</strong> POST
     * <p><strong>Endpoint:</strong> {@code POST /api/v1/task-lists}
     * <p><strong>Response Status:</strong> 201 Created
     *
     * <p><strong>Request Body Format:</strong>
     * <pre>
     * {
     *   "name": "Task list name",
     *   "description": "Task list description"
     * }
     * </pre>
     *
     * <p><strong>Validation Rules:</strong>
     * <ul>
     *   <li>Name is required and must not be blank</li>
     *   <li>Description is required and must not be blank</li>
     *   <li>Name must be unique across all task lists</li>
     * </ul>
     *
     * @param createRequest the task list creation request containing the name and description.
     *                      Validated with {@code @Valid} annotation.
     * @return a {@link ResponseEntity} containing the created {@link TaskListDTOResponse}
     *         with HTTP status 201 Created
     * @throws IllegalArgumentException if the creation request is invalid
     * @throws com.nsalazar.quicktask.tasklist.application.exception.DuplicateNameException if the name is already in use
     */
    @PostMapping
    public ResponseEntity<TaskListDTOResponse> create(@Valid @RequestBody TaskListDTOCreateRequest createRequest) {
        log.info("POST /api/v1/task-lists - Creating new task list with name: '{}'", createRequest.getName());
        TaskListDTOResponse result = taskListService.create(createRequest);
        log.info("POST /api/v1/task-lists - Task list created successfully with ID: {}", result.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    /**
     * Updates an existing task list.
     *
     * <p><strong>HTTP Method:</strong> PUT
     * <p><strong>Endpoint:</strong> {@code PUT /api/v1/task-lists/{id}}
     * <p><strong>Response Status:</strong> 200 OK
     *
     * <p><strong>Request Body Format:</strong>
     * <pre>
     * {
     *   "name": "Updated name",
     *   "description": "Updated description"
     * }
     * </pre>
     *
     * <p>All fields are optional. Only provided (non-null) fields will be updated.
     * At least one field must be provided.
     *
     * @param id the unique identifier (UUID) of the task list to update. Cannot be null.
     * @param updateRequest the update request DTO containing fields to modify.
     *                      Validated with {@code @Valid} annotation.
     * @return a {@link ResponseEntity} containing the updated {@link TaskListDetailDTOResponse}
     *         with HTTP status 200 OK
     * @throws com.nsalazar.quicktask.shared.exception.ResourceNotFoundException if no task list exists with the provided ID
     * @throws IllegalArgumentException if the update request is invalid
     * @throws com.nsalazar.quicktask.tasklist.application.exception.DuplicateNameException if the new name is already in use
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskListDetailDTOResponse> update(
            @PathVariable @NonNull UUID id,
            @Valid @RequestBody TaskListDTOUpdateRequest updateRequest) {
        log.info("PUT /api/v1/task-lists/{} - Updating task list with name: '{}'", id, updateRequest.getName());
        TaskListDetailDTOResponse result = taskListService.update(id, updateRequest);
        log.info("PUT /api/v1/task-lists/{} - Task list updated successfully", id);
        return ResponseEntity.ok(result);
    }

    /**
     * Deletes a task list. All associated tasks are unlinked (taskListId set to null) but not deleted.
     *
     * <p><strong>HTTP Method:</strong> DELETE
     * <p><strong>Endpoint:</strong> {@code DELETE /api/v1/task-lists/{id}}
     * <p><strong>Response Status:</strong> 204 No Content
     *
     * <p><strong>Example Request:</strong><br>
     * {@code DELETE /api/v1/task-lists/a1b2c3d4-e5f6-7890-abcd-ef1234567890}
     *
     * @param id the unique identifier (UUID) of the task list to delete. Cannot be null.
     * @return a {@link ResponseEntity} with HTTP status 204 No Content
     * @throws com.nsalazar.quicktask.shared.exception.ResourceNotFoundException if no task list exists with the provided ID
     * @throws IllegalArgumentException if the provided ID is null
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable @NonNull UUID id) {
        log.info("DELETE /api/v1/task-lists/{} - Deleting task list", id);
        taskListService.delete(id);
        log.info("DELETE /api/v1/task-lists/{} - Task list deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

}
