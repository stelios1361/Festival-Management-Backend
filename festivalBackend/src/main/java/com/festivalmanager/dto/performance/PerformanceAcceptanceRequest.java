package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO used by a festival organizer to accept (approve) a performance.
 * <p>
 * This object contains the necessary information to authorize the approval
 * of a performance within a festival.
 * </p>
 */
@Getter
@Setter
public class PerformanceAcceptanceRequest {

    /**
     * The username of the requester performing the approval.
     * Typically a festival organizer with appropriate permissions.
     */
    private String requesterUsername;

    /**
     * Authentication token of the requester used to validate the operation.
     */
    private String token;

    /**
     * The ID of the performance to be approved.
     */
    private Long performanceId;
}
