package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO used by an artist to withdraw a performance.
 * <p>
 * This object is sent when the creator or authorized user wants to remove
 * a performance from the festival workflow before it is approved or scheduled.
 * </p>
 */
@Getter
@Setter
public class PerformanceWithdrawRequest {

    /**
     * The username of the user requesting the withdrawal.
     * Typically the main artist or an authorized contributor.
     */
    private String requesterUsername;

    /**
     * Authentication token of the requester used to validate the operation.
     */
    private String token;

    /**
     * The ID of the performance to withdraw.
     */
    private Long performanceId;
}
