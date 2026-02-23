package com.nsalazar.quicktask.tasklist.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) for detailed TaskList API responses.
 *
 * <p>This class provides a detailed view of a TaskList including the full information
 * of all associated {@link com.nsalazar.quicktask.task.domain.Task} entities. Unlike
 * {@link TaskListDTOResponse} which uses {@link com.nsalazar.quicktask.task.application.dto.response.TaskDTOResponse},
 * this DTO embeds a lightweight {@link TaskInfo} representation for each task.
 *
 * <p><strong>Usage Context:</strong>
 * This DTO is used in the response body for the following REST endpoints:
 * <ul>
 *   <li>GET {@code /api/v1/task-lists/{id}} - Returns a single TaskListDetailDTOResponse object</li>
 *   <li>PUT {@code /api/v1/task-lists/{id}} - Returns the updated TaskListDetailDTOResponse object</li>
 * </ul>
 *
 * <p><strong>Response Format Example:</strong>
 * <pre>
 * {
 *   "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
 *   "name": "Development Tasks",
 *   "description": "All development related tasks",
 *   "tasks": [
 *     {
 *       "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
 *       "title": "Complete documentation",
 *       "description": "Write comprehensive documentation",
 *       "completed": false,
 *       "createdAt": "2026-02-19T10:30:00",
 *       "updatedAt": null
 *     }
 *   ],
 *   "createdAt": "2026-02-18T09:00:00",
 *   "updatedAt": "2026-02-19T14:30:00"
 * }
 * </pre>
 *
 * <p><strong>Difference with {@link TaskListDTOResponse}:</strong>
 * <ul>
 *   <li>{@code TaskListDTOResponse} — used in paginated list responses (getAll) and create</li>
 *   <li>{@code TaskListDetailDTOResponse} — used in single-entity responses (getById, update)
 *       with embedded {@link TaskInfo} objects containing full task details</li>
 * </ul>
 *
 * @author nsalazar
 * @see TaskListDTOResponse
 * @see TaskInfo
 * @see com.nsalazar.quicktask.tasklist.domain.TaskList
 * @see com.nsalazar.quicktask.tasklist.application.ITaskListService
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskListDetailDTOResponse {

    /**
     * The unique identifier of the task list.
     *
     * <p><strong>Example:</strong> {@code a1b2c3d4-e5f6-7890-abcd-ef1234567890}
     */
    private UUID id;

    /**
     * The name of the task list.
     *
     * <p><strong>Example:</strong> {@code "Development Tasks"}
     */
    private String name;

    /**
     * The description of the task list.
     *
     * <p><strong>Example:</strong> {@code "All development related tasks"}
     */
    private String description;

    /**
     * The list of tasks associated with this task list.
     *
     * <p>Contains full task details for each associated task.
     * Can be empty if no tasks are assigned to this list.
     */
    private List<TaskInfo> tasks;

    /**
     * The timestamp when the task list was created.
     *
     * <p>Set automatically when the task list is created and never changes.
     *
     * <p><strong>Example:</strong> {@code 2026-02-18T09:00:00}
     */
    private LocalDateTime createdAt;

    /**
     * The timestamp when the task list was last updated.
     *
     * <p>Initially null after creation. Set to the current time whenever the task list is modified.
     *
     * <p><strong>Example:</strong> {@code 2026-02-19T14:30:00} or {@code null}
     */
    private LocalDateTime updatedAt;

    /**
     * Summary information of a Task associated with a TaskList.
     *
     * <p>This inner class provides a representation of a Task containing
     * its essential fields. It avoids circular references by not including
     * the TaskList reference back.
     *
     * <p><strong>Response Format Example:</strong>
     * <pre>
     * {
     *   "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
     *   "title": "Complete documentation",
     *   "description": "Write comprehensive documentation",
     *   "completed": false,
     *   "createdAt": "2026-02-19T10:30:00",
     *   "updatedAt": null
     * }
     * </pre>
     *
     * @author nsalazar
     * @see TaskListDetailDTOResponse
     * @see com.nsalazar.quicktask.task.domain.Task
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskInfo {

        /**
         * The unique identifier of the task.
         */
        private UUID id;

        /**
         * The title of the task.
         */
        private String title;

        /**
         * The description of the task.
         */
        private String description;

        /**
         * The completion status of the task.
         */
        private boolean completed;

        /**
         * The timestamp when the task was created.
         */
        private LocalDateTime createdAt;

        /**
         * The timestamp when the task was last updated.
         */
        private LocalDateTime updatedAt;

    }

}

