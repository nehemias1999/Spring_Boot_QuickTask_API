package com.nsalazar.quicktask.tasklist.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating an existing TaskList.
 *
 * <p>All fields are optional — only provided (non-null) fields will be updated.
 * At least one field must be provided; an empty DTO is not allowed.
 *
 * @author nsalazar
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskListDTOUpdateRequest {

    private String name;

    private String description;

}

