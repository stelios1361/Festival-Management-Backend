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
    @Table(name = "festival_vendor_managements")
    public  class VendorManagement {

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