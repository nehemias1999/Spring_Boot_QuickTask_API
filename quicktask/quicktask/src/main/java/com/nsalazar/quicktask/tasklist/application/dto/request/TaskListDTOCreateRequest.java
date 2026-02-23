package com.nsalazar.quicktask.tasklist.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new TaskList.
 *
 * <p>Contains only the name and description. The TaskList is created without any tasks.
 *
 * @author nsalazar
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskListDTOCreateRequest {

    /**
     * The name of the task list.
     *
     * <p><strong>Constraints:</strong>
     * <ul>
     *   <li>Required - must not be null or blank</li>
     *   <li>Maximum length: 50 characters (enforced at database level)</li>
     *   <li>Must be unique across all task lists</li>
     * </ul>
     *
     * <p><strong>Validation Error Message:</strong> "Task list name is required"
     */
    @NotBlank(message = "Task list name is required")
    private String name;

    /**
     * The description of the task list.
     *
     * <p><strong>Constraints:</strong>
     * <ul>
     *   <li>Required - must not be null or blank</li>
     *   <li>Maximum length: 200 characters (enforced at database level)</li>
     * </ul>
     *
     * <p><strong>Validation Error Message:</strong> "Task list description is required"
     */
    @NotBlank(message = "Task list description is required")
    private String description;

}

