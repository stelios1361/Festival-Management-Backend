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

@Getter
@Setter
@Entity
@Table(name = "festival_venue_layouts")
public class VenueLayout {

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
