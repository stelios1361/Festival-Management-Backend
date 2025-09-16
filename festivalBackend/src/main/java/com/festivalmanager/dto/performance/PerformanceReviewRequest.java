package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PerformanceReviewRequest {

    private String requesterUsername;  // Staff performing the review
    private String token;              // Authentication token
    private Long performanceId;        // Performance to review

    private Double score;              // Numerical score
    private String reviewerComments;   // Detailed comments
}