package com.festivalmanager.model;

import com.festivalmanager.enums.PermanentRoleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing a user in the festival management system.
 * <p>
 * A user may have a permanent system role (e.g., ADMIN, USER_MANAGER) 
 * and may participate in festivals as an artist, organizer, or staff member.
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    /**
     * Unique identifier of the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Username of the user. Must be unique in the system.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Password of the user (stored securely, e.g., hashed).
     */
    @Column(nullable = false)
    private String password;

    /**
     * Full name of the user.
     */
    @Column(nullable = false)
    private String fullName;

    /**
     * Permanent role assigned to the user.
     * Example: ADMIN, USER_MANAGER, etc.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PermanentRoleType permanentRole;

    /**
     * Indicates whether the user account is active.
     */
    @Column(nullable = false)
    private boolean active;

    /**
     * Counter for failed login attempts.
     * Used for security measures like temporary locking.
     */
    private int failedLoginAttempts;

    /**
     * Counter for failed password update attempts.
     * Used for security measures.
     */
    private int failedPasswordUpdates;
}
