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

    private final IJPATaskListRepository jpaTaskListRepository;
    private final ITaskListEntityMapper taskListEntityMapper;

    @Override
    public Page<TaskList> findAll(Pageable pageable) {
        return jpaTaskListRepository.findAll(pageable)
                .map(taskListEntityMapper::toTaskList);
    }

    @Override
    public Optional<TaskList> findById(UUID id) {
        return jpaTaskListRepository.findById(id)
                .map(taskListEntityMapper::toTaskList);
    }

    @Override
    public TaskList save(TaskList taskList) {
        TaskListEntity entity = taskListEntityMapper.toTaskListEntity(taskList);
        TaskListEntity savedEntity = jpaTaskListRepository.save(entity);
        return taskListEntityMapper.toTaskList(savedEntity);
    }

    @Override
    public void delete(UUID id) {
        jpaTaskListRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaTaskListRepository.existsById(id);
    }

    @Override
    public Optional<TaskList> findByName(String name) {
        return jpaTaskListRepository.findByName(name)
                .map(taskListEntityMapper::toTaskList);
    }

}

