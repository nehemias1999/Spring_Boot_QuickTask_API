package com.nsalazar.quicktask.task.infrastructure.database.mapper;

import com.nsalazar.quicktask.task.domain.Task;
import com.nsalazar.quicktask.task.infrastructure.database.entity.TaskEntity;
import org.mapstruct.*;

/**
 * Mapper interface for converting between Task domain objects and TaskEntity persistence objects.
 *
 * <p>This interface uses MapStruct to generate type-safe mapping implementations at compile-time.
 * It provides bidirectional mapping between the domain layer Task model and the persistence layer
 * TaskEntity model, enabling clean separation of concerns between business logic and data storage.
 *
 * <p><strong>Purpose:</strong>
 * The mapper acts as a bridge between the domain and persistence layers, converting:
 * <ul>
 *   <li>{@link TaskEntity} (database representation) ↔ {@link Task} (domain model)</li>
 *   <li>Ensures the domain layer remains independent of persistence concerns</li>
 *   <li>Provides a single point for coordinating data transformations</li>
 *   <li>Enables independent evolution of domain and persistence schemas</li>
 * </ul>
 *
 * <p><strong>MapStruct Configuration:</strong>
 * <ul>
 *   <li>Component Model: Spring - Generated implementation is a Spring-managed bean</li>
 *   <li>Unmapped Source Policy: ERROR - Fails compilation if source properties are not mapped</li>
 *   <li>Unmapped Target Policy: ERROR - Fails compilation if target properties are not mapped</li>
 * </ul>
 *
 * <p><strong>Mapping Strategy:</strong>
 * <ul>
 *   <li>All fields are mapped by name matching (case-insensitive)</li>
 *   <li>UUID id field maps directly between layers</li>
 *   <li>All primitive types (String, boolean, LocalDateTime) map automatically</li>
 *   <li>Type safety is enforced at compile-time</li>
 *   <li>No runtime reflection for field access</li>
 * </ul>
 *
 * <p><strong>Usage Location in Architecture:</strong>
 * <ul>
 *   <li>TaskRepository.java - Used to convert TaskEntity to Task after database queries</li>
 *   <li>TaskRepository.java - Used to convert Task to TaskEntity before persistence</li>
 *   <li>Service layer indirectly benefits from clean separation enabled by this mapper</li>
 * </ul>
 *
 * <p><strong>Data Flow Example:</strong>
 * <pre>
 * Create Flow:
 * TaskDTOCreateRequest
 *     ↓ (ITaskDTOMapper)
 * Task (domain model)
 *     ↓ (ITaskEntityMapper.toTaskEntity)
 * TaskEntity (persistence model)
 *     ↓ (JPA save)
 * [Database]
 *
 * Read Flow:
 * [Database]
 *     ↓ (JPA query)
 * TaskEntity (persistence model)
 *     ↓ (ITaskEntityMapper.toTask)
 * Task (domain model)
 *     ↓ (ITaskDTOMapper)
 * TaskDTOResponse (API response)
 * </pre>
 *
 * <p><strong>Compilation Safety:</strong>
 * The ERROR policies ensure:
 * <ul>
 *   <li>If TaskEntity adds a new field, compilation fails until mapping is defined</li>
 *   <li>If Task adds a new field, compilation fails until mapping is defined</li>
 *   <li>Prevents silent bugs from unmapped properties</li>
 *   <li>Provides immediate feedback during development</li>
 * </ul>
 *
 * <p><strong>Generated Implementation:</strong>
 * MapStruct generates an implementation class (ITaskEntityMapperImpl) that:
 * <ul>
 *   <li>Implements this interface</li>
 *   <li>Uses direct field access and assignment (no reflection)</li>
 *   <li>Can be debugged and inspected in the target directory</li>
 *   <li>Is registered as a Spring bean for dependency injection</li>
 * </ul>
 *
 * @author nsalazar
 * @see Task
 * @see TaskEntity
 * @see com.nsalazar.quicktask.task.infrastructure.database.TaskRepository
 * @see com.nsalazar.quicktask.task.application.dto.mapper.ITaskDTOMapper
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ITaskEntityMapper {

    /**
     * Converts a persistence TaskEntity object to a domain Task object.
     *
     * <p><strong>Purpose:</strong>
     * This method is used after retrieving task data from the database. The TaskEntity
     * represents the raw database schema, while the Task represents the domain model.
     * This conversion enables the domain layer to work with clean, framework-independent objects.
     *
     * <p><strong>When Used:</strong>
     * <ul>
     *   <li>TaskRepository.findById() - After retrieving a single task from the database</li>
     *   <li>TaskRepository.findAll() - After retrieving a page of tasks from the database</li>
     *   <li>Any Spring Data JPA query result processing</li>
     * </ul>
     *
     * <p><strong>Field Mapping Behavior:</strong>
     * All fields are mapped by name to matching properties:
     * <ul>
     *   <li>{@code id} (UUID) → {@code id} (UUID)</li>
     *   <li>{@code title} (String) → {@code title} (String)</li>
     *   <li>{@code description} (String) → {@code description} (String)</li>
     *   <li>{@code completed} (boolean) → {@code completed} (boolean)</li>
     *   <li>{@code createdAt} (LocalDateTime) → {@code createdAt} (LocalDateTime)</li>
     *   <li>{@code updatedAt} (LocalDateTime) → {@code updatedAt} (LocalDateTime)</li>
     * </ul>
     *
     * <p><strong>Data Integrity:</strong>
     * <ul>
     *   <li>All data from the entity is copied to the domain object</li>
     *   <li>No data transformation or filtering occurs</li>
     *   <li>null values are preserved (e.g., updatedAt can be null)</li>
     *   <li>Collections and complex types are handled (if any)</li>
     * </ul>
     *
     * <p><strong>Performance Characteristics:</strong>
     * <ul>
     *   <li>O(1) operation - constant time regardless of data size</li>
     *   <li>No database queries or I/O operations</li>
     *   <li>Direct field assignment, no reflection</li>
     *   <li>Thread-safe (no shared mutable state)</li>
     * </ul>
     *
     * <p><strong>Null Handling:</strong>
     * <ul>
     *   <li>If taskEntity parameter is null, the method will throw a NullPointerException</li>
     *   <li>Individual null field values (like updatedAt) are preserved in the result</li>
     *   <li>The returned Task object is never null if conversion succeeds</li>
     * </ul>
     *
     * <p><strong>Example Usage:</strong>
     * <pre>
     * // In TaskRepository.findById():
     * Optional&lt;TaskEntity&gt; entityOpt = jpaRepository.findById(id);
     * Optional&lt;Task&gt; taskOpt = entityOpt.map(mapper::toTask);
     * </pre>
     *
     * @param taskEntity the persistence {@link TaskEntity} object retrieved from the database.
     *                   Must not be null.
     * @return a new {@link Task} domain object with all properties from the entity.
     *         Never null if the input is not null.
     * @throws NullPointerException if taskEntity is null
     *
     * @see TaskEntity
     * @see Task
     */
    Task toTask(TaskEntity taskEntity);

    /**
     * Converts a domain Task object to a persistence TaskEntity object.
     *
     * <p><strong>Purpose:</strong>
     * This method is used before persisting task data to the database. The Task object
     * represents the domain model, while the TaskEntity represents the database schema.
     * This conversion prepares domain objects for persistence layer operations.
     *
     * <p><strong>When Used:</strong>
     * <ul>
     *   <li>TaskRepository.save() - Before inserting or updating a task in the database</li>
     *   <li>Service layer before calling repository.save()</li>
     *   <li>Any operation that needs to persist domain objects</li>
     * </ul>
     *
     * <p><strong>Field Mapping Behavior:</strong>
     * All fields are mapped by name to matching properties:
     * <ul>
     *   <li>{@code id} (UUID) → {@code id} (UUID)</li>
     *   <li>{@code title} (String) → {@code title} (String)</li>
     *   <li>{@code description} (String) → {@code description} (String)</li>
     *   <li>{@code completed} (boolean) → {@code completed} (boolean)</li>
     *   <li>{@code createdAt} (LocalDateTime) → {@code createdAt} (LocalDateTime)</li>
     *   <li>{@code updatedAt} (LocalDateTime) → {@code updatedAt} (LocalDateTime)</li>
     * </ul>
     *
     * <p><strong>Database Constraints:</strong>
     * The returned TaskEntity will have all fields set. JPA/database constraints are enforced:
     * <ul>
     *   <li>id: Can be null for new inserts (database will generate UUID)</li>
     *   <li>title: Must not be null (NOT NULL constraint at database level)</li>
     *   <li>description: Must not be null (NOT NULL constraint at database level)</li>
     *   <li>completed: Must not be null (NOT NULL with DEFAULT FALSE)</li>
     *   <li>createdAt: Must not be null (NOT NULL constraint at database level)</li>
     *   <li>updatedAt: Can be null (nullable column)</li>
     * </ul>
     *
     * <p><strong>ID Generation for New Tasks:</strong>
     * <ul>
     *   <li>For new tasks, the Task.id may be null</li>
     *   <li>The TaskEntity.id will also be null</li>
     *   <li>The database will generate a UUID during INSERT via @GeneratedValue(GenerationType.UUID)</li>
     *   <li>The generated UUID can be retrieved after the INSERT completes</li>
     * </ul>
     *
     * <p><strong>Performance Characteristics:</strong>
     * <ul>
     *   <li>O(1) operation - constant time regardless of data size</li>
     *   <li>No database queries or I/O operations</li>
     *   <li>Direct field assignment, no reflection</li>
     *   <li>Thread-safe (no shared mutable state)</li>
     * </ul>
     *
     * <p><strong>Data Integrity:</strong>
     * <ul>
     *   <li>All data from the domain object is copied to the entity</li>
     *   <li>No data transformation or filtering occurs</li>
     *   <li>null values are preserved (e.g., updatedAt can be null for new tasks)</li>
     *   <li>The returned entity is ready for persistence operations</li>
     * </ul>
     *
     * <p><strong>Null Handling:</strong>
     * <ul>
     *   <li>If task parameter is null, the method will throw a NullPointerException</li>
     *   <li>Individual null field values (like updatedAt) are preserved in the result</li>
     *   <li>The returned TaskEntity object is never null if conversion succeeds</li>
     * </ul>
     *
     * <p><strong>Transactional Context:</strong>
     * This conversion happens within the transaction context of the repository operation.
     * The entity returned by this method should be used within the same transaction.
     *
     * <p><strong>Example Usage:</strong>
     * <pre>
     * // In TaskRepository.save():
     * Task task = taskDomainObject;
     * TaskEntity entity = mapper.toTaskEntity(task);
     * TaskEntity saved = jpaRepository.save(entity);
     * Task result = mapper.toTask(saved);
     * </pre>
     *
     * @param task the domain {@link Task} object to convert.
     *             Must not be null.
     * @return a new {@link TaskEntity} persistence object with all properties from the task.
     *         Ready to be passed to Spring Data JPA save() method.
     *         Never null if the input is not null.
     * @throws NullPointerException if task is null
     *
     * @see Task
     * @see TaskEntity
     */
    TaskEntity toTaskEntity(Task task);

}

