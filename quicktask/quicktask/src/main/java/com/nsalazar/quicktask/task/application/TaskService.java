package com.nsalazar.quicktask.task.application;

import com.nsalazar.quicktask.task.application.dto.mapper.ITaskDTOMapper;
import com.nsalazar.quicktask.task.application.dto.request.TaskDTOCreateRequest;
import com.nsalazar.quicktask.task.application.dto.request.TaskDTOUpdateRequest;
import com.nsalazar.quicktask.task.application.dto.response.TaskDTOResponse;
import com.nsalazar.quicktask.task.application.exception.DuplicateTitleException;
import com.nsalazar.quicktask.task.domain.Task;
import com.nsalazar.quicktask.task.domain.repository.ITaskRepository;
import com.nsalazar.quicktask.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service class for managing tasks.
 *
 * <p>This class handles business logic for task operations including CRUD operations and validations.
 * It implements the {@link ITaskService} interface and acts as a bridge between the REST controller
 * and the data repository layer.
 *
 * <p>All methods are transactional by default. Read-only operations use {@code readOnly = true}
 * to optimize database performance.
 *
 * <p>This service is responsible for:
 * <ul>
 *   <li>Retrieving tasks (all tasks paginated or by ID)</li>
 *   <li>Creating new tasks with validation and timestamp initialization</li>
 *   <li>Updating existing tasks with validation and timestamp updates</li>
 *   <li>Deleting tasks with existence verification</li>
 *   <li>Mapping between domain models and DTOs</li>
 * </ul>
 *
 * @author nsalazar
 * @see ITaskService
 * @see ITaskRepository
 * @see ITaskDTOMapper
 * @see Task
 * @see TaskDTOResponse
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TaskService implements ITaskService {

    /**
     * Repository for persisting and retrieving task data.
     * Used for all database operations related to tasks.
     */
    private final ITaskRepository taskRepository;

    /**
     * Mapper for converting between domain Task objects and Data Transfer Objects (DTOs).
     * Used to maintain separation between business logic and API contracts.
     */
    private final ITaskDTOMapper taskDTOMapper;

    /**
     * Retrieves a paginated list of all tasks.
     *
     * <p>This method fetches tasks from the repository with pagination support, allowing clients
     * to request specific pages of results. The results are sorted and paginated according to
     * the provided {@link Pageable} parameter.
     *
     * <p>This is a read-only operation and is marked with {@code @Transactional(readOnly = true)}
     * for performance optimization.
     *
     * @param pageable the pagination and sorting information, including page number, page size,
     *                 and sort criteria
     * @return a {@link Page} of {@link TaskDTOResponse} objects containing the task data for the
     *         requested page
     * @throws ResourceNotFoundException if no tasks are found in the database
     * @see Pageable
     * @see Page
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TaskDTOResponse> getAll(Pageable pageable) {
        Page<Task> tasksPage = taskRepository.findAll(pageable);

        if(tasksPage.isEmpty())
            throw new ResourceNotFoundException("Task list is empty");

        return tasksPage
                .map(taskDTOMapper::toTaskDTOResponse);
    }

    /**
     * Retrieves a single task by its unique identifier.
     *
     * <p>This method fetches a task from the repository using its UUID. The retrieved task is then
     * converted to a Data Transfer Object (DTO) for the API response.
     *
     * <p>This is a read-only operation and is marked with {@code @Transactional(readOnly = true)}
     * for performance optimization.
     *
     * @param id the unique identifier (UUID) of the task to retrieve
     * @return a {@link TaskDTOResponse} containing the task data
     * @throws ResourceNotFoundException if no task exists with the provided ID
     * @see Task
     * @see TaskDTOResponse
     */
    @Override
    @Transactional(readOnly = true)
    public TaskDTOResponse getById(UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return taskDTOMapper.toTaskDTOResponse(task);
    }

    /**
     * Creates a new task.
     *
     * <p>This method creates a new task from the provided creation request DTO. The following
     * business logic is applied during creation:
     * <ul>
     *   <li>Validates the input DTO to ensure it is not null</li>
     *   <li>Validates that the title is not empty or null</li>
     *   <li>Validates that the title is not already in use by another incomplete task</li>
     *   <li>Converts the DTO to a domain {@link Task} object</li>
     *   <li>Sets the completed status to {@code false} for new tasks</li>
     *   <li>Sets the creation timestamp to the current time</li>
     *   <li>Persists the task to the database</li>
     *   <li>Returns the saved task as a DTO response</li>
     * </ul>
     *
     * @param createTaskDTO the task creation request containing the title and description
     * @return a {@link TaskDTOResponse} containing the created task data with generated ID and timestamps
     * @throws IllegalArgumentException if the creation DTO is null, title is null/empty, or description is null/empty
     * @throws DuplicateTitleException if a task with the same title already exists and is incomplete
     * @see TaskDTOCreateRequest
     * @see TaskDTOResponse
     * @see DuplicateTitleException
     */
    @Override
    public TaskDTOResponse create(TaskDTOCreateRequest createTaskDTO) {
        validateTaskDTOCreateRequest(createTaskDTO);
        validateTitleNotDuplicated(createTaskDTO.getTitle());

        Task task = taskDTOMapper.toTask(createTaskDTO);
        task.setCompleted(false);
        task.setCreatedAt(LocalDateTime.now());

        Task savedTask = taskRepository.save(task);
        return taskDTOMapper.toTaskDTOResponse(savedTask);
    }

    /**
     * Updates an existing task.
     *
     * <p>This method updates a task with the provided ID using data from the update request DTO.
     * The following business logic is applied during update:
     * <ul>
     *   <li>Validates the update DTO to ensure required fields are present and non-empty</li>
     *   <li>Validates that the new title is not already in use by another incomplete task</li>
     *   <li>Retrieves the existing task from the repository by ID</li>
     *   <li>Maps the update DTO properties to the existing task domain object</li>
     *   <li>Updates the modification timestamp to the current time</li>
     *   <li>Persists the updated task to the database</li>
     *   <li>Returns the updated task as a DTO response</li>
     * </ul>
     *
     * <p>The creation timestamp is preserved and not modified during updates. The {@code completed}
     * status can be modified through this method.
     *
     * @param id the unique identifier (UUID) of the task to update
     * @param updateTaskDTO the update request containing the new title, description, and completion status
     * @return a {@link TaskDTOResponse} containing the updated task data
     * @throws ResourceNotFoundException if no task exists with the provided ID
     * @throws IllegalArgumentException if the update DTO is null or contains empty required fields
     * @throws DuplicateTitleException if the new title is already in use by another incomplete task
     * @see TaskDTOUpdateRequest
     * @see TaskDTOResponse
     * @see DuplicateTitleException
     */
    @Override
    public TaskDTOResponse update(UUID id, TaskDTOUpdateRequest updateTaskDTO) {
        validateTaskDTOUpdateRequest(updateTaskDTO);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        // Only validate title uniqueness if the title is being changed
        if (!task.getTitle().equals(updateTaskDTO.getTitle())) {
            validateTitleNotDuplicated(updateTaskDTO.getTitle());
        }

        taskDTOMapper.toTask(updateTaskDTO, task);
        task.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = taskRepository.save(task);
        return taskDTOMapper.toTaskDTOResponse(updatedTask);
    }

    /**
     * Deletes a task by its ID.
     *
     * <p>This method deletes a task from the database after verifying its existence. The deletion
     * is performed within a transaction to ensure data consistency.
     *
     * <p>If the task does not exist, a {@link ResourceNotFoundException} is thrown before any
     * deletion attempt is made.
     *
     * @param id the unique identifier (UUID) of the task to delete
     * @throws ResourceNotFoundException if no task exists with the provided ID
     * @see ITaskRepository#delete(UUID)
     */
    @Override
    public void delete(UUID id) {
        if(!taskRepository.existsById(id))
            throw new ResourceNotFoundException("Task not found with Id: " + id);

        taskRepository.delete(id);
    }

    /**
     * Validates the creation task DTO.
     *
     * <p>This method performs validation on the task creation request to ensure it contains
     * the required data. The following validations are performed:
     * <ul>
     *   <li>Verifies that the DTO is not null</li>
     * </ul>
     *
     * <p>Additional validation of field content (title and description) should be performed
     * at the controller or annotation level using Bean Validation constraints.
     *
     * @param createTaskDTO the creation DTO to validate
     * @throws IllegalArgumentException if the DTO is null or required fields are missing/invalid
     * @see TaskDTOCreateRequest
     */
    private void validateTaskDTOCreateRequest(TaskDTOCreateRequest createTaskDTO) {
        if (createTaskDTO == null) {
            throw new IllegalArgumentException("Task creation request cannot be null");
        }
    }

    /**
     * Validates the update task DTO.
     *
     * <p>This method performs validation on the task update request to ensure it contains
     * valid data. The following validations are performed:
     * <ul>
     *   <li>Verifies that the DTO is not null</li>
     *   <li>Ensures the title is not null and not empty (after trimming whitespace)</li>
     *   <li>Ensures the description is not null and not empty (after trimming whitespace)</li>
     * </ul>
     *
     * <p>The completed status is optional and can be set to either true or false.
     *
     * @param updateTaskDTO the update DTO to validate
     * @throws IllegalArgumentException if the DTO is null, title is missing/empty, or description is missing/empty
     * @see TaskDTOUpdateRequest
     */
    private void validateTaskDTOUpdateRequest(TaskDTOUpdateRequest updateTaskDTO) {
        if (updateTaskDTO == null) {
            throw new IllegalArgumentException("Task update request cannot be null");
        }
        if (updateTaskDTO.getTitle() == null || updateTaskDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title is required");
        }
        if (updateTaskDTO.getDescription() == null || updateTaskDTO.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Task description is required");
        }
    }

    /**
     * Validates that no incomplete task already exists with the specified title.
     *
     * <p><strong>Purpose:</strong>
     * This method enforces the business rule that prevents duplicate titles for incomplete tasks.
     * It queries the repository to check if a task with the same title already exists in an incomplete state.
     *
     * <p><strong>Validation Logic:</strong>
     * <ul>
     *   <li>Searches for an incomplete task with the specified title</li>
     *   <li>If found, throws DuplicateTitleException with a descriptive message</li>
     *   <li>If not found, allows the operation to proceed</li>
     *   <li>Completed tasks with the same title do not cause a conflict</li>
     * </ul>
     *
     * <p><strong>Usage Context:</strong>
     * This method is called:
     * <ul>
     *   <li>During task creation to ensure the title is unique among incomplete tasks</li>
     *   <li>During task update to ensure the new title doesn't conflict with existing incomplete tasks</li>
     * </ul>
     *
     * <p><strong>Exception Thrown:</strong>
     * If validation fails, a {@link DuplicateTitleException} is thrown with message:
     * {@code "A task with title '...' already exists and is incomplete"}
     *
     * <p><strong>Performance:</strong>
     * This method performs a single database query using the indexed title column.
     * The query is efficient and returns quickly.
     *
     * @param title the title to validate for uniqueness among incomplete tasks. Must not be null.
     * @throws DuplicateTitleException if a task with the same title already exists in an incomplete state
     *
     * @see DuplicateTitleException
     * @see ITaskRepository#findByTitleAndNotCompleted(String)
     */
    private void validateTitleNotDuplicated(String title) {
        if (taskRepository.findByTitleAndNotCompleted(title).isPresent()) {
            throw new DuplicateTitleException(
                    String.format("A task with title '%s' already exists and is incomplete", title)
            );
        }
    }

}