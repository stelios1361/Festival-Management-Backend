package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerformanceViewRequest {

    /**
     * The username of the requester; optional if the requester is a visitor.
     */
    private String requesterUsername;

    /**
     * The authentication token of the requester; optional if the requester is a
     * visitor.
     */
    private String token;

    /**
     * The ID of the performance to view.
     */
    private Long performancelId;
}
