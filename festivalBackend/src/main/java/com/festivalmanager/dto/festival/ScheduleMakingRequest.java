package com.festivalmanager.dto.festival;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for starting the schedule-making phase of a festival.
 * 
 * <p>This request object contains the necessary information to initiate the
 * scheduling process for a specific festival, including authentication
 * details of the requester.</p>
 */
@Getter
@Setter
public class ScheduleMakingRequest {

    /** The username of the user initiating the schedule-making phase. */
    private String requesterUsername;

    /** The authentication token of the requester. */
    private String token;

    /** The ID of the festival for which the schedule-making phase is starting. */
    private Long festivalId;
}
