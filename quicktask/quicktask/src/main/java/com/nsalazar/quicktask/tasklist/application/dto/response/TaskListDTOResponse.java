package com.nsalazar.quicktask.tasklist.application.dto.response;

import com.nsalazar.quicktask.task.application.dto.response.TaskDTOResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for TaskList API responses.
 *
 * <p>Contains all TaskList data including the list of associated tasks.
 *
 * @author nsalazar
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskListDTOResponse {

    private UUID id;

    private String name;

    private String description;

    private List<TaskDTOResponse> tasks;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}

