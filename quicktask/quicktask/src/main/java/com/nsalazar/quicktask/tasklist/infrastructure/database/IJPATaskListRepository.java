package com.nsalazar.quicktask.tasklist.infrastructure.database;

import com.nsalazar.quicktask.tasklist.infrastructure.database.entity.TaskListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link TaskListEntity}.
 *
 * <p>Provides low-level database access operations. This repository should only be used
 * by {@link TaskListRepository}, not directly by the service layer.
 *
 * @author nsalazar
 * @see TaskListEntity
 * @see TaskListRepository
 */
@Repository
public interface IJPATaskListRepository extends JpaRepository<TaskListEntity, UUID> {

    /**
     * Finds a task list by its exact name.
     *
     * @param name the name to search for
     * @return an {@link Optional} containing the TaskListEntity if found, or empty if not found
     */
    Optional<TaskListEntity> findByName(String name);

}

