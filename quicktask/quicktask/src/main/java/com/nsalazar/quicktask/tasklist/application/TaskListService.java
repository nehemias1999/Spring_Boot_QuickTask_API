package com.nsalazar.quicktask.tasklist.application;

import com.nsalazar.quicktask.shared.exception.ResourceNotFoundException;
import com.nsalazar.quicktask.task.domain.Task;
import com.nsalazar.quicktask.task.domain.repository.ITaskRepository;
import com.nsalazar.quicktask.tasklist.application.dto.mapper.ITaskListDTOMapper;
import com.nsalazar.quicktask.tasklist.application.dto.request.TaskListDTOCreateRequest;
import com.nsalazar.quicktask.tasklist.application.dto.request.TaskListDTOUpdateRequest;
import com.nsalazar.quicktask.tasklist.application.dto.response.TaskListDTOResponse;
import com.nsalazar.quicktask.tasklist.application.dto.response.TaskListDetailDTOResponse;
import com.nsalazar.quicktask.tasklist.application.exception.DuplicateNameException;
import com.nsalazar.quicktask.tasklist.domain.TaskList;
import com.nsalazar.quicktask.tasklist.domain.repository.ITaskListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for managing TaskLists.
 *
 * <p>Handles business logic for TaskList CRUD operations. Task assignment and movement
 * between lists is managed through the Task entity's service layer.
 *
 * @author nsalazar
 * @see ITaskListService
 * @see ITaskListRepository
 * @see ITaskRepository
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaskListService implements ITaskListService {

    private final ITaskListRepository taskListRepository;
    private final ITaskRepository taskRepository;
    private final ITaskListDTOMapper taskListDTOMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<TaskListDTOResponse> getAll(Pageable pageable) {
        log.debug("Fetching all task lists with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<TaskList> taskListPage = taskListRepository.findAll(pageable);

        if (taskListPage.isEmpty()) {
            log.warn("No task lists found in the database");
            throw new ResourceNotFoundException("No task lists found");
        }

        log.debug("Found {} task lists (total: {})", taskListPage.getNumberOfElements(), taskListPage.getTotalElements());
        return taskListPage.map(taskListDTOMapper::toTaskListDTOResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskListDetailDTOResponse getById(UUID id) {
        log.debug("Fetching task list with ID: {}", id);
        TaskList taskList = taskListRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task list not found with ID: {}", id);
                    return new ResourceNotFoundException("Task list not found with id: " + id);
                });
        log.debug("Task list found: '{}' (ID: {})", taskList.getName(), id);
        return buildTaskListDetailDTOResponse(taskList);
    }

    @Override
    public TaskListDTOResponse create(TaskListDTOCreateRequest createRequest) {
        log.debug("Creating new task list with name: '{}'", createRequest != null ? createRequest.getName() : "null");
        validateCreateRequest(createRequest);
        validateNameNotDuplicated(createRequest.getName());

        TaskList taskList = taskListDTOMapper.toTaskList(createRequest);
        taskList.setTasks(new ArrayList<>());
        taskList.setCreatedAt(LocalDateTime.now());

        TaskList savedTaskList = taskListRepository.save(taskList);
        log.info("Task list created successfully: '{}' (ID: {})", savedTaskList.getName(), savedTaskList.getId());
        return taskListDTOMapper.toTaskListDTOResponse(savedTaskList);
    }

    @Override
    public TaskListDetailDTOResponse update(UUID id, TaskListDTOUpdateRequest updateRequest) {
        log.debug("Updating task list with ID: {}", id);
        validateUpdateRequest(updateRequest);

        TaskList taskList = taskListRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task list not found for update with ID: {}", id);
                    return new ResourceNotFoundException("Task list not found with id: " + id);
                });

        if (updateRequest.getName() != null && !taskList.getName().equals(updateRequest.getName())) {
            log.debug("Name changed from '{}' to '{}', validating uniqueness", taskList.getName(), updateRequest.getName());
            validateNameNotDuplicated(updateRequest.getName());
        }

        taskListDTOMapper.toTaskList(updateRequest, taskList);
        taskList.setUpdatedAt(LocalDateTime.now());

        TaskList updatedTaskList = taskListRepository.save(taskList);
        log.info("Task list updated successfully: '{}' (ID: {})", updatedTaskList.getName(), updatedTaskList.getId());
        return buildTaskListDetailDTOResponse(updatedTaskList);
    }

    @Override
    public void delete(UUID id) {
        log.debug("Deleting task list with ID: {}", id);
        TaskList taskList = taskListRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Task list not found for deletion with ID: {}", id);
                    return new ResourceNotFoundException("Task list not found with id: " + id);
                });

        // Unlink all tasks from this list before deleting
        int taskCount = taskList.getTasks().size();
        if (taskCount > 0) {
            log.debug("Unlinking {} tasks from task list '{}'", taskCount, taskList.getName());
        }
        for (Task task : taskList.getTasks()) {
            task.setTaskListId(null);
            task.setUpdatedAt(LocalDateTime.now());
            taskRepository.save(task);
        }

        taskListRepository.delete(id);
        log.info("Task list deleted successfully: '{}' (ID: {}), {} tasks unlinked", taskList.getName(), id, taskCount);
    }

    private void validateCreateRequest(TaskListDTOCreateRequest createRequest) {
        if (createRequest == null) {
            throw new IllegalArgumentException("Task list creation request cannot be null");
        }
    }

    private void validateUpdateRequest(TaskListDTOUpdateRequest updateRequest) {
        if (updateRequest == null) {
            throw new IllegalArgumentException("Task list update request cannot be null");
        }

        boolean hasName = updateRequest.getName() != null;
        boolean hasDescription = updateRequest.getDescription() != null;

        if (!hasName && !hasDescription) {
            throw new IllegalArgumentException("Task list update request must contain at least one field to update");
        }

        if (hasName && updateRequest.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Task list name cannot be blank");
        }
        if (hasDescription && updateRequest.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Task list description cannot be blank");
        }
    }

    private void validateNameNotDuplicated(String name) {
        if (taskListRepository.findByName(name).isPresent()) {
            log.warn("Duplicate name detected: '{}'", name);
            throw new DuplicateNameException(
                    String.format("A task list with name '%s' already exists", name)
            );
        }
    }

    /**
     * Builds a {@link TaskListDetailDTOResponse} from a domain {@link TaskList} object.
     *
     * <p>This method constructs a detailed response DTO that includes the task list's data
     * along with all associated {@link Task} entities mapped as {@link TaskListDetailDTOResponse.TaskInfo} objects.
     *
     * @param taskList the domain TaskList object to convert. Must not be null.
     * @return a {@link TaskListDetailDTOResponse} with complete task list data and associated tasks info
     */
    private TaskListDetailDTOResponse buildTaskListDetailDTOResponse(TaskList taskList) {
        return TaskListDetailDTOResponse.builder()
                .id(taskList.getId())
                .name(taskList.getName())
                .description(taskList.getDescription())
                .tasks(taskList.getTasks() != null
                        ? taskList.getTasks().stream()
                                .map(task -> TaskListDetailDTOResponse.TaskInfo.builder()
                                        .id(task.getId())
                                        .title(task.getTitle())
                                        .description(task.getDescription())
                                        .completed(task.isCompleted())
                                        .createdAt(task.getCreatedAt())
                                        .updatedAt(task.getUpdatedAt())
                                        .build())
                                .collect(Collectors.toList())
                        : Collections.emptyList())
                .createdAt(taskList.getCreatedAt())
                .updatedAt(taskList.getUpdatedAt())
                .build();
    }

}
