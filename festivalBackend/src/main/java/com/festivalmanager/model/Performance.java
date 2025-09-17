package com.festivalmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "performances")
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique system-generated identifier
     */
    @Column(unique = true, nullable = false, updatable = false)
    private String identifier = java.util.UUID.randomUUID().toString();

    /**
     * System-generated creation date
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    /**
     * Unique performance name
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Description
     */
    @Column(nullable = false, length = 1000)
    private String description;

    /**
     * Genre
     */
    @Column(nullable = false)
    private String genre;

    /**
     * Duration in minutes
     */
    @Column(nullable = false)
    private Integer duration;

    /**
     * Band members (users who are ARTISTS in the festival)
     */
    @ManyToMany
    @JoinTable(
            name = "performance_band_members",
            joinColumns = @JoinColumn(name = "performance_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> bandMembers = new HashSet<>();

    /**
     * Technical requirements (separate table, just stores file link)
     */
    @OneToOne(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private TechnicalRequirementFile technicalRequirement;

    /**
     * Optional setlist
     */
    @ElementCollection
    @CollectionTable(name = "performance_setlist", joinColumns = @JoinColumn(name = "performance_id"))
    @Column(name = "song")
    private Set<String> setlist = new HashSet<>();

    /**
     * Merchandise items (separate table)
     */
    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MerchandiseItem> merchandiseItems = new HashSet<>();

    /**
     * Optional preferred rehearsal times
     */
    @ElementCollection
    @CollectionTable(name = "performance_rehearsal_times", joinColumns = @JoinColumn(name = "performance_id"))
    @Column(name = "rehearsal_time")
    private Set<LocalTime> preferredRehearsalTimes = new HashSet<>();

    /**
     * Optional preferred performance slots
     */
    @ElementCollection
    @CollectionTable(name = "performance_slots", joinColumns = @JoinColumn(name = "performance_id"))
    @Column(name = "performance_slot")
    private Set<LocalTime> preferredPerformanceSlots = new HashSet<>();

    /**
     * Stage manager (must be a STAFF member of the festival)
     */
    @ManyToOne
    @JoinColumn(name = "stage_manager_id")
    private User stageManager;

    /**
     * Creator / main artist
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "creator_id")
    private User creator;

    /**
     * Reviewer comments
     */
    @Column(length = 1000)
    private String reviewerComments;

    /**
     * Reviewer score
     */
    private Double score;

    /**
     * Current state
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerformanceState state = PerformanceState.CREATED;

    /**
     * Festival reference
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "festival_id")
    private Festival festival;

    // ---------------- STATES ----------------
    public enum PerformanceState {
        CREATED,
        SUBMITTED,
        REVIEWED,
        APPROVED,
        SCHEDULED,
        REJECTED
    }
}
