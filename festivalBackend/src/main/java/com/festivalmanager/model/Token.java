package com.festivalmanager.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * Authentication token for a user session.
 */
@Getter
@Setter
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The actual token string */
    @Column(nullable = false, unique = true)
    private String value;

    /** Expiration date/time */
    private LocalDateTime expiresAt;
    
    
    /** Status of token valid or invalid */
    private boolean active = true; 

    /** Associated user */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
