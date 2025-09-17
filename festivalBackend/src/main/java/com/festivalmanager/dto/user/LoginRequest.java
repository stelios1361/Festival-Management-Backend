package com.festivalmanager.dto.user;

import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO used to authenticate a user and initiate a login session.
 * <p>
 * This object contains the credentials required for authentication.
 * </p>
 */
@Getter
@Setter
public class LoginRequest {

    /**
     * The username of the user attempting to log in.
     */
    private String username;

    /**
     * The password of the user attempting to log in.
     */
    private String password;
}
