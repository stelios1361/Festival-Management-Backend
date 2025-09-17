package com.festivalmanager.dto.user;

import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO used to update a user's password.
 * <p>
 * This object is used by a logged-in user to change their current password.
 * Password confirmation is required to ensure accuracy.
 * </p>
 */
@Getter
@Setter
public class UpdatePasswordRequest {

    /**
     * The username of the user requesting the password change.
     */
    private String requesterUsername;

    /**
     * Authentication token of the requester used to validate the operation.
     */
    private String token;

    /**
     * The current password of the user, used to verify identity.
     */
    private String oldPassword;

    /**
     * The new password to set for the account.
     */
    private String newPassword1;

    /**
     * Confirmation of the new password to ensure it was entered correctly.
     */
    private String newPassword2;
}
