package com.festivalmanager.dto.user;

import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO used to register a new user in the system.
 * <p>
 * This object contains the necessary information for creating a new user account.
 * Password confirmation is required to ensure accuracy.
 * </p>
 */
@Getter
@Setter
public class RegisterRequest {

    /**
     * The desired username of the new user.
     */
    private String username;

    /**
     * The full name of the new user.
     */
    private String fullname;

    /**
     * The password for the new account.
     */
    private String password1;

    /**
     * Password confirmation to verify that {@code password1} was entered correctly.
     */
    private String password2;
}
