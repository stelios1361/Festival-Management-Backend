package com.festivalmanager.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing an authentication token for a user session.
 * <p>
 * Tokens are used to authenticate API requests and manage user sessions.
 * Each token is linked to a single user and has an expiration date.
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "tokens")
public class Token {

    /**
     * Unique identifier of the token.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The actual token string used for authentication.
     * Must be unique in the system.
     */
    @Column(nullable = false, unique = true)
    private String value;

    /**
     * Expiration date and time of the token.
     */
    private LocalDateTime expiresAt;

    /**
     * Status indicating whether the token is active or invalidated.
     */
    private boolean active = true;

    /**
     * The user associated with this token.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
