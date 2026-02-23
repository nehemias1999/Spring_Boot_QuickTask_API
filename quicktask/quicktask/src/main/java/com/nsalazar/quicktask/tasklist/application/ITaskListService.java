package com.nsalazar.quicktask.tasklist.application;

import com.nsalazar.quicktask.tasklist.application.dto.request.TaskListDTOCreateRequest;
import com.nsalazar.quicktask.tasklist.application.dto.request.TaskListDTOUpdateRequest;
import com.nsalazar.quicktask.tasklist.application.dto.response.TaskListDTOResponse;
import com.nsalazar.quicktask.tasklist.application.dto.response.TaskListDetailDTOResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface for managing TaskLists.
 *
 * <p>Defines the contract for all TaskList-related CRUD business operations.
 * Task assignment and movement between lists is managed through the Task service layer.
 *
 * @author nsalazar
 * @see TaskListDTOCreateRequest
 * @see TaskListDTOUpdateRequest
 * @see TaskListDTOResponse
 */
public interface ITaskListService {

    /**
     * Retrieves a paginated list of all task lists.
     *
     * @param pageable pagination and sorting information
     * @return a page of TaskListDTOResponse objects
     */
    Page<TaskListDTOResponse> getAll(Pageable pageable);

    /**
     * Retrieves a single task list by its ID.
     *
     * @param id the UUID of the task list
     * @return the TaskListDetailDTOResponse with full task details
     */
    TaskListDetailDTOResponse getById(UUID id);

    /**
     * Creates a new task list (without any tasks).
     *
     * @param createRequest the creation request DTO
     * @return the created TaskListDTOResponse
     */
    TaskListDTOResponse create(TaskListDTOCreateRequest createRequest);

    /**
     * Updates an existing task list's name and description.
     *
     * @param id the UUID of the task list to update
     * @param updateRequest the update request DTO
     * @return the updated TaskListDetailDTOResponse with full task details
     */
    TaskListDetailDTOResponse update(UUID id, TaskListDTOUpdateRequest updateRequest);

    /**
     * Deletes a task list by its ID.
     * All tasks associated with the task list will be unlinked (taskListId set to null).
     *
     * @param id the UUID of the task list to delete
     */
    void delete(UUID id);

}
