package com.festivalmanager.dto.user;

import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO used to update user information.
 * <p>
 * This object allows a user to update their own account information
 * or, if the requester is an admin, to update another user's information.
 * All fields except {@code requesterUsername} and {@code token} are optional.
 * </p>
 */
@Getter
@Setter
public class UpdateInfoRequest {

    /**
     * The username of the user performing the update.
     * Required to authenticate and authorize the operation.
     */
    private String requesterUsername;

    /**
     * Authentication token of the requester used to validate the operation.
     */
    private String token;

    /**
     * The username of the target user whose information is being updated.
     * Optional; only used when the requester is an administrator.
     */
    private String targetUsername;

    /**
     * The new username to set for the account.
     * Optional.
     */
    private String newUsername;

    /**
     * The new full name to set for the account.
     * Optional.
     */
    private String newFullName;
}
