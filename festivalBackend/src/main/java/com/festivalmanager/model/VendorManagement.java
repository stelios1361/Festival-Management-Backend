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
 * Entity representing the vendor management details of a festival.
 * <p>
 * Contains information about food stalls and merchandise booths
 * available at the festival venue. Each festival has at most one
 * {@code VendorManagement} entity.
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "festival_vendor_managements")
public class VendorManagement {

    /**
     * Unique identifier for the vendor management entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The festival this vendor management configuration belongs to.
     */
    @OneToOne
    @JoinColumn(name = "festival_id", nullable = false)
    private Festival festival;

    /**
     * The set of food stalls available at the festival venue.
     * Example: "Burger Tent", "Pizza Stand", "Vegan Corner".
     */
    @ElementCollection
    @CollectionTable(name = "vendor_food_stalls", joinColumns = @JoinColumn(name = "vendor_management_id"))
    @Column(name = "food_stall")
    private Set<String> foodStalls = new HashSet<>();

    /**
     * The set of merchandise booths available at the festival venue.
     * Example: "Band Merch Booth", "Festival Souvenirs".
     */
    @ElementCollection
    @CollectionTable(name = "vendor_merch_booths", joinColumns = @JoinColumn(name = "vendor_management_id"))
    @Column(name = "merch_booth")
    private Set<String> merchandiseBooths = new HashSet<>();
}
