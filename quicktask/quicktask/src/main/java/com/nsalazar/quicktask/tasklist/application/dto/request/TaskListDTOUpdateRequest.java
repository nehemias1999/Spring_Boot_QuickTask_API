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

    /**
     * The updated name of the task list.
     *
     * <p>Optional. If provided, must not be blank. Must be unique across all task lists.
     * Maximum length: 50 characters (enforced at database level).
     */
    private String name;

    /**
     * The updated description of the task list.
     *
     * <p>Optional. If provided, must not be blank.
     * Maximum length: 200 characters (enforced at database level).
     */
    private String description;

}

