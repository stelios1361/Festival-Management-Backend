package com.festivalmanager.dto.festival;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for initiating the decision-making phase of a festival.
 * 
 * <p>This request object contains the necessary information to start the process
 * of decision for a specific festival, including authentication details of the requester.</p>
 */
@Getter
@Setter
public class DecisionMakingRequest {

    /** The username of the user making the decision request. */
    private String requesterUsername;

    /** The authentication token of the requester. */
    private String token;

    /** The ID of the festival for which the decision-making process is to be applied. */
    private Long festivalId;
}
