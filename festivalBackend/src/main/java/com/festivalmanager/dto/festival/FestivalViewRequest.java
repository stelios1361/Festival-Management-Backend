package com.festivalmanager.dto.festival;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for viewing a specific festival's details.
 * 
 * <p>This request object contains the necessary information to retrieve a
 * festival. The {@code requesterUsername} and {@code token} are optional and
 * can be null if the request is made by a visitor.</p>
 */
@Getter
@Setter
public class FestivalViewRequest {

    /** The username of the requester; optional if the requester is a visitor. */
    private String requesterUsername;

    /** The authentication token of the requester; optional if the requester is a visitor. */
    private String token;

    /** The ID of the festival to view. */
    private Long festivalId;
}
