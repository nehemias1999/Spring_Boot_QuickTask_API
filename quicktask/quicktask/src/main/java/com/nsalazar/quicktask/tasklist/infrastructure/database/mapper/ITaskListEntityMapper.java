package com.nsalazar.quicktask.tasklist.infrastructure.database.mapper;

import com.nsalazar.quicktask.task.infrastructure.database.mapper.ITaskEntityMapper;
import com.nsalazar.quicktask.tasklist.domain.TaskList;
import com.nsalazar.quicktask.tasklist.infrastructure.database.entity.TaskListEntity;
import org.mapstruct.*;

/**
 * Mapper interface for converting between TaskList domain objects and TaskListEntity persistence objects.
 *
 * <p>Uses MapStruct to generate type-safe mapping implementations at compile-time.
 * Provides bidirectional mapping between the domain and persistence layers.
 *
 * @author nsalazar
 * @see TaskList
 * @see TaskListEntity
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {ITaskEntityMapper.class}
)
public interface ITaskListEntityMapper {

    /**
     * Converts a TaskListEntity to a TaskList domain object.
     * The tasks list is mapped using {@link ITaskEntityMapper#toTask}.
     *
     * @param taskListEntity the persistence entity
     * @return the domain TaskList object
     */
    TaskList toTaskList(TaskListEntity taskListEntity);

    /**
     * Converts a TaskList domain object to a TaskListEntity persistence object.
     * The tasks list is ignored because the relationship is managed by the owning side (TaskEntity).
     *
     * @param taskList the domain object
     * @return the persistence TaskListEntity object
     */
    @Mapping(target = "tasks", ignore = true)
    @BeanMapping(ignoreUnmappedSourceProperties = {"tasks"})
    TaskListEntity toTaskListEntity(TaskList taskList);

}


