package com.festivalmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "performances")
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Auto-generated identifier

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
     * Performance description
     */
    @Column(nullable = false, length = 1000)
    private String description;

    /**
     * Performance genre
     */
    @Column(nullable = false)
    private String genre;

    /**
     * Performance duration in minutes
     */
    @Column(nullable = false)
    private Integer duration;

    /**
     * Performance-specific user roles
     */
    @OneToMany(mappedBy = "performance", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PerformanceUserRole> userRoles = new HashSet<>();

    /**
     * Optional technical requirements
     */
    @Embedded
    private TechnicalRequirements technicalRequirements;

    /**
     * Optional setlist
     */
    @ElementCollection
    @CollectionTable(name = "performance_setlist", joinColumns = @JoinColumn(name = "performance_id"))
    @Column(name = "song")
    private Set<String> setlist = new HashSet<>();

    /**
     * Optional merchandise items
     */
    @ElementCollection
    @CollectionTable(name = "performance_merchandise", joinColumns = @JoinColumn(name = "performance_id"))
    private Set<MerchandiseItem> merchandiseItems = new HashSet<>();

    /**
     * Optional preferred rehearsal times
     */
    @ElementCollection
    @CollectionTable(name = "performance_rehearsal_times", joinColumns = @JoinColumn(name = "performance_id"))
    @Column(name = "rehearsal_time")
    private Set<String> preferredRehearsalTimes = new HashSet<>();

    /**
     * Optional preferred performance slots
     */
    @ElementCollection
    @CollectionTable(name = "performance_slots", joinColumns = @JoinColumn(name = "performance_id"))
    @Column(name = "performance_slot")
    private Set<String> preferredPerformanceSlots = new HashSet<>();

    /**
     * Stage manager assigned later
     */
    @ManyToOne
    @JoinColumn(name = "stage_manager_id")
    private User stageManager;

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
     * Performance state
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerformanceState state = PerformanceState.CREATED;

    /**
     * Festival to which this performance belongs
     */
    @ManyToOne
    @JoinColumn(name = "festival_id", nullable = false)
    private Festival festival;

    // ---------------- EMBEDDABLE CLASSES ----------------
    @Embeddable
    @Getter
    @Setter
    public static class TechnicalRequirements {

        private String equipment; // e.g., "Guitar, Drums"
        private String stageSetup; // e.g., "Center stage"
        private String soundLighting; // e.g., "Basic sound/light"
    }

    @Embeddable
    @Getter
    @Setter
    public static class MerchandiseItem {

        private String name;
        private String description;
        private String type; // e.g., "T-shirt", "CD"
        private Double price;
    }

    // ---------------- PERFORMANCE STATES ----------------
    public enum PerformanceState {
        CREATED,
        SUBMITTED,
        REVIEWED,
        APPROVED,
        SCHEDULED,
        REJECTED
    }
}
