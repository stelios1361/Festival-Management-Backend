package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class PerformanceFinalSubmissionRequest {
    private String requesterUsername;       // Artist performing the submission
    private String token;                   // Authentication token
    private Long performanceId;             // Performance to update
    private List<String> setlist;           // Final setlist
    private Set<LocalTime> rehearsalTimes; // Final rehearsal times
    private Set<LocalTime> performanceTimeSlots; // Final performance slots
}
