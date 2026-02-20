package com.nsalazar.quicktask.task.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for updating an existing task.
 *
 * <p>This class encapsulates the data submitted by clients when updating an existing task through the
 * REST API. It serves as a contract between the API consumer and the server, defining the fields that
 * can be modified and the validation rules for task update requests.
 *
 * <p><strong>Validation:</strong>
 * <ul>
 *   <li>Both {@code title} and {@code description} are required and must not be blank</li>
 *   <li>Blank means null, empty string, or string containing only whitespace characters</li>
 *   <li>The {@code completed} status is optional and defaults to false if not provided</li>
 *   <li>Validation is performed by the Bean Validation framework before the request reaches the service layer</li>
 * </ul>
 *
 * <p><strong>Usage Context:</strong>
 * This DTO is used as the request body parameter for the PUT endpoint that updates existing tasks:
 * <pre>
 * PUT /api/v1/tasks/{id}
 * Content-Type: application/json
 *
 * {
 *   "title": "Updated task title",
 *   "description": "Updated task description",
 *   "completed": true
 * }
 * </pre>
 *
 * <p><strong>Lifecycle:</strong>
 * <ol>
 *   <li>Client submits JSON request with title, description, and completed status</li>
 *   <li>Spring deserializes JSON to this DTO object</li>
 *   <li>Bean Validation annotated constraints are validated</li>
 *   <li>If validation passes, DTO is passed to the service layer</li>
 *   <li>Service layer maps this DTO to the existing domain Task object using the mapper</li>
 *   <li>Service layer updates the {@code updatedAt} timestamp to the current time</li>
 * </ol>
 *
 * <p><strong>Important Notes:</strong>
 * <ul>
 *   <li>The {@code id} field is not part of this DTO - it is provided as a path parameter in the URL</li>
 *   <li>The {@code createdAt} timestamp cannot be modified - it is preserved from the original task</li>
 *   <li>The {@code updatedAt} timestamp is automatically set by the service layer, not by the client</li>
 *   <li>All fields provided in this DTO will overwrite the corresponding fields in the existing task</li>
 * </ul>
 *
 * @author nsalazar
 * @see com.nsalazar.quicktask.task.application.ITaskService
 * @see com.nsalazar.quicktask.task.infrastructure.restcontroller.TaskController
 * @see com.nsalazar.quicktask.task.domain.Task
 * @see TaskDTOCreateRequest
 */
@Data
@Builder
public class TaskDTOUpdateRequest {

    /**
     * The updated title of the task.
     *
     * <p><strong>Constraints:</strong>
     * <ul>
     *   <li>Required - must not be null or empty</li>
     *   <li>Must not contain only whitespace characters</li>
     *   <li>Maximum length is typically 50 characters (enforced at database level)</li>
     *   <li>Can contain any characters including special characters and unicode</li>
     * </ul>
     *
     * <p><strong>Examples:</strong>
     * <ul>
     *   <li>"Complete project documentation by Friday"</li>
     *   <li>"Fix critical bug in authentication module"</li>
     *   <li>"Review pull requests and provide feedback"</li>
     * </ul>
     *
     * <p><strong>Validation Error Message:</strong> "Task title is required"
     *
     * <p><strong>Behavior on Update:</strong> If provided, replaces the existing title completely.
     * The new value must pass validation before being applied to the task.
     */
    @NotBlank(message = "Task title is required")
    private String title;

    /**
     * The updated description of the task.
     *
     * <p><strong>Constraints:</strong>
     * <ul>
     *   <li>Required - must not be null or empty</li>
     *   <li>Must not contain only whitespace characters</li>
     *   <li>Maximum length is typically 200 characters (enforced at database level)</li>
     *   <li>Can contain multiple lines, special characters, and unicode characters</li>
     * </ul>
     *
     * <p><strong>Examples:</strong>
     * <ul>
     *   <li>"Complete all sections including API documentation, usage examples, and deployment instructions"</li>
     *   <li>"Investigate the root cause and implement a permanent fix for the authentication issue"</li>
     *   <li>"Review all pending pull requests and provide constructive feedback to the team"</li>
     * </ul>
     *
     * <p><strong>Validation Error Message:</strong> "Task description is required"
     *
     * <p><strong>Behavior on Update:</strong> If provided, replaces the existing description completely.
     * The new value must pass validation before being applied to the task.
     */
    @NotBlank(message = "Task description is required")
    private String description;

    /**
     * The updated completion status of the task.
     *
     * <p><strong>Constraints:</strong>
     * <ul>
     *   <li>Optional - can be true or false</li>
     *   <li>Represents the completion state of the task</li>
     *   <li>{@code true} - task is completed</li>
     *   <li>{@code false} - task is still pending/incomplete</li>
     * </ul>
     *
     * <p><strong>Examples:</strong>
     * <ul>
     *   <li>Set to {@code true} when task work is finished</li>
     *   <li>Set to {@code false} to reopen or unmark a completed task</li>
     * </ul>
     *
     * <p><strong>Default Behavior:</strong> If not explicitly provided in the request, this field
     * defaults to {@code false} (task is not completed).
     *
     * <p><strong>Behavior on Update:</strong> If provided, replaces the existing completed status.
     * This is the only field in this DTO that is not strictly required to be non-blank and can
     * be modified independently of the title and description.
     */
    private boolean completed;

}
