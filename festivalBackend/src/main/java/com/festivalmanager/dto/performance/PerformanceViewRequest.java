package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO used to view the details of a performance.
 * <p>
 * Depending on the role of the requester (visitor, artist, staff, organizer),
 * the returned details may vary. Visitors only see limited information,
 * whereas privileged users can view full performance details.
 * </p>
 */
@Getter
@Setter
public class PerformanceViewRequest {

    /**
     * The username of the requester. Optional if the requester is a visitor.
     */
    private String requesterUsername;

    /**
     * Authentication token of the requester. Optional if the requester is a visitor.
     */
    private String token;

    /**
     * The ID of the performance to view.
     */
    private Long performanceId;  // Fixed typo from performancelId to performanceId
}
