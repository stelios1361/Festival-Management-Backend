package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO used to assign a staff member to a performance.
 * <p>
 * This object is sent by a festival organizer or administrator to assign
 * a user as the stage manager or responsible staff for a specific performance.
 * </p>
 */
@Getter
@Setter
public class PerformanceAssignStaffRequest {

    /**
     * The username of the requester performing the staff assignment.
     * Typically a festival organizer or admin with appropriate permissions.
     */
    private String requesterUsername;

    /**
     * Authentication token of the requester used to validate the operation.
     */
    private String token;

    /**
     * The ID of the performance for which a staff member is being assigned.
     */
    private Long performanceId;

    /**
     * The user ID of the staff member to assign to the performance.
     */
    private Long staffUserId;
}
