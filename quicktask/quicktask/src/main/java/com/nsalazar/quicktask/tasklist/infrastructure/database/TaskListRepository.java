package com.nsalazar.quicktask.tasklist.infrastructure.database;

import com.nsalazar.quicktask.tasklist.domain.TaskList;
import com.nsalazar.quicktask.tasklist.domain.repository.ITaskListRepository;
import com.nsalazar.quicktask.tasklist.infrastructure.database.entity.TaskListEntity;
import com.nsalazar.quicktask.tasklist.infrastructure.database.mapper.ITaskListEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository implementation for TaskList entities.
 *
 * <p>Acts as a bridge between the domain layer and the persistence layer, converting
 * between domain {@link TaskList} objects and {@link TaskListEntity} persistence objects.
 *
 * @author nsalazar
 * @see ITaskListRepository
 * @see IJPATaskListRepository
 * @see ITaskListEntityMapper
 */
@Repository
@RequiredArgsConstructor
public class TaskListRepository implements ITaskListRepository {

    /**
     * Spring Data JPA repository for direct database operations on TaskListEntity.
     */
    private final IJPATaskListRepository jpaTaskListRepository;

    /**
     * Mapper for converting between domain TaskList objects and TaskListEntity persistence objects.
     */
    private final ITaskListEntityMapper taskListEntityMapper;

    /**
     * {@inheritDoc}
     *
     * <p>Fetches a paginated list of TaskListEntity objects from the database
     * and maps each one to a domain TaskList object.
     */
    @Override
    public Page<TaskList> findAll(Pageable pageable) {
        return jpaTaskListRepository.findAll(pageable)
                .map(taskListEntityMapper::toTaskList);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Performs a primary key lookup and maps the result to a domain TaskList object.
     */
    @Override
    public Optional<TaskList> findById(UUID id) {
        return jpaTaskListRepository.findById(id)
                .map(taskListEntityMapper::toTaskList);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Converts the domain TaskList to a TaskListEntity, persists it, and converts
     * the saved entity back to a domain object with the generated ID.
     */
    @Override
    public TaskList save(TaskList taskList) {
        TaskListEntity entity = taskListEntityMapper.toTaskListEntity(taskList);
        TaskListEntity savedEntity = jpaTaskListRepository.save(entity);
        return taskListEntityMapper.toTaskList(savedEntity);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to Spring Data JPA's {@code deleteById} method.
     */
    @Override
    public void delete(UUID id) {
        jpaTaskListRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to Spring Data JPA's {@code existsById} method.
     */
    @Override
    public boolean existsById(UUID id) {
        return jpaTaskListRepository.existsById(id);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to the JPA repository's custom query method and maps the result
     * to a domain TaskList object.
     */
    @Override
    public Optional<TaskList> findByName(String name) {
        return jpaTaskListRepository.findByName(name)
                .map(taskListEntityMapper::toTaskList);
    }

}

