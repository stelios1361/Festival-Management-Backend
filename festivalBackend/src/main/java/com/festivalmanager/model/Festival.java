package com.festivalmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
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

    /** System-generated identifier */
    @Column(unique = true, nullable = false, updatable = false)
    private String identifier = java.util.UUID.randomUUID().toString();

    /** System-generated creation date */
    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    /** Festival main info */
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, length = 1000)
    private String description;

    @ElementCollection
    @CollectionTable(name = "festival_dates", joinColumns = @JoinColumn(name = "festival_id"))
    @Column(name = "date", nullable = false)
    private Set<LocalDate> dates = new HashSet<>();

    @Column(nullable = false)
    private String venue;

    /** Festival state */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FestivalState state = FestivalState.CREATED;

    // ---------------- Nested tables ----------------

    /** Venue layout */
    @OneToOne(mappedBy = "festival", cascade = CascadeType.ALL, orphanRemoval = true)
    private VenueLayout venueLayout;

    /** Budget */
    @OneToOne(mappedBy = "festival", cascade = CascadeType.ALL, orphanRemoval = true)
    private Budget budget;

    /** Vendor management */
    @OneToOne(mappedBy = "festival", cascade = CascadeType.ALL, orphanRemoval = true)
    private VendorManagement vendorManagement;

    /** Festival-specific user roles (ORGANIZER, STAFF) */
    @OneToMany(mappedBy = "festival", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FestivalUserRole> userRoles = new HashSet<>();

    /** Performances associated with this festival */
    @OneToMany(mappedBy = "festival", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Performance> performances = new HashSet<>();

    // ---------------- Nested entities ----------------

    @Getter
    @Setter
    @Entity
    @Table(name = "festival_venue_layouts")
    public static class VenueLayout {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @OneToOne
        @JoinColumn(name = "festival_id", nullable = false)
        private Festival festival;

        @ElementCollection
        @CollectionTable(name = "venue_stages", joinColumns = @JoinColumn(name = "venue_layout_id"))
        @Column(name = "stage")
        private Set<String> stages = new HashSet<>();

        @ElementCollection
        @CollectionTable(name = "venue_vendor_areas", joinColumns = @JoinColumn(name = "venue_layout_id"))
        @Column(name = "vendor_area")
        private Set<String> vendorAreas = new HashSet<>();

        @ElementCollection
        @CollectionTable(name = "venue_facilities", joinColumns = @JoinColumn(name = "venue_layout_id"))
        @Column(name = "facility")
        private Set<String> facilities = new HashSet<>();
    }

    @Getter
    @Setter
    @Entity
    @Table(name = "festival_budgets")
    public static class Budget {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @OneToOne
        @JoinColumn(name = "festival_id", nullable = false)
        private Festival festival;

        private Double tracking = 0.0;
        private Double costs = 0.0;
        private Double logistics = 0.0;
        private Double expectedRevenue = 0.0;
    }

    @Getter
    @Setter
    @Entity
    @Table(name = "festival_vendor_managements")
    public static class VendorManagement {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @OneToOne
        @JoinColumn(name = "festival_id", nullable = false)
        private Festival festival;

        @ElementCollection
        @CollectionTable(name = "vendor_food_stalls", joinColumns = @JoinColumn(name = "vendor_management_id"))
        @Column(name = "food_stall")
        private Set<String> foodStalls = new HashSet<>();

        @ElementCollection
        @CollectionTable(name = "vendor_merch_booths", joinColumns = @JoinColumn(name = "vendor_management_id"))
        @Column(name = "merch_booth")
        private Set<String> merchandiseBooths = new HashSet<>();
    }

    // ---------------- Festival states ----------------
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
