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

    /**
     * Retrieves all task lists with pagination and sorting support.
     *
     * @param pageable pagination and sorting information
     * @return a {@link Page} of {@link TaskList} domain objects
     */
    Page<TaskList> findAll(Pageable pageable);

    /**
     * Retrieves a single task list by its unique identifier.
     *
     * @param id the UUID of the task list to retrieve
     * @return an {@link Optional} containing the task list if found, or empty if not found
     */
    Optional<TaskList> findById(UUID id);

    /**
     * Persists a task list (insert or update).
     *
     * @param taskList the task list domain object to save
     * @return the saved task list with generated ID (if new)
     */
    TaskList save(TaskList taskList);

    /**
     * Deletes a task list by its unique identifier.
     *
     * @param id the UUID of the task list to delete
     */
    void delete(UUID id);

    /**
     * Checks whether a task list with the given ID exists.
     *
     * @param id the UUID to check
     * @return {@code true} if a task list with the given ID exists, {@code false} otherwise
     */
    boolean existsById(UUID id);

    /**
     * Finds a task list by its exact name.
     *
     * @param name the name to search for
     * @return an {@link Optional} containing the task list if found, or empty if not found
     */
    Optional<TaskList> findByName(String name);

}

