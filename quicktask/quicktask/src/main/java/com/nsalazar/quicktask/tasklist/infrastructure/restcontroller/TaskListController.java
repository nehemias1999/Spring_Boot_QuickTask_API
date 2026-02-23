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

    private final ITaskListService taskListService;

    /**
     * Retrieves a paginated list of all task lists.
     *
     * @param pageable pagination and sorting information
     * @return a page of TaskListDTOResponse objects with HTTP 200
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
     * @param id the UUID of the task list
     * @return the TaskListDetailDTOResponse with HTTP 200
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
     * @param createRequest the creation request DTO
     * @return the created TaskListDTOResponse with HTTP 201
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
     * @param id the UUID of the task list to update
     * @param updateRequest the update request DTO
     * @return the updated TaskListDetailDTOResponse with HTTP 200
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
     * Deletes a task list. All associated tasks are unlinked but not deleted.
     *
     * @param id the UUID of the task list to delete
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable @NonNull UUID id) {
        log.info("DELETE /api/v1/task-lists/{} - Deleting task list", id);
        taskListService.delete(id);
        log.info("DELETE /api/v1/task-lists/{} - Task list deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

}
