package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

/**
 * Request DTO used by an artist to submit the final version of a performance.
 * <p>
 * This object is sent when the creator of a performance wants to finalize
 * the setlist, rehearsal times, and preferred performance time slots.
 * </p>
 */
@Getter
@Setter
public class PerformanceFinalSubmissionRequest {

    /**
     * The username of the artist performing the final submission.
     */
    private String requesterUsername;

    /**
     * Authentication token of the requester used to validate the operation.
     */
    private String token;

    /**
     * The ID of the performance being updated/submitted.
     */
    private Long performanceId;

    /**
     * The final setlist of songs for the performance.
     */
    private List<String> setlist;

    /**
     * The final preferred rehearsal times for the performance.
     */
    private Set<LocalTime> rehearsalTimes;

    /**
     * The final preferred performance time slots.
     */
    private Set<LocalTime> performanceTimeSlots;
}
