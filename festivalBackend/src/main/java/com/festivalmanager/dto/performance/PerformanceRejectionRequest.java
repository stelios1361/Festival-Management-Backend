package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerformanceRejectionRequest {

    private String requesterUsername;  // Festival organizer performing the rejection
    private String token;              // Authentication token
    private Long performanceId;        // Performance to reject
    private String rejectionReason;    // Mandatory reason
}