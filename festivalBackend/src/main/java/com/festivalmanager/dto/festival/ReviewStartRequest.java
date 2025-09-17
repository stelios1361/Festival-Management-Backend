package com.festivalmanager.dto.festival;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for starting the review phase of a festival.
 * 
 * <p>This request object contains the necessary information to initiate the
 * review process for a specific festival, including authentication details
 * of the requester.</p>
 */
@Getter
@Setter
public class ReviewStartRequest {

    /** The username of the user initiating the review phase. */
    private String requesterUsername;

    /** The authentication token of the requester. */
    private String token;

    /** The ID of the festival for which the review phase is starting. */
    private Long festivalId;
}
