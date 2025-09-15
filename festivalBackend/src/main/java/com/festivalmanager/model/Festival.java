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
@Table(name = "festivals")
public class Festival {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique system-generated identifier */
    @Column(unique = true, nullable = false, updatable = false)
    private String identifier = java.util.UUID.randomUUID().toString();

    /** System-generated creation date */
    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    /** Unique festival name */
    @Column(nullable = false, unique = true)
    private String name;

    /** Festival description */
    @Column(nullable = false)
    private String description;

    /** Festival dates */
    @ElementCollection
    @CollectionTable(name = "festival_dates", joinColumns = @JoinColumn(name = "festival_id"))
    @Column(name = "date", nullable = false)
    private Set<LocalDateTime> dates = new HashSet<>();

    /** Venue of the festival */
    @Column(nullable = false)
    private String venue;

    /** Festival-specific user roles (ORGANIZER, STAFF, ARTIST) */
    @OneToMany(mappedBy = "festival", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FestivalUserRole> userRoles = new HashSet<>();

    /** Performances associated with this festival */
    @OneToMany(mappedBy = "festival", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Performance> performances = new HashSet<>();

    /** Optional venue layout */
    @ElementCollection
    @CollectionTable(name = "festival_stages", joinColumns = @JoinColumn(name = "festival_id"))
    @Column(name = "stage")
    private Set<String> stages = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "festival_vendor_areas", joinColumns = @JoinColumn(name = "festival_id"))
    @Column(name = "vendor_area")
    private Set<String> vendorAreas = new HashSet<>();

    /** Optional budget */
    @Embedded
    private Budget budget;

    /** Optional vendor management */
    @Embedded
    private VendorManagement vendorManagement;

    /** Festival state */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FestivalState state = FestivalState.CREATED;

    // ---------------- EMBEDDABLE CLASSES ----------------
    @Getter
    @Setter
    @Embeddable
    public static class Budget {
        private Double tracking = 0.0;
        private Double costs = 0.0;
        private Double logistics = 0.0;
        private Double expectedRevenue = 0.0;
    }

    @Getter
    @Setter
    @Embeddable
    public static class VendorManagement {
        @ElementCollection
        @CollectionTable(name = "festival_food_stalls", joinColumns = @JoinColumn(name = "festival_id"))
        @Column(name = "food_stall")
        private Set<String> foodStalls = new HashSet<>();

        @ElementCollection
        @CollectionTable(name = "festival_merch_booths", joinColumns = @JoinColumn(name = "festival_id"))
        @Column(name = "merch_booth")
        private Set<String> merchandiseBooths = new HashSet<>();
    }

    // ---------------- FESTIVAL STATES ----------------
    public enum FestivalState {
        CREATED,
        SUBMISSION,
        ASSIGNMENT,
        REVIEW,
        SCHEDULING,
        FINAL_SUBMISSION,
        DECISION,
        ANNOUNCED
    }
}
