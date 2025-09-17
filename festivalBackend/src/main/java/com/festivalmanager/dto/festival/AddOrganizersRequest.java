package com.festivalmanager.dto.festival;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for adding organizers to a festival.
 * 
 * <p>This request object carries the necessary information to add one or
 * more users as organizers to a specific festival, including authentication
 * details of the requester.</p>
 */
@Getter
@Setter
public class AddOrganizersRequest {

    /** The username of the user making the request. */
    private String requesterUsername;

    /** The authentication token of the requester. */
    private String token;

    /** The ID of the festival to which organizers are being added. */
    private Long festivalId;

    /** The set of usernames to be added as organizers to the festival. */
    private Set<String> usernames;
}
