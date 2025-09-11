package com.festivalmanager.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing a user in the system.
 */
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
    private PermanentRole permanentRole;

    /** Whether the user is active */
    private boolean active;

    /** Failed login attempts counter */
    private int failedLoginAttempts;

    /** Failed password update attempts counter */
    private int failedPasswordUpdates;

    /** Lock expiration timestamp, if account is locked */
    private LocalDateTime lockedUntil;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public PermanentRole getPermanentRole() {
        return permanentRole;
    }

    public void setPermanentRole(PermanentRole permanentRole) {
        this.permanentRole = permanentRole;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public int getFailedPasswordUpdates() {
        return failedPasswordUpdates;
    }

    public void setFailedPasswordUpdates(int failedPasswordUpdates) {
        this.failedPasswordUpdates = failedPasswordUpdates;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(LocalDateTime lockedUntil) {
        this.lockedUntil = lockedUntil;
    }
    
    
    


    
}
