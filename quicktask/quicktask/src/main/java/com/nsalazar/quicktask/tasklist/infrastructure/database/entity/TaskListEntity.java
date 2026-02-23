package com.nsalazar.quicktask.tasklist.infrastructure.database.entity;

import com.nsalazar.quicktask.task.infrastructure.database.entity.TaskEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA Entity representing a TaskList in the database.
 *
 * <p>Maps to the "tbl_task_lists" table. Has a one-to-many relationship with {@link TaskEntity}.
 *
 * @author nsalazar
 * @see TaskEntity
 */
@Getter
@Setter
@Entity
@Table(
    name = "tbl_task_lists",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_task_list_name",
            columnNames = {"name"}
        )
    }
)
@AllArgsConstructor
@NoArgsConstructor
public class TaskListEntity {

    /**
     * The unique identifier of the task list.
     *
     * <p>Auto-generated UUID used as the primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The name of the task list.
     *
     * <p><strong>Database Properties:</strong> VARCHAR(50), NOT NULL, UNIQUE.
     */
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    /**
     * The description of the task list.
     *
     * <p><strong>Database Properties:</strong> VARCHAR(200), NOT NULL.
     */
    @Column(name = "description", nullable = false, length = 200)
    private String description;

    /**
     * The list of task entities associated with this task list.
     *
     * <p>Mapped as a one-to-many relationship with cascade and orphan removal.
     * Uses lazy loading for performance.
     */
    @OneToMany(mappedBy = "taskList", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TaskEntity> tasks = new ArrayList<>();

    /**
     * The timestamp when the task list was created.
     *
     * <p><strong>Database Properties:</strong> NOT NULL. Set once during creation.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp when the task list was last updated.
     *
     * <p><strong>Database Properties:</strong> Nullable. Initially null, set on each update.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}

