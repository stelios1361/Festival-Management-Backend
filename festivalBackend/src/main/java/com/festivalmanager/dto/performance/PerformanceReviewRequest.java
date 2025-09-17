package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO used by a staff member to review a performance.
 * <p>
 * This object is sent when a staff member wants to provide a numerical score
 * and detailed reviewer comments for a performance.
 * </p>
 */
@Getter
@Setter
public class PerformanceReviewRequest {

    /**
     * The username of the staff member performing the review.
     */
    private String requesterUsername;

    /**
     * Authentication token of the requester used to validate the operation.
     */
    private String token;

    /**
     * The ID of the performance being reviewed.
     */
    private Long performanceId;

    /**
     * The numerical score assigned to the performance.
     */
    private Double score;

    /**
     * Detailed comments provided by the reviewer.
     */
    private String reviewerComments;
}
