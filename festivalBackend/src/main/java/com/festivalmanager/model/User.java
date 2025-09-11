package com.festivalmanager.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing a user in the system.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    private PermanentRole permanentRole;

    /** Whether the user is active */
    private boolean active;

    /** Failed login attempts counter */
    private int failedLoginAttempts;

    /** Failed password update attempts counter */
    private int failedPasswordUpdates;

    /** Lock expiration timestamp, if account is locked */
    private LocalDateTime lockedUntil;

}
