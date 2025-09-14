package com.festivalmanager.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Authentication token for a user session.
 */
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

    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
