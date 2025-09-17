package com.festivalmanager.dto.festival;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for adding staff members to a festival.
 * 
 * <p>This request object carries the necessary information to add one or
 * more users as staff to a specific festival, including authentication
 * details of the requester.</p>
 */
@Getter
@Setter
public class AddStaffRequest {

    /** The username of the user making the request. */
    private String requesterUsername;

    /** The authentication token of the requester. */
    private String token;

    /** The ID of the festival to which staff members are being added. */
    private Long festivalId;

    /** The set of usernames to be added as staff to the festival. */
    private Set<String> usernames;
}
