package com.nsalazar.quicktask.tasklist.domain;

import com.nsalazar.quicktask.task.domain.Task;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Domain model representing a TaskList.
 *
 * <p>A TaskList groups multiple {@link Task} objects under a common name and description.
 * It provides organizational structure for tasks, allowing users to categorize and
 * manage related tasks together.
 *
 * <p><strong>Relationship:</strong>
 * A TaskList has a one-to-many relationship with Task. One TaskList can contain many Tasks,
 * and each Task can belong to at most one TaskList.
 *
 * @author nsalazar
 * @see Task
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskList {

    private UUID id;

    private String name;

    private String description;

    @Builder.Default
    private List<Task> tasks = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}

