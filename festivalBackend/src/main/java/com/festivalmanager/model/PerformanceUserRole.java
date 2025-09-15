package com.festivalmanager.model;

import com.festivalmanager.enums.PerformanceRoleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "performance_user_roles")
public class PerformanceUserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerformanceRoleType role;

    @Column(nullable = false)
    private boolean mainArtist = false;

}
