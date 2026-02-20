package com.nsalazar.quicktask.task.application;

import com.nsalazar.quicktask.task.application.dto.mapper.ITaskDTOMapper;
import com.nsalazar.quicktask.task.application.dto.request.TaskDTOCreateRequest;
import com.nsalazar.quicktask.task.application.dto.request.TaskDTOUpdateRequest;
import com.nsalazar.quicktask.task.application.dto.response.TaskDTOResponse;
import com.nsalazar.quicktask.task.application.exception.DuplicateTitleException;
import com.nsalazar.quicktask.task.domain.Task;
import com.nsalazar.quicktask.task.domain.repository.ITaskRepository;
import com.nsalazar.quicktask.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaskService.
 *
 * <p>Tests the service layer business logic including CRUD operations and validations.
 * Uses Mockito to mock repository and mapper dependencies.
 *
 * <p><strong>Test Coverage:</strong>
 * <ul>
 *   <li>CRUD Operations: Create, Read, Update, Delete with business logic</li>
 *   <li>Validation: Title uniqueness, DTO validation, existence checks</li>
 *   <li>Exception Handling: Custom exceptions for errors</li>
 *   <li>Mapping: DTO to domain object conversion</li>
 *   <li>Edge Cases: Empty results, duplicates, invalid input</li>
 * </ul>
 *
 * @author nsalazar
 * @see TaskService
 * @see ITaskService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService Tests")
class TaskServiceTest {

    @Mock
    private ITaskRepository taskRepository;

    @Mock
    private ITaskDTOMapper taskDTOMapper;

    @InjectMocks
    private TaskService taskService;

    private UUID testTaskId;
    private Task testTask;
    private TaskDTOResponse testTaskResponse;
    private TaskDTOCreateRequest createRequest;
    private TaskDTOUpdateRequest updateRequest;

    private static final String TEST_TITLE = "Test Task";
    private static final String TEST_DESCRIPTION = "Test Description";

    /**
     * Setup method executed before each test.
     * Initializes mock objects and test data.
     */
    @BeforeEach
    void setUp() {
        testTaskId = UUID.randomUUID();

        testTask = Task.builder()
                .id(testTaskId)
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .completed(false)
                .createdAt(LocalDateTime.now())
                .build();

        testTaskResponse = TaskDTOResponse.builder()
                .id(testTaskId)
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .completed(false)
                .createdAt(LocalDateTime.now())
                .build();

        createRequest = TaskDTOCreateRequest.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .build();

        updateRequest = TaskDTOUpdateRequest.builder()
                .title("Updated Title")
                .description("Updated Description")
                .completed(false)
                .build();
    }

    /**
     * Tests retrieving all tasks with pagination.
     * Verifies successful pagination and DTO mapping.
     */
    @Test
    @DisplayName("Should get all tasks with pagination")
    void testGetAllTasks() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(List.of(testTask), pageable, 1);

        when(taskRepository.findAll(pageable)).thenReturn(taskPage);
        when(taskDTOMapper.toTaskDTOResponse(testTask)).thenReturn(testTaskResponse);

        // Act
        Page<TaskDTOResponse> result = taskService.getAll(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(TEST_TITLE, result.getContent().get(0).getTitle());
        verify(taskRepository, times(1)).findAll(pageable);
    }

    /**
     * Tests retrieving all tasks when none exist.
     * Verifies that ResourceNotFoundException is thrown.
     */
    @Test
    @DisplayName("Should throw ResourceNotFoundException when no tasks exist")
    void testGetAllTasksEmpty() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(taskRepository.findAll(pageable)).thenReturn(emptyPage);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskService.getAll(pageable),
                "Should throw ResourceNotFoundException when no tasks exist");
        verify(taskRepository, times(1)).findAll(pageable);
    }

    /**
     * Tests retrieving a single task by ID.
     * Verifies successful retrieval and DTO mapping.
     */
    @Test
    @DisplayName("Should get task by ID")
    void testGetTaskById() {
        // Arrange
        when(taskRepository.findById(testTaskId)).thenReturn(Optional.of(testTask));
        when(taskDTOMapper.toTaskDTOResponse(testTask)).thenReturn(testTaskResponse);

        // Act
        TaskDTOResponse result = taskService.getById(testTaskId);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_TITLE, result.getTitle());
        assertEquals(testTaskId, result.getId());
        verify(taskRepository, times(1)).findById(testTaskId);
    }

    /**
     * Tests retrieving a task by ID when it doesn't exist.
     * Verifies that ResourceNotFoundException is thrown.
     */
    @Test
    @DisplayName("Should throw ResourceNotFoundException when task ID not found")
    void testGetTaskByIdNotFound() {
        // Arrange
        when(taskRepository.findById(testTaskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskService.getById(testTaskId),
                "Should throw ResourceNotFoundException when task not found");
        verify(taskRepository, times(1)).findById(testTaskId);
    }

    /**
     * Tests creating a new task successfully.
     * Verifies that the task is created with correct initial state.
     */
    @Test
    @DisplayName("Should create a new task successfully")
    void testCreateTask() {
        // Arrange
        when(taskRepository.findByTitleAndNotCompleted(TEST_TITLE)).thenReturn(Optional.empty());
        when(taskDTOMapper.toTask(createRequest)).thenReturn(testTask);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(taskDTOMapper.toTaskDTOResponse(testTask)).thenReturn(testTaskResponse);

        // Act
        TaskDTOResponse result = taskService.create(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_TITLE, result.getTitle());
        assertFalse(result.isCompleted());
        verify(taskRepository, times(1)).findByTitleAndNotCompleted(TEST_TITLE);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    /**
     * Tests creating a task with a duplicate title.
     * Verifies that DuplicateTitleException is thrown.
     */
    @Test
    @DisplayName("Should throw DuplicateTitleException when title already exists")
    void testCreateTaskWithDuplicateTitle() {
        // Arrange
        when(taskRepository.findByTitleAndNotCompleted(TEST_TITLE))
                .thenReturn(Optional.of(testTask));

        // Act & Assert
        assertThrows(DuplicateTitleException.class, () -> taskService.create(createRequest),
                "Should throw DuplicateTitleException when title already exists");
        verify(taskRepository, times(1)).findByTitleAndNotCompleted(TEST_TITLE);
        verify(taskRepository, never()).save(any(Task.class));
    }

    /**
     * Tests creating a task with null request.
     * Verifies that IllegalArgumentException is thrown.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException when create request is null")
    void testCreateTaskWithNullRequest() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.create(null),
                "Should throw IllegalArgumentException when request is null");
    }

    /**
     * Tests updating an existing task successfully.
     * Verifies that the task is updated with new values.
     */
    @Test
    @DisplayName("Should update an existing task successfully")
    void testUpdateTask() {
        // Arrange
        Task updatedTask = Task.builder()
                .id(testTaskId)
                .title("Updated Title")
                .description("Updated Description")
                .completed(false)
                .createdAt(testTask.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        TaskDTOResponse updatedResponse = TaskDTOResponse.builder()
                .id(testTaskId)
                .title("Updated Title")
                .description("Updated Description")
                .completed(false)
                .createdAt(testTask.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(taskRepository.findById(testTaskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.findByTitleAndNotCompleted("Updated Title")).thenReturn(Optional.empty());
        doNothing().when(taskDTOMapper).toTask(updateRequest, testTask);
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
        when(taskDTOMapper.toTaskDTOResponse(updatedTask)).thenReturn(updatedResponse);

        // Act
        TaskDTOResponse result = taskService.update(testTaskId, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        verify(taskRepository, times(1)).findById(testTaskId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    /**
     * Tests updating a task with a new title that's already in use.
     * Verifies that DuplicateTitleException is thrown.
     */
    @Test
    @DisplayName("Should throw DuplicateTitleException when updating with duplicate title")
    void testUpdateTaskWithDuplicateTitle() {
        // Arrange
        Task existingTask = Task.builder()
                .id(UUID.randomUUID())
                .title("Updated Title")
                .description("Existing description")
                .completed(false)
                .build();

        when(taskRepository.findById(testTaskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.findByTitleAndNotCompleted("Updated Title"))
                .thenReturn(Optional.of(existingTask));

        // Act & Assert
        assertThrows(DuplicateTitleException.class, () -> taskService.update(testTaskId, updateRequest),
                "Should throw DuplicateTitleException when new title already exists");
        verify(taskRepository, times(1)).findById(testTaskId);
        verify(taskRepository, times(1)).findByTitleAndNotCompleted("Updated Title");
        verify(taskRepository, never()).save(any(Task.class));
    }

    /**
     * Tests updating a task that doesn't exist.
     * Verifies that ResourceNotFoundException is thrown.
     */
    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent task")
    void testUpdateTaskNotFound() {
        // Arrange
        when(taskRepository.findById(testTaskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.update(testTaskId, updateRequest),
                "Should throw ResourceNotFoundException when task not found");
        verify(taskRepository, times(1)).findById(testTaskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    /**
     * Tests updating a task with the same title (no change).
     * Verifies that the update proceeds without duplicate title check.
     */
    @Test
    @DisplayName("Should update task when title remains the same")
    void testUpdateTaskWithSameTitle() {
        // Arrange
        TaskDTOUpdateRequest sameTitle = TaskDTOUpdateRequest.builder()
                .title(TEST_TITLE) // Same as original
                .description("New Description")
                .completed(false)
                .build();

        Task updatedTask = Task.builder()
                .id(testTaskId)
                .title(TEST_TITLE)
                .description("New Description")
                .completed(false)
                .createdAt(testTask.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        TaskDTOResponse updatedResponse = TaskDTOResponse.builder()
                .id(testTaskId)
                .title(TEST_TITLE)
                .description("New Description")
                .completed(false)
                .build();

        when(taskRepository.findById(testTaskId)).thenReturn(Optional.of(testTask));
        doNothing().when(taskDTOMapper).toTask(sameTitle, testTask);
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
        when(taskDTOMapper.toTaskDTOResponse(updatedTask)).thenReturn(updatedResponse);

        // Act
        TaskDTOResponse result = taskService.update(testTaskId, sameTitle);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_TITLE, result.getTitle());
        verify(taskRepository, times(1)).findById(testTaskId);
        // Should not check for duplicate when title hasn't changed
        verify(taskRepository, never()).findByTitleAndNotCompleted(TEST_TITLE);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    /**
     * Tests updating a task with null request.
     * Verifies that IllegalArgumentException is thrown.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException when update request is null")
    void testUpdateTaskWithNullRequest() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> taskService.update(testTaskId, null),
                "Should throw IllegalArgumentException when request is null");
    }

    /**
     * Tests updating a task with empty title.
     * Verifies that IllegalArgumentException is thrown.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException when title is empty")
    void testUpdateTaskWithEmptyTitle() {
        // Arrange
        TaskDTOUpdateRequest invalidRequest = TaskDTOUpdateRequest.builder()
                .title("")
                .description("Description")
                .completed(false)
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> taskService.update(testTaskId, invalidRequest),
                "Should throw IllegalArgumentException when title is empty");
    }

    /**
     * Tests deleting a task successfully.
     * Verifies that the task is deleted from the repository.
     */
    @Test
    @DisplayName("Should delete a task successfully")
    void testDeleteTask() {
        // Arrange
        when(taskRepository.existsById(testTaskId)).thenReturn(true);

        // Act
        taskService.delete(testTaskId);

        // Assert
        verify(taskRepository, times(1)).existsById(testTaskId);
        verify(taskRepository, times(1)).delete(testTaskId);
    }

    /**
     * Tests deleting a task that doesn't exist.
     * Verifies that ResourceNotFoundException is thrown.
     */
    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent task")
    void testDeleteTaskNotFound() {
        // Arrange
        when(taskRepository.existsById(testTaskId)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> taskService.delete(testTaskId),
                "Should throw ResourceNotFoundException when task not found");
        verify(taskRepository, times(1)).existsById(testTaskId);
        verify(taskRepository, never()).delete(testTaskId);
    }

}

