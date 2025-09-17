package com.festivalmanager.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing the layout of a festival venue.
 * <p>
 * The layout contains details about available stages, vendor areas,
 * and facilities. Each festival is associated with at most one
 * {@code VenueLayout}.
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "festival_venue_layouts")
public class VenueLayout {

    /**
     * Unique identifier for the venue layout.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The festival this venue layout belongs to.
     * Each festival has exactly one associated venue layout.
     */
    @OneToOne
    @JoinColumn(name = "festival_id", nullable = false)
    private Festival festival;

    /**
     * The set of stages available at the festival venue.
     */
    @ElementCollection
    @CollectionTable(name = "venue_stages", joinColumns = @JoinColumn(name = "venue_layout_id"))
    @Column(name = "stage")
    private Set<String> stages = new HashSet<>();

    /**
     * The set of vendor areas available at the festival venue.
     */
    @ElementCollection
    @CollectionTable(name = "venue_vendor_areas", joinColumns = @JoinColumn(name = "venue_layout_id"))
    @Column(name = "vendor_area")
    private Set<String> vendorAreas = new HashSet<>();

    /**
     * The set of facilities available at the festival venue.
     * Example: restrooms, medical tents, food courts.
     */
    @ElementCollection
    @CollectionTable(name = "venue_facilities", joinColumns = @JoinColumn(name = "venue_layout_id"))
    @Column(name = "facility")
    private Set<String> facilities = new HashSet<>();
}
