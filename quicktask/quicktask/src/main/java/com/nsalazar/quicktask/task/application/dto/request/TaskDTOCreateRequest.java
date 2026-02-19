package com.nsalazar.quicktask.task.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object (DTO) for creating a new task.
 *
 * <p>This class encapsulates the data submitted by clients when creating a new task through the
 * REST API. It serves as a contract between the API consumer and the server, defining the required
 * fields and validation rules for task creation requests.
 *
 * <p><strong>Validation:</strong>
 * <ul>
 *   <li>Both {@code title} and {@code description} are required and must not be blank</li>
 *   <li>Blank means null, empty string, or string containing only whitespace characters</li>
 *   <li>Validation is performed by the Bean Validation framework before the request reaches the service layer</li>
 * </ul>
 *
 * <p><strong>Usage Context:</strong>
 * This DTO is used as the request body parameter for the POST endpoint that creates new tasks:
 * <pre>
 * POST /api/v1/tasks
 * Content-Type: application/json
 *
 * {
 *   "title": "Complete project documentation",
 *   "description": "Write comprehensive documentation for all API endpoints"
 * }
 * </pre>
 *
 * <p><strong>Lifecycle:</strong>
 * <ol>
 *   <li>Client submits JSON request with title and description</li>
 *   <li>Spring deserializes JSON to this DTO object</li>
 *   <li>Bean Validation annotated constraints are validated</li>
 *   <li>If validation passes, DTO is passed to the service layer</li>
 *   <li>Service layer maps this DTO to a domain Task object using the mapper</li>
 * </ol>
 *
 * <p><strong>Note:</strong> This DTO does NOT contain fields for id, completed status, or timestamps.
 * These fields are server-generated and managed by the application, not provided by the client.
 *
 * @author nsalazar
 * @see com.nsalazar.quicktask.task.application.ITaskService
 * @see com.nsalazar.quicktask.task.infrastructure.restcontroller.TaskController
 * @see com.nsalazar.quicktask.task.domain.Task
 */
@Data
public class TaskDTOCreateRequest {

    /**
     * The title of the task.
     *
     * <p><strong>Constraints:</strong>
     * <ul>
     *   <li>Required - must not be null or empty</li>
     *   <li>Must not contain only whitespace characters</li>
     *   <li>Maximum length is typically 50 characters (enforced at database level)</li>
     * </ul>
     *
     * <p><strong>Examples:</strong>
     * <ul>
     *   <li>"Complete project documentation"</li>
     *   <li>"Fix critical bug in login module"</li>
     *   <li>"Schedule team meeting"</li>
     * </ul>
     *
     * <p><strong>Validation Error Message:</strong> "Task title is required"
     */
    @NotBlank(message = "Task title is required")
    private String title;

    /**
     * The description of the task.
     *
     * <p><strong>Constraints:</strong>
     * <ul>
     *   <li>Required - must not be null or empty</li>
     *   <li>Must not contain only whitespace characters</li>
     *   <li>Maximum length is typically 200 characters (enforced at database level)</li>
     *   <li>Can contain multiple lines and special characters</li>
     * </ul>
     *
     * <p><strong>Examples:</strong>
     * <ul>
     *   <li>"Write comprehensive documentation for all API endpoints and include usage examples"</li>
     *   <li>"Fix the bug that causes the application to crash when processing large files"</li>
     *   <li>"Schedule a meeting with the development team to discuss Q1 objectives"</li>
     * </ul>
     *
     * <p><strong>Validation Error Message:</strong> "Task description is required"
     */
    @NotBlank(message = "Task description is required")
    private String description;

}
