package com.festivalmanager.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password; 

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PermanentRole permanentRole = PermanentRole.USER;

    @Column(nullable = false)
    private boolean active = true;

    // Security
    private int failedLoginAttempts = 0;
    private int failedPasswordUpdates = 0;

    private LocalDateTime lockedUntil; // null = not locked
}
