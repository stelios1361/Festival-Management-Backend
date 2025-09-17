package com.festivalmanager.dto.user;

import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO used to update the active status of a user account.
 * <p>
 * This object is typically used by an administrator or authorized user
 * to activate or deactivate another user's account.
 * </p>
 */
@Getter
@Setter
public class UpdateAccountStatusRequest {

    /**
     * The username of the user executing the request.
     * Typically an administrator or authorized manager.
     */
    private String requesterUsername;

    /**
     * Authentication token of the requester used to validate the operation.
     */
    private String token;

    /**
     * The username of the target user whose account status is being changed.
     */
    private String targetUsername;

    /**
     * The new active status of the account.
     * {@code true} = activate the account, {@code false} = deactivate the account.
     */
    private Boolean newActive;
}
