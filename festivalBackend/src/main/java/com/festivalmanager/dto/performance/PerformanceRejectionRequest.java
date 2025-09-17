package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO used by a festival organizer to reject a performance.
 * <p>
 * This object is sent when an organizer wants to mark a performance as rejected
 * within the festival management workflow. A rejection reason must be provided.
 * </p>
 */
@Getter
@Setter
public class PerformanceRejectionRequest {

    /**
     * The username of the requester performing the rejection.
     * Typically a festival organizer with appropriate permissions.
     */
    private String requesterUsername;

    /**
     * Authentication token of the requester used to validate the operation.
     */
    private String token;

    /**
     * The ID of the performance that is being rejected.
     */
    private Long performanceId;

    /**
     * The mandatory reason explaining why the performance is rejected.
     */
    private String rejectionReason;
}
