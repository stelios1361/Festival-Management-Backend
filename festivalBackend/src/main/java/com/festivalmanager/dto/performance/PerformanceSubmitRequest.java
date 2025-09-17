package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO used by an artist to submit a performance for review or approval.
 * <p>
 * This object is sent when the creator or authorized user wants to move a performance
 * from a draft state to a submitted state within the festival workflow.
 * </p>
 */
@Getter
@Setter
public class PerformanceSubmitRequest {

    /**
     * The username of the user performing the submission.
     * Typically the main artist or an authorized contributor.
     */
    private String requesterUsername;

    /**
     * Authentication token of the requester used to validate the operation.
     */
    private String token;

    /**
     * The ID of the performance being submitted.
     */
    private Long performanceId;
}
