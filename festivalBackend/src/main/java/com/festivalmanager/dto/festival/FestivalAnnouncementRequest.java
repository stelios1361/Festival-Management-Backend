package com.festivalmanager.dto.festival;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for announcing a festival publicly.
 * 
 * <p>This request object carries the necessary information to make a festival
 * announcement, including authentication details of the requester.</p>
 */
@Getter
@Setter
public class FestivalAnnouncementRequest {

    /** The username of the user making the announcement request. */
    private String requesterUsername;

    /** The authentication token of the requester. */
    private String token;

    /** The ID of the festival to be announced. */
    private Long festivalId;
}
