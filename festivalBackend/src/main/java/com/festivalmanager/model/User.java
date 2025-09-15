package com.festivalmanager.model;

import com.festivalmanager.enums.PermanentRoleType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing a user in the system.
 */
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Username of the user */
    private String username;

    /** Password of the user (stored securely) */
    private String password;

    /** Full name of the user */
    private String fullName;

    /** Permanent role assigned to the user */
    @Enumerated(EnumType.STRING)
    private PermanentRoleType permanentRole;

    /** Whether the user is active */
    private boolean active;

    /** Failed login attempts counter */
    private int failedLoginAttempts;

    /** Failed password update attempts counter */
    private int failedPasswordUpdates;

    /** Lock expiration timestamp, if account is locked */
    private LocalDateTime lockedUntil;

}
