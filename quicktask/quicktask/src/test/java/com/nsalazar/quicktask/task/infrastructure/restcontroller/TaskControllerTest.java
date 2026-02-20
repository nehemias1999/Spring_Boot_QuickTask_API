package com.nsalazar.quicktask.task.infrastructure.restcontroller;

import com.nsalazar.quicktask.task.application.ITaskService;
import com.nsalazar.quicktask.task.application.dto.request.TaskDTOCreateRequest;
import com.nsalazar.quicktask.task.application.dto.request.TaskDTOUpdateRequest;
import com.nsalazar.quicktask.task.application.dto.response.TaskDTOResponse;
import com.nsalazar.quicktask.task.application.exception.DuplicateTitleException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TaskController.
 *
 * <p>Tests the REST API controller layer including request/response handling, HTTP status codes,
 * and error scenarios. Uses Mockito for mocking the service layer.
 *
 * <p><strong>Test Coverage:</strong>
 * <ul>
 *   <li>GET Endpoints: Retrieve all tasks, retrieve single task</li>
 *   <li>POST Endpoint: Create new task</li>
 *   <li>PUT Endpoint: Update existing task</li>
 *   <li>DELETE Endpoint: Delete task</li>
 *   <li>HTTP Status Codes: 200, 201, 204, 404, 409</li>
 *   <li>Error Handling: Proper exception handling</li>
 * </ul>
 *
 * @author nsalazar
 * @see TaskController
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TaskController Tests")
class TaskControllerTest {

    @Mock
    private ITaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private UUID testTaskId;
    private TaskDTOResponse testTaskResponse;
    private TaskDTOCreateRequest createRequest;
    private TaskDTOUpdateRequest updateRequest;
    private Pageable pageable;

    private static final String TEST_TITLE = "Test Task";
    private static final String TEST_DESCRIPTION = "Test Description";

    /**
     * Setup method executed before each test.
     * Initializes test data and DTOs.
     */
    @BeforeEach
    void setUp() {
        testTaskId = UUID.randomUUID();
        pageable = PageRequest.of(0, 10);

        testTaskResponse = TaskDTOResponse.builder()
                .id(testTaskId)
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .completed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
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

    // ==================== GET ALL TASKS TESTS ====================

    /**
     * Tests getAll() method for retrieving all tasks.
     * Verifies successful retrieval with pagination.
     */
    @Test
    @DisplayName("Should retrieve all tasks with pagination and return 200 OK")
    void testGetAllTasks() {
        // Arrange
        Page<TaskDTOResponse> taskPage = new PageImpl<>(
                List.of(testTaskResponse),
                pageable,
                1
        );

        when(taskService.getAll(any(Pageable.class))).thenReturn(taskPage);

        // Act
        ResponseEntity<Page<TaskDTOResponse>> response = taskController.getAll(pageable);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(TEST_TITLE, response.getBody().getContent().get(0).getTitle());
        verify(taskService, times(1)).getAll(any(Pageable.class));
    }

    /**
     * Tests getAll() method when no tasks exist.
     * Verifies ResourceNotFoundException is thrown.
     */
    @Test
    @DisplayName("Should throw ResourceNotFoundException when no tasks exist")
    void testGetAllTasksNotFound() {
        // Arrange
        when(taskService.getAll(any(Pageable.class)))
                .thenThrow(new ResourceNotFoundException("Task list is empty"));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskController.getAll(pageable));
        verify(taskService, times(1)).getAll(any(Pageable.class));
    }

    // ==================== GET BY ID TESTS ====================

    /**
     * Tests getById() method for retrieving a single task.
     * Verifies successful retrieval of a specific task.
     */
    @Test
    @DisplayName("Should retrieve task by ID and return 200 OK")
    void testGetTaskById() {
        // Arrange
        when(taskService.getById(testTaskId)).thenReturn(testTaskResponse);

        // Act
        ResponseEntity<TaskDTOResponse> response = taskController.getById(testTaskId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testTaskId, response.getBody().getId());
        assertEquals(TEST_TITLE, response.getBody().getTitle());
        verify(taskService, times(1)).getById(testTaskId);
    }

    /**
     * Tests getById() method when task doesn't exist.
     * Verifies ResourceNotFoundException is thrown.
     */
    @Test
    @DisplayName("Should throw ResourceNotFoundException when task not found by ID")
    void testGetTaskByIdNotFound() {
        // Arrange
        when(taskService.getById(testTaskId))
                .thenThrow(new ResourceNotFoundException("Task not found with id: " + testTaskId));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskController.getById(testTaskId));
        verify(taskService, times(1)).getById(testTaskId);
    }

    // ==================== CREATE TASK TESTS ====================

    /**
     * Tests create() method for creating a new task.
     * Verifies successful creation with 201 Created status.
     */
    @Test
    @DisplayName("Should create a new task and return 201 Created")
    void testCreateTask() {
        // Arrange
        when(taskService.create(any(TaskDTOCreateRequest.class))).thenReturn(testTaskResponse);

        // Act
        ResponseEntity<TaskDTOResponse> response = taskController.create(createRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testTaskId, response.getBody().getId());
        assertEquals(TEST_TITLE, response.getBody().getTitle());
        assertFalse(response.getBody().isCompleted());
        verify(taskService, times(1)).create(any(TaskDTOCreateRequest.class));
    }

    /**
     * Tests create() method with duplicate title.
     * Verifies DuplicateTitleException is thrown.
     */
    @Test
    @DisplayName("Should throw DuplicateTitleException when creating task with duplicate title")
    void testCreateTaskWithDuplicateTitle() {
        // Arrange
        when(taskService.create(any(TaskDTOCreateRequest.class)))
                .thenThrow(new DuplicateTitleException("A task with title '" + TEST_TITLE + "' already exists and is incomplete"));

        // Act & Assert
        assertThrows(DuplicateTitleException.class, () -> taskController.create(createRequest));
        verify(taskService, times(1)).create(any(TaskDTOCreateRequest.class));
    }

    // ==================== UPDATE TASK TESTS ====================

    /**
     * Tests update() method for updating an existing task.
     * Verifies successful update with 200 OK status.
     */
    @Test
    @DisplayName("Should update an existing task and return 200 OK")
    void testUpdateTask() {
        // Arrange
        TaskDTOResponse updatedResponse = TaskDTOResponse.builder()
                .id(testTaskId)
                .title("Updated Title")
                .description("Updated Description")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(taskService.update(eq(testTaskId), any(TaskDTOUpdateRequest.class)))
                .thenReturn(updatedResponse);

        // Act
        ResponseEntity<TaskDTOResponse> response = taskController.update(testTaskId, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Title", response.getBody().getTitle());
        assertEquals("Updated Description", response.getBody().getDescription());
        verify(taskService, times(1)).update(eq(testTaskId), any(TaskDTOUpdateRequest.class));
    }

    /**
     * Tests update() method when task doesn't exist.
     * Verifies ResourceNotFoundException is thrown.
     */
    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent task")
    void testUpdateTaskNotFound() {
        // Arrange
        when(taskService.update(eq(testTaskId), any(TaskDTOUpdateRequest.class)))
                .thenThrow(new ResourceNotFoundException("Task not found with id: " + testTaskId));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskController.update(testTaskId, updateRequest));
        verify(taskService, times(1)).update(eq(testTaskId), any(TaskDTOUpdateRequest.class));
    }

    /**
     * Tests update() method with duplicate title.
     * Verifies DuplicateTitleException is thrown.
     */
    @Test
    @DisplayName("Should throw DuplicateTitleException when updating with duplicate title")
    void testUpdateTaskWithDuplicateTitle() {
        // Arrange
        when(taskService.update(eq(testTaskId), any(TaskDTOUpdateRequest.class)))
                .thenThrow(new DuplicateTitleException("A task with title 'Updated Title' already exists and is incomplete"));

        // Act & Assert
        assertThrows(DuplicateTitleException.class, () -> taskController.update(testTaskId, updateRequest));
        verify(taskService, times(1)).update(eq(testTaskId), any(TaskDTOUpdateRequest.class));
    }

    // ==================== DELETE TASK TESTS ====================

    /**
     * Tests delete() method for deleting a task.
     * Verifies successful deletion with 204 No Content status.
     */
    @Test
    @DisplayName("Should delete a task and return 204 No Content")
    void testDeleteTask() {
        // Arrange
        doNothing().when(taskService).delete(testTaskId);

        // Act
        ResponseEntity<?> response = taskController.delete(testTaskId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(taskService, times(1)).delete(testTaskId);
    }

    /**
     * Tests delete() method when task doesn't exist.
     * Verifies ResourceNotFoundException is thrown.
     */
    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent task")
    void testDeleteTaskNotFound() {
        // Arrange
        doThrow(new ResourceNotFoundException("Task not found with Id: " + testTaskId))
                .when(taskService).delete(testTaskId);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskController.delete(testTaskId));
        verify(taskService, times(1)).delete(testTaskId);
    }

    // ==================== ADDITIONAL TESTS ====================

    /**
     * Tests that create() correctly passes the DTO to service.
     * Verifies the service receives the correct data.
     */
    @Test
    @DisplayName("Should pass correct DTO to service when creating task")
    void testCreateTaskPassesCorrectDTO() {
        // Arrange
        when(taskService.create(any(TaskDTOCreateRequest.class))).thenReturn(testTaskResponse);

        // Act
        taskController.create(createRequest);

        // Assert
        verify(taskService).create(argThat(dto ->
                dto.getTitle().equals(TEST_TITLE) &&
                dto.getDescription().equals(TEST_DESCRIPTION)
        ));
    }

    /**
     * Tests that update() correctly passes ID and DTO to service.
     * Verifies the service receives the correct parameters.
     */
    @Test
    @DisplayName("Should pass correct ID and DTO to service when updating task")
    void testUpdateTaskPassesCorrectParameters() {
        // Arrange
        TaskDTOResponse updatedResponse = TaskDTOResponse.builder()
                .id(testTaskId)
                .title("Updated Title")
                .description("Updated Description")
                .completed(false)
                .build();

        when(taskService.update(any(UUID.class), any(TaskDTOUpdateRequest.class)))
                .thenReturn(updatedResponse);

        // Act
        taskController.update(testTaskId, updateRequest);

        // Assert
        verify(taskService).update(eq(testTaskId), argThat(dto ->
                dto.getTitle().equals("Updated Title") &&
                dto.getDescription().equals("Updated Description")
        ));
    }

    /**
     * Tests getAll() with different page sizes.
     * Verifies pagination works correctly.
     */
    @Test
    @DisplayName("Should handle different page sizes correctly")
    void testGetAllTasksWithDifferentPageSize() {
        // Arrange
        Pageable customPageable = PageRequest.of(0, 5);
        Page<TaskDTOResponse> taskPage = new PageImpl<>(
                List.of(testTaskResponse),
                customPageable,
                1
        );

        when(taskService.getAll(any(Pageable.class))).thenReturn(taskPage);

        // Act
        ResponseEntity<Page<TaskDTOResponse>> response = taskController.getAll(customPageable);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5, response.getBody().getSize());
        verify(taskService, times(1)).getAll(customPageable);
    }

    /**
     * Tests update() preserves task completion status.
     * Verifies completed field is passed correctly.
     */
    @Test
    @DisplayName("Should preserve completion status when updating task")
    void testUpdateTaskPreservesCompletionStatus() {
        // Arrange
        TaskDTOUpdateRequest completedRequest = TaskDTOUpdateRequest.builder()
                .title("Title")
                .description("Description")
                .completed(true)
                .build();

        TaskDTOResponse completedResponse = TaskDTOResponse.builder()
                .id(testTaskId)
                .title("Title")
                .description("Description")
                .completed(true)
                .build();

        when(taskService.update(any(UUID.class), any(TaskDTOUpdateRequest.class)))
                .thenReturn(completedResponse);

        // Act
        ResponseEntity<TaskDTOResponse> response = taskController.update(testTaskId, completedRequest);

        // Assert
        assertNotNull(response);
        assertTrue(response.getBody().isCompleted());
    }

}
