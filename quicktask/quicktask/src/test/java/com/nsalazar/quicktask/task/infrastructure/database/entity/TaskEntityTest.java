package com.nsalazar.quicktask.task.infrastructure.database.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TaskEntity Tests")
class TaskEntityTest {

    @Test
    @DisplayName("should create task with all fields")
    void createsTaskWithAllFields() {
        UUID id = UUID.randomUUID();
        String title = "Complete project";
        String description = "Finish the Spring Boot project by Friday";
        boolean completed = false;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now().plusHours(1);

        TaskEntity task = new TaskEntity(id, title, description, completed, createdAt, updatedAt, null);

        assertEquals(id, task.getId());
        assertEquals(title, task.getTitle());
        assertEquals(description, task.getDescription());
        assertFalse(task.isCompleted());
        assertEquals(createdAt, task.getCreatedAt());
        assertEquals(updatedAt, task.getUpdatedAt());
    }

    @Test
    @DisplayName("should create task with default constructor")
    void createsTaskWithDefaultConstructor() {
        TaskEntity task = new TaskEntity();

        assertNull(task.getId());
        assertNull(task.getTitle());
        assertNull(task.getDescription());
        assertFalse(task.isCompleted());
        assertNull(task.getCreatedAt());
        assertNull(task.getUpdatedAt());
    }

    @Test
    @DisplayName("should set and get id")
    void setsAndGetsId() {
        TaskEntity task = new TaskEntity();
        UUID id = UUID.randomUUID();

        task.setId(id);

        assertEquals(id, task.getId());
    }

    @Test
    @DisplayName("should set and get title")
    void setsAndGetsTitle() {
        TaskEntity task = new TaskEntity();
        String title = "Buy groceries";

        task.setTitle(title);

        assertEquals(title, task.getTitle());
    }

    @Test
    @DisplayName("should set and get description")
    void setsAndGetsDescription() {
        TaskEntity task = new TaskEntity();
        String description = "Buy milk, eggs, and bread";

        task.setDescription(description);

        assertEquals(description, task.getDescription());
    }

    @Test
    @DisplayName("should set and get completed status")
    void setsAndGetsCompletedStatus() {
        TaskEntity task = new TaskEntity();

        task.setCompleted(false);
        assertFalse(task.isCompleted());

        task.setCompleted(true);
        assertTrue(task.isCompleted());
    }

    @Test
    @DisplayName("should set and get createdAt timestamp")
    void setsAndGetsCreatedAtTimestamp() {
        TaskEntity task = new TaskEntity();
        LocalDateTime createdAt = LocalDateTime.of(2026, 2, 19, 10, 30, 0);

        task.setCreatedAt(createdAt);

        assertEquals(createdAt, task.getCreatedAt());
    }

    @Test
    @DisplayName("should set and get updatedAt timestamp")
    void setsAndGetsUpdatedAtTimestamp() {
        TaskEntity task = new TaskEntity();
        LocalDateTime updatedAt = LocalDateTime.of(2026, 2, 19, 15, 45, 30);

        task.setUpdatedAt(updatedAt);

        assertEquals(updatedAt, task.getUpdatedAt());
    }

    @Test
    @DisplayName("should allow updatedAt to be null")
    void allowsUpdatedAtToBeNull() {
        TaskEntity task = new TaskEntity();
        task.setUpdatedAt(LocalDateTime.now());

        task.setUpdatedAt(null);

        assertNull(task.getUpdatedAt());
    }

    @Test
    @DisplayName("should support title with maximum length")
    void supportsTitleWithMaximumLength() {
        TaskEntity task = new TaskEntity();
        String maxTitle = "a".repeat(50);

        task.setTitle(maxTitle);

        assertEquals(maxTitle, task.getTitle());
        assertEquals(50, task.getTitle().length());
    }

    @Test
    @DisplayName("should support description with maximum length")
    void supportsDescriptionWithMaximumLength() {
        TaskEntity task = new TaskEntity();
        String maxDescription = "a".repeat(200);

        task.setDescription(maxDescription);

        assertEquals(maxDescription, task.getDescription());
        assertEquals(200, task.getDescription().length());
    }

    @Test
    @DisplayName("should allow title with special characters")
    void allowsTitleWithSpecialCharacters() {
        TaskEntity task = new TaskEntity();
        String titleWithSpecialChars = "Task: Complete @Home #Urgent";

        task.setTitle(titleWithSpecialChars);

        assertEquals(titleWithSpecialChars, task.getTitle());
    }

    @Test
    @DisplayName("should allow description with special characters")
    void allowsDescriptionWithSpecialCharacters() {
        TaskEntity task = new TaskEntity();
        String descriptionWithSpecialChars = "Complete: @Home #Urgent, deadline: 2026-02-20!";

        task.setDescription(descriptionWithSpecialChars);

        assertEquals(descriptionWithSpecialChars, task.getDescription());
    }

    @Test
    @DisplayName("should allow title with unicode characters")
    void allowsTitleWithUnicodeCharacters() {
        TaskEntity task = new TaskEntity();
        String unicodeTitle = "完成项目 🚀";

        task.setTitle(unicodeTitle);

        assertEquals(unicodeTitle, task.getTitle());
    }

    @Test
    @DisplayName("should allow description with unicode characters")
    void allowsDescriptionWithUnicodeCharacters() {
        TaskEntity task = new TaskEntity();
        String unicodeDescription = "完成 Spring Boot 项目 🎯";

        task.setDescription(unicodeDescription);

        assertEquals(unicodeDescription, task.getDescription());
    }

    @Test
    @DisplayName("should handle empty string title")
    void handlesEmptyStringTitle() {
        TaskEntity task = new TaskEntity();
        String emptyTitle = "";

        task.setTitle(emptyTitle);

        assertEquals("", task.getTitle());
    }

    @Test
    @DisplayName("should handle empty string description")
    void handlesEmptyStringDescription() {
        TaskEntity task = new TaskEntity();
        String emptyDescription = "";

        task.setDescription(emptyDescription);

        assertEquals("", task.getDescription());
    }

    @Test
    @DisplayName("should handle title with whitespace")
    void handlesTitleWithWhitespace() {
        TaskEntity task = new TaskEntity();
        String titleWithWhitespace = "  Complete project  ";

        task.setTitle(titleWithWhitespace);

        assertEquals("  Complete project  ", task.getTitle());
    }

    @Test
    @DisplayName("should handle description with newlines")
    void handlesDescriptionWithNewlines() {
        TaskEntity task = new TaskEntity();
        String descriptionWithNewlines = "Line 1\nLine 2\nLine 3";

        task.setDescription(descriptionWithNewlines);

        assertEquals("Line 1\nLine 2\nLine 3", task.getDescription());
    }

    @Test
    @DisplayName("should track multiple timestamp updates")
    void tracksMultipleTimestampUpdates() {
        TaskEntity task = new TaskEntity();
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 1, 10, 0, 0);
        LocalDateTime firstUpdate = LocalDateTime.of(2026, 1, 15, 14, 30, 0);
        LocalDateTime secondUpdate = LocalDateTime.of(2026, 2, 1, 18, 45, 0);

        task.setCreatedAt(createdAt);
        task.setUpdatedAt(firstUpdate);

        assertEquals(createdAt, task.getCreatedAt());
        assertEquals(firstUpdate, task.getUpdatedAt());

        task.setUpdatedAt(secondUpdate);

        assertEquals(createdAt, task.getCreatedAt());
        assertEquals(secondUpdate, task.getUpdatedAt());
    }

    @Test
    @DisplayName("should generate unique ids for different instances")
    void generatesUniqueIdsForDifferentInstances() {
        TaskEntity task1 = new TaskEntity();
        TaskEntity task2 = new TaskEntity();
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        task1.setId(id1);
        task2.setId(id2);

        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    @DisplayName("should preserve task state through multiple operations")
    void preservesTaskStateThroughMultipleOperations() {
        TaskEntity task = new TaskEntity();
        UUID id = UUID.randomUUID();
        String title = "Important task";
        String description = "This is an important task";
        LocalDateTime createdAt = LocalDateTime.now();

        task.setId(id);
        task.setTitle(title);
        task.setDescription(description);
        task.setCreatedAt(createdAt);
        task.setCompleted(false);

        task.setCompleted(true);
        task.setUpdatedAt(LocalDateTime.now());

        assertEquals(id, task.getId());
        assertEquals(title, task.getTitle());
        assertEquals(description, task.getDescription());
        assertTrue(task.isCompleted());
        assertEquals(createdAt, task.getCreatedAt());
        assertNotNull(task.getUpdatedAt());
    }

    @Test
    @DisplayName("should transition task from incomplete to complete")
    void transitionsTaskFromIncompleteToComplete() {
        TaskEntity task = new TaskEntity();
        task.setCompleted(false);

        assertFalse(task.isCompleted());

        task.setCompleted(true);

        assertTrue(task.isCompleted());
    }

    @Test
    @DisplayName("should allow toggling completed status multiple times")
    void allowsTogglingCompletedStatusMultipleTimes() {
        TaskEntity task = new TaskEntity();
        task.setCompleted(false);
        assertFalse(task.isCompleted());

        task.setCompleted(true);
        assertTrue(task.isCompleted());

        task.setCompleted(false);
        assertFalse(task.isCompleted());

        task.setCompleted(true);
        assertTrue(task.isCompleted());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("should set completed status to various values")
    void setsCompletedStatusToVariousValues(boolean status) {
        TaskEntity task = new TaskEntity();

        task.setCompleted(status);

        assertEquals(status, task.isCompleted());
    }

    @Test
    @DisplayName("should handle tasks with very long titles near limit")
    void handlesTasksWithVeryLongTitlesNearLimit() {
        TaskEntity task = new TaskEntity();
        String longTitle = "a".repeat(49) + "b";

        task.setTitle(longTitle);

        assertEquals(50, task.getTitle().length());
        assertTrue(task.getTitle().endsWith("b"));
    }

    @Test
    @DisplayName("should handle tasks with very long descriptions near limit")
    void handlesTasksWithVeryLongDescriptionsNearLimit() {
        TaskEntity task = new TaskEntity();
        String longDescription = "a".repeat(199) + "z";

        task.setDescription(longDescription);

        assertEquals(200, task.getDescription().length());
        assertTrue(task.getDescription().endsWith("z"));
    }

    @Test
    @DisplayName("should handle null createdAt and updatedAt initially")
    void handlesNullTimestampsInitially() {
        TaskEntity task = new TaskEntity();

        assertNull(task.getCreatedAt());
        assertNull(task.getUpdatedAt());
    }

    @Test
    @DisplayName("should maintain createdAt immutability pattern")
    void maintainsCreatedAtImmutabilityPattern() {
        TaskEntity task = new TaskEntity();
        LocalDateTime createdAt = LocalDateTime.of(2026, 2, 19, 10, 0, 0);
        task.setCreatedAt(createdAt);

        LocalDateTime originalCreatedAt = task.getCreatedAt();
        task.setUpdatedAt(LocalDateTime.now());

        assertEquals(originalCreatedAt, task.getCreatedAt());
    }
}

