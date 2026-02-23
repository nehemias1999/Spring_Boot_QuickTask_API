package com.nsalazar.quicktask.tasklist.domain.repository;

import com.nsalazar.quicktask.tasklist.domain.TaskList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for the TaskList domain model.
 *
 * <p>Defines the contract for data access operations on TaskList domain objects,
 * abstracting the persistence mechanism from the domain layer.
 *
 * @author nsalazar
 * @see TaskList
 */
public interface ITaskListRepository {

    Page<TaskList> findAll(Pageable pageable);

    Optional<TaskList> findById(UUID id);

    TaskList save(TaskList taskList);

    void delete(UUID id);

    boolean existsById(UUID id);

    Optional<TaskList> findByName(String name);

}

