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

    @NotBlank(message = "Task list name is required")
    private String name;

    @NotBlank(message = "Task list description is required")
    private String description;

}

