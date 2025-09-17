package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO used by a festival organizer to approve a performance.
 * <p>
 * This object is sent when an organizer wants to mark a performance as approved
 * within a festival management workflow.
 * </p>
 */
@Getter
@Setter
public class PerformanceApprovalRequest {

    /**
     * The username of the requester performing the approval.
     * Typically a festival organizer with sufficient permissions.
     */
    private String requesterUsername;

    /**
     * Authentication token of the requester used to validate the operation.
     */
    private String token;

    /**
     * The ID of the performance that is being approved.
     */
    private Long performanceId;
}
