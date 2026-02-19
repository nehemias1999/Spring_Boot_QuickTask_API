package com.nsalazar.quicktask.task.infrastructure.database;

import com.nsalazar.quicktask.task.infrastructure.database.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * This interface provides low-level database access operations for TaskEntity objects.
 * It extends Spring Data's {@link JpaRepository} to automatically inherit CRUD operations
 * and JPA-specific functionality without requiring manual implementation.
 *
 * <strong>Purpose:</strong>
 * This repository handles direct database interactions at the persistence layer:
 * <ul>
 *   <li>Provides CRUD (Create, Read, Update, Delete) operations</li>
 *   <li>Executes JPA queries against the database</li>
 *   <li>Returns persistence layer TaskEntity objects (not domain objects)</li>
 *   <li>Manages JPA entity lifecycle and session management</li>
 * </ul>
 *
 * <strong>Generic Type Parameters:</strong>
 * <ul>
 *   <li>{@code TaskEntity} - The entity type managed by this repository</li>
 *   <li>{@code UUID} - The type of the primary key field</li>
 * </ul>
 *
 * <strong>Inherited Methods from JpaRepository:</strong>
 * JpaRepository provides the following common operations automatically:
 * <ul>
 *   <li>{@code save(TaskEntity)} - Insert or update a task entity</li>
 *   <li>{@code saveAll(Iterable)} - Batch save multiple entities</li>
 *   <li>{@code findById(UUID)} - Find a single entity by ID</li>
 *   <li>{@code findAll()} - Retrieve all entities</li>
 *   <li>{@code findAll(Pageable)} - Retrieve entities with pagination and sorting</li>
 *   <li>{@code count()} - Count total number of entities</li>
 *   <li>{@code delete(TaskEntity)} - Delete a single entity</li>
 *   <li>{@code deleteById(UUID)} - Delete entity by ID</li>
 *   <li>{@code existsById(UUID)} - Check if entity exists by ID</li>
 *   <li>{@code flush()} - Flush pending changes to the database</li>
 *   <li>{@code deleteInBatch(Iterable)} - Batch delete with single SQL statement</li>
 *   <li>{@code deleteAllInBatch()} - Delete all entities with single SQL statement</li>
 * </ul>
 *
 * <strong>Architecture Layer:</strong>
 * This repository is part of the infrastructure (persistence) layer and should only be used by
 * {@link TaskRepository}, which wraps it and handles mapping between persistence and domain layers.
 * The service layer should depend on {@link com.nsalazar.quicktask.task.domain.repository.ITaskRepository},
 * not this interface.
 *
 * <strong>Data Access Flow:</strong>
 * <pre>
 * Service Layer
 *     ↓ (uses ITaskRepository interface)
 * TaskRepository (wraps IJPATaskRepository)
 *     ↓ (delegates to)
 * IJPATaskRepository (Spring Data JPA)
 *     ↓ (executes)
 * [Database]
 * </pre>
 *
 * <strong>Entity Lifecycle Management:</strong>
 * This repository manages the JPA entity lifecycle:
 * <ul>
 *   <li><strong>Transient:</strong> New TaskEntity objects not yet persisted</li>
 *   <li><strong>Managed:</strong> Entities tracked by the persistence context</li>
 *   <li><strong>Detached:</strong> Entities no longer managed by the session</li>
 *   <li><strong>Removed:</strong> Entities marked for deletion</li>
 * </ul>
 *
 * <strong>Transaction Management:</strong>
 * All methods are transactional by default when called from the service layer.
 * The service layer's {@code @Transactional} annotation provides the transaction context.
 * Changes are automatically flushed and committed when the transaction completes.
 *
 * <strong>Lazy Loading Behavior:</strong>
 * JPA entities may have lazy-loaded associations. Care should be taken to ensure:
 * <ul>
 *   <li>Associations are loaded within the transaction context</li>
 *   <li>Lazy loading exceptions don't occur after transaction closure</li>
 *   <li>The returned entities are converted to DTOs for client use</li>
 * </ul>
 *
 * <strong>Custom Query Methods:</strong>
 * This interface currently inherits standard JPA methods. If custom queries are needed:
 * <ul>
 *   <li>Add {@code @Query} annotated methods to this interface</li>
 *   <li>Define custom JPQL or native SQL queries</li>
 *   <li>Use method naming conventions for simple derived queries</li>
 * </ul>
 *
 * <strong>Spring Stereotype Annotation:</strong>
 * The {@code @Repository} annotation:
 * <ul>
 *   <li>Marks this as a Spring-managed bean for component scanning</li>
 *   <li>Enables exception translation from database to Spring exceptions</li>
 *   <li>Allows autowiring in other Spring beans</li>
 *   <li>Facilitates testing and mocking</li>
 * </ul>
 *
 * <strong>Important Notes:</strong>
 * <ul>
 *   <li>This repository works with TaskEntity persistence objects, not Task domain objects</li>
 *   <li>Always use TaskRepository wrapper for proper layering and mapping</li>
 *   <li>Do not expose this interface to the service layer directly</li>
 *   <li>Entity objects should be converted to DTOs before returning to clients</li>
 *   <li>Database schema must have a "tbl_tasks" table with proper column definitions</li>
 * </ul>
 *
 * <strong>Usage Example (from TaskRepository):</strong>
 * <pre>
 * // Retrieve with pagination
 * Page&lt;TaskEntity&gt; page = jpaRepository.findAll(pageable);
 *
 * // Retrieve by ID
 * Optional&lt;TaskEntity&gt; entity = jpaRepository.findById(id);
 *
 * // Save new or update existing
 * TaskEntity saved = jpaRepository.save(entity);
 *
 * // Check existence
 * boolean exists = jpaRepository.existsById(id);
 *
 * // Delete by ID
 * jpaRepository.deleteById(id);
 * </pre>
 *
 * @author nsalazar
 * @see JpaRepository
 * @see TaskEntity
 * @see TaskRepository
 * @see com.nsalazar.quicktask.task.domain.repository.ITaskRepository
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface IJPATaskRepository extends JpaRepository<TaskEntity, UUID> {

}
