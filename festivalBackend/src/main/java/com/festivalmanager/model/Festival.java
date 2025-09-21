package com.festivalmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a festival.
 * <p>
 * A festival has a unique identifier, creation date, main info (name, description, venue),
 * a set of scheduled dates, and various nested entities such as venue layout, budget,
 * vendor management, user roles, and performances.
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "festivals")
public class Festival {

    /** Unique database identifier of the festival. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /** Timestamp when the festival was created. */
    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    /** Name of the festival (unique). */
    @Column(nullable = false, unique = true)
    private String name;

    /** Description of the festival. */
    @Column(nullable = false, length = 1000)
    private String description;

    /** Dates on which the festival takes place. */
    @ElementCollection
    @CollectionTable(name = "festival_dates", joinColumns = @JoinColumn(name = "festival_id"))
    @Column(name = "date", nullable = false)
    private Set<LocalDate> dates = new HashSet<>();

    /** Venue where the festival takes place. */
    @Column(nullable = false)
    private String venue;

    /** Current state of the festival workflow. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FestivalState state = FestivalState.CREATED;

    // ---------------- Nested entities ----------------

    /** Layout of the festival venue. */
    @OneToOne(mappedBy = "festival", cascade = CascadeType.ALL, orphanRemoval = true)
    private VenueLayout venueLayout;

    /** Budget details of the festival. */
    @OneToOne(mappedBy = "festival", cascade = CascadeType.ALL, orphanRemoval = true)
    private Budget budget;

    /** Vendor management configuration for the festival. */
    @OneToOne(mappedBy = "festival", cascade = CascadeType.ALL, orphanRemoval = true)
    private VendorManagement vendorManagement;

    /** Festival-specific user roles (ORGANIZER, STAFF, etc.). */
    @OneToMany(mappedBy = "festival", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FestivalUserRole> userRoles = new HashSet<>();

    /** Performances associated with this festival. */
    @OneToMany(mappedBy = "festival", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Performance> performances = new HashSet<>();

    /**
     * Enumeration representing possible states of a festival.
     */
    public enum FestivalState {
        CREATED,          // Festival record created
        SUBMISSION,       // Performance submission phase
        ASSIGNMENT,       // Assigning staff and organizers
        REVIEW,           // Performance review phase
        SCHEDULING,       // Scheduling performances
        FINAL_SUBMISSION, // Final artist submissions
        DECISION,         // Organizer decisions on performances
        ANNOUNCED         // Festival officially announced to public
    }
}
