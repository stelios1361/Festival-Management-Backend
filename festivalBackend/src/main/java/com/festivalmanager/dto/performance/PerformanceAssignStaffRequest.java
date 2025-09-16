package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerformanceAssignStaffRequest {

    private String requesterUsername;  // User performing the assignment (could be festival admin)
    private String token;              // Authentication token
    private Long performanceId;        // Performance to assign staff to
    private Long staffUserId;          // User to assign as staff
}
