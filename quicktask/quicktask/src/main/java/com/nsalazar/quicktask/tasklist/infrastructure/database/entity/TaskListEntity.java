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

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", nullable = false, length = 200)
    private String description;

    @OneToMany(mappedBy = "taskList", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TaskEntity> tasks = new ArrayList<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}

