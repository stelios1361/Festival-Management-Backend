package com.festivalmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a performance at a festival.
 * <p>
 * A performance can have multiple band members, technical requirements,
 * merchandise items, and optional rehearsal or performance slots. It is
 * associated with a festival and managed by a creator (main artist) and
 * optionally a stage manager.
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "performances")
public class Performance {

    /** Unique database identifier for the performance. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique system-generated identifier (UUID). */
    @Column(unique = true, nullable = false, updatable = false)
    private String identifier = java.util.UUID.randomUUID().toString();

    /** Timestamp when the performance was created. */
    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    /** Unique name of the performance. */
    @Column(nullable = false, unique = true)
    private String name;

    /** Description of the performance. */
    @Column(nullable = false, length = 1000)
    private String description;

    /** Genre of the performance. */
    @Column(nullable = false)
    private String genre;

    /** Duration of the performance in minutes. */
    @Column(nullable = false)
    private Integer duration;

    /** Band members (users who are artists) performing in this performance. */
    @ManyToMany
    @JoinTable(
            name = "performance_band_members",
            joinColumns = @JoinColumn(name = "performance_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> bandMembers = new HashSet<>();

    /** Optional technical requirement file associated with the performance. */
    @OneToOne(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private TechnicalRequirementFile technicalRequirement;

    /** Optional setlist of songs for the performance. */
    @ElementCollection
    @CollectionTable(name = "performance_setlist", joinColumns = @JoinColumn(name = "performance_id"))
    @Column(name = "song")
    private Set<String> setlist = new HashSet<>();

    /** Merchandise items associated with the performance. */
    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MerchandiseItem> merchandiseItems = new HashSet<>();

    /** Optional preferred rehearsal times. */
    @ElementCollection
    @CollectionTable(name = "performance_rehearsal_times", joinColumns = @JoinColumn(name = "performance_id"))
    @Column(name = "rehearsal_time")
    private Set<LocalTime> preferredRehearsalTimes = new HashSet<>();

    /** Optional preferred performance slots. */
    @ElementCollection
    @CollectionTable(name = "performance_slots", joinColumns = @JoinColumn(name = "performance_id"))
    @Column(name = "performance_slot")
    private Set<LocalTime> preferredPerformanceSlots = new HashSet<>();

    /** Stage manager responsible for this performance (must be festival staff). */
    @ManyToOne
    @JoinColumn(name = "stage_manager_id")
    private User stageManager;

    /** Creator / main artist of the performance. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "creator_id")
    private User creator;

    /** Reviewer comments for this performance. */
    @Column(length = 1000)
    private String reviewerComments;

    /** Reviewer score. */
    private Double score;

    /** Current state of the performance workflow. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerformanceState state = PerformanceState.CREATED;

    /** Festival to which this performance belongs. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "festival_id")
    private Festival festival;

    /**
     * Enumeration representing the possible states of a performance.
     */
    public enum PerformanceState {
        CREATED,    // Initial state
        SUBMITTED,  // Submitted for review
        REVIEWED,   // Reviewed by staff
        APPROVED,   // Approved by organizer
        SCHEDULED,  // Scheduled for performance
        REJECTED    // Rejected by organizer
    }
}
