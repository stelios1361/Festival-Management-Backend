package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerformanceApprovalRequest {

    private String requesterUsername;  // Festival organizer performing the approval
    private String token;              // Authentication token
    private Long performanceId;        // Performance to approve
}
