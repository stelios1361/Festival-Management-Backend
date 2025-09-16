package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerformanceSubmitRequest {

    private String requesterUsername;  // User performing the submission
    private String token;              // Authentication token
    private Long performanceId;        // Performance to submit
}
