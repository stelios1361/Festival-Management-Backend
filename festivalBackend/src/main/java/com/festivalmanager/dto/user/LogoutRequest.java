package com.festivalmanager.dto.user;

import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO used to log out a user by invalidating their current session token.
 * <p>
 * This object is sent when a user wants to terminate an active session.
 * </p>
 */
@Getter
@Setter
public class LogoutRequest {

    /**
     * The username of the user performing the logout.
     */
    private String requesterUsername;

    /**
     * The session token to be invalidated for logout.
     */
    private String token;
}
