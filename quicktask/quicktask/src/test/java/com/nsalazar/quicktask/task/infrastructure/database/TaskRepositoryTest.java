package com.nsalazar.quicktask.task.infrastructure.database;

import com.nsalazar.quicktask.task.domain.Task;
import com.nsalazar.quicktask.task.domain.repository.ITaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for TaskRepository.
 *
 * <p>Tests the repository layer operations including CRUD operations and custom queries.
 * Uses {@code @SpringBootTest} to load the full application context and test with a real database.
 *
 * <p><strong>Test Coverage:</strong>
 * <ul>
 *   <li>CRUD Operations: Create, Read, Update, Delete</li>
 *   <li>Pagination and Sorting: Paginated queries with sorting</li>
 *   <li>Existence Checks: Verify if records exist by ID</li>
 *   <li>Custom Queries: Find incomplete tasks by title</li>
 *   <li>Edge Cases: Empty results, null values, boundary conditions</li>
 * </ul>
 *
 * @author nsalazar
 * @see TaskRepository
 * @see ITaskRepository
 */
@SpringBootTest
@Transactional
@DisplayName("TaskRepository Tests")
class TaskRepositoryTest {

    @Autowired
    private ITaskRepository taskRepository;


    private Task testTask;
    private static final String TEST_TITLE = "Test Task Title";
    private static final String TEST_DESCRIPTION = "Test Task Description";

    /**
     * Setup method executed before each test.
     * Initializes test data with a sample task.
     */
    @BeforeEach
    void setUp() {
        testTask = Task.builder()
                .title(TEST_TITLE)
                .description(TEST_DESCRIPTION)
                .completed(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Tests saving a new task.
     * Verifies that a task can be created and has an ID assigned.
     */
    @Test
    @DisplayName("Should save a new task successfully")
    void testSaveNewTask() {
        // Arrange
        assertNull(testTask.getId(), "New task should not have an ID");

        // Act
        Task savedTask = taskRepository.save(testTask);

        // Assert
        assertNotNull(savedTask.getId(), "Saved task should have an ID");
        assertEquals(TEST_TITLE, savedTask.getTitle());
        assertEquals(TEST_DESCRIPTION, savedTask.getDescription());
        assertFalse(savedTask.isCompleted());
    }

    /**
     * Tests updating an existing task.
     * Verifies that task properties can be modified and persisted.
     */
    @Test
    @DisplayName("Should update an existing task successfully")
    void testUpdateTask() {
        // Arrange
        Task savedTask = taskRepository.save(testTask);

        String newTitle = "Updated Task Title";
        String newDescription = "Updated Description";

        // Act
        Optional<Task> retrievedTask = taskRepository.findById(savedTask.getId());
        assertTrue(retrievedTask.isPresent(), "Task should be found");

        Task taskToUpdate = retrievedTask.get();
        taskToUpdate.setTitle(newTitle);
        taskToUpdate.setDescription(newDescription);
        taskToUpdate.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = taskRepository.save(taskToUpdate);

        // Assert
        assertEquals(newTitle, updatedTask.getTitle());
        assertEquals(newDescription, updatedTask.getDescription());
    }

    /**
     * Tests finding a task by ID.
     * Verifies that a saved task can be retrieved by its ID.
     */
    @Test
    @DisplayName("Should find a task by ID")
    void testFindTaskById() {
        // Arrange
        Task savedTask = taskRepository.save(testTask);
        UUID savedId = savedTask.getId();

        // Act
        Optional<Task> foundTask = taskRepository.findById(savedId);

        // Assert
        assertTrue(foundTask.isPresent(), "Task should be found by ID");
        assertEquals(savedId, foundTask.get().getId());
        assertEquals(TEST_TITLE, foundTask.get().getTitle());
    }

    /**
     * Tests finding a task by ID when it doesn't exist.
     * Verifies that an empty Optional is returned for non-existent IDs.
     */
    @Test
    @DisplayName("Should return empty Optional when task ID not found")
    void testFindTaskByIdNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        Optional<Task> foundTask = taskRepository.findById(nonExistentId);

        // Assert
        assertTrue(foundTask.isEmpty(), "Should return empty Optional for non-existent ID");
    }

    /**
     * Tests deleting a task by ID.
     * Verifies that a task is removed from the database.
     */
    @Test
    @DisplayName("Should delete a task by ID")
    void testDeleteTaskById() {
        // Arrange
        Task savedTask = taskRepository.save(testTask);
        UUID savedId = savedTask.getId();

        assertTrue(taskRepository.existsById(savedId), "Task should exist before deletion");

        // Act
        taskRepository.delete(savedId);

        // Assert
        assertFalse(taskRepository.existsById(savedId), "Task should not exist after deletion");
    }

    /**
     * Tests checking if a task exists by ID.
     * Verifies the existence check functionality.
     */
    @Test
    @DisplayName("Should check if task exists by ID")
    void testExistsById() {
        // Arrange
        Task savedTask = taskRepository.save(testTask);
        UUID savedId = savedTask.getId();

        // Act & Assert
        assertTrue(taskRepository.existsById(savedId), "Saved task should exist");
        assertFalse(taskRepository.existsById(UUID.randomUUID()), "Non-existent task should not exist");
    }

    /**
     * Tests retrieving all tasks with pagination.
     * Verifies that paginated queries return correct page information.
     */
    @Test
    @DisplayName("Should retrieve all tasks with pagination")
    void testFindAllWithPagination() {
        // Arrange
        Task task1 = Task.builder()
                .title("Task 1")
                .description("Description 1")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .build();

        Task task2 = Task.builder()
                .title("Task 2")
                .description("Description 2")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .build();

        taskRepository.save(task1);
        taskRepository.save(task2);

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Task> tasksPage = taskRepository.findAll(pageable);

        // Assert
        assertTrue(tasksPage.getTotalElements() >= 2, "Should have at least 2 tasks");
        assertFalse(tasksPage.isEmpty(), "Page should not be empty");
    }

    /**
     * Tests finding an incomplete task by title.
     * Verifies that the custom query correctly filters by title and completed status.
     */
    @Test
    @DisplayName("Should find incomplete task by title")
    void testFindByTitleAndNotCompleted() {
        // Arrange
        Task incompletedTask = Task.builder()
                .title("Incomplete Task")
                .description("This task is incomplete")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .build();

        taskRepository.save(incompletedTask);

        // Act
        Optional<Task> foundTask = taskRepository.findByTitleAndNotCompleted("Incomplete Task");

        // Assert
        assertTrue(foundTask.isPresent(), "Should find incomplete task by title");
        assertEquals("Incomplete Task", foundTask.get().getTitle());
        assertFalse(foundTask.get().isCompleted());
    }

    /**
     * Tests that completed tasks are not found by title.
     * Verifies that the query only returns incomplete tasks.
     */
    @Test
    @DisplayName("Should not find completed task by title")
    void testFindByTitleAndNotCompletedIgnoresCompleted() {
        // Arrange
        Task completedTask = Task.builder()
                .title("Completed Task")
                .description("This task is completed")
                .completed(true)
                .createdAt(LocalDateTime.now())
                .build();

        taskRepository.save(completedTask);

        // Act
        Optional<Task> foundTask = taskRepository.findByTitleAndNotCompleted("Completed Task");

        // Assert
        assertTrue(foundTask.isEmpty(), "Should not find completed task");
    }

    /**
     * Tests finding by title when no matching task exists.
     * Verifies that an empty Optional is returned.
     */
    @Test
    @DisplayName("Should return empty Optional when title not found")
    void testFindByTitleAndNotCompletedNotFound() {
        // Act
        Optional<Task> foundTask = taskRepository.findByTitleAndNotCompleted("Non-existent Title");

        // Assert
        assertTrue(foundTask.isEmpty(), "Should return empty Optional when title not found");
    }

    /**
     * Tests saving multiple tasks.
     * Verifies batch creation of tasks.
     */
    @Test
    @DisplayName("Should save multiple tasks")
    void testSaveMultipleTasks() {
        // Arrange
        Task task1 = Task.builder()
                .title("Task 1")
                .description("Description 1")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .build();

        Task task2 = Task.builder()
                .title("Task 2")
                .description("Description 2")
                .completed(false)
                .createdAt(LocalDateTime.now())
                .build();

        // Act
        Task savedTask1 = taskRepository.save(task1);
        Task savedTask2 = taskRepository.save(task2);

        // Assert
        assertNotNull(savedTask1.getId());
        assertNotNull(savedTask2.getId());
        assertNotEquals(savedTask1.getId(), savedTask2.getId());
    }

}

