package com.festivalmanager.dto.festival;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for deleting a festival.
 * 
 * <p>This request object carries the necessary information to delete a
 * specific festival, including authentication details of the requester.</p>
 */
@Getter
@Setter
public class FestivalDeleteRequest {

    /** The username of the user making the delete request. */
    private String requesterUsername;

    /** The authentication token of the requester. */
    private String token;

    /** The ID of the festival to be deleted. */
    private Long festivalId;
}
