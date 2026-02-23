package com.nsalazar.quicktask.tasklist.application.dto.mapper;

import com.nsalazar.quicktask.task.application.dto.mapper.ITaskDTOMapper;
import com.nsalazar.quicktask.tasklist.application.dto.request.TaskListDTOCreateRequest;
import com.nsalazar.quicktask.tasklist.application.dto.request.TaskListDTOUpdateRequest;
import com.nsalazar.quicktask.tasklist.application.dto.response.TaskListDTOResponse;
import com.nsalazar.quicktask.tasklist.domain.TaskList;
import org.mapstruct.*;

/**
 * Mapper interface for converting between TaskList domain objects and DTOs.
 *
 * <p>Uses MapStruct with strict unmapped policies and delegates Task mapping
 * to {@link ITaskDTOMapper}.
 *
 * @author nsalazar
 * @see TaskList
 * @see TaskListDTOCreateRequest
 * @see TaskListDTOUpdateRequest
 * @see TaskListDTOResponse
 */
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {ITaskDTOMapper.class}
)
public interface ITaskListDTOMapper {

    /**
     * Converts a create request DTO to a TaskList domain object.
     * Server-generated fields are ignored.
     *
     * @param createRequest the creation request DTO
     * @return a new TaskList domain object
     */
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "tasks", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
    })
    TaskList toTaskList(TaskListDTOCreateRequest createRequest);

    /**
     * Updates an existing TaskList domain object from an update request DTO.
     * Preserves id, tasks, and timestamps.
     *
     * @param updateRequest the update request DTO
     * @param taskList the existing TaskList to update in-place
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "tasks", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
    })
    void toTaskList(TaskListDTOUpdateRequest updateRequest, @MappingTarget TaskList taskList);

    /**
     * Converts a TaskList domain object to a response DTO.
     * Tasks are mapped using {@link ITaskDTOMapper#toTaskDTOResponse}.
     *
     * @param taskList the domain object
     * @return the response DTO
     */
    TaskListDTOResponse toTaskListDTOResponse(TaskList taskList);

}

