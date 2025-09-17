package com.festivalmanager.dto.user;

import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO used to delete a user from the system.
 * <p>
 * This object can be used in two ways:
 * <ul>
 *     <li>By an authorized user (e.g., admin) to delete another user account.</li>
 *     <li>By a user requesting self-deletion, in which case {@code targetUsername} can be omitted.</li>
 * </ul>
 * </p>
 */
@Getter
@Setter
public class DeleteUserRequest {

    /**
     * The username of the user making the deletion request.
     * Typically an admin or the user requesting self-deletion.
     */
    private String requesterUsername;

    /**
     * Authentication token of the requester used to validate the operation.
     */
    private String token;

    /**
     * The username of the target user to be deleted.
     * Optional: if omitted, the requester is requesting self-deletion.
     */
    private String targetUsername;
}
