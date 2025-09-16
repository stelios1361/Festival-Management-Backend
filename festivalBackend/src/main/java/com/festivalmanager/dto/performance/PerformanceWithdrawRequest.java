package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerformanceWithdrawRequest {

    private String requesterUsername;  // User requesting withdrawal
    private String token;              // Authentication token
    private Long performanceId;        // Performance to withdraw
}
