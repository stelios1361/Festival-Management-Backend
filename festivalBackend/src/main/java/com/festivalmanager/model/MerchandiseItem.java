package com.festivalmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing a merchandise item associated with a performance.
 * <p>
 * Merchandise items can include T-shirts, CDs, posters, or other products
 * sold at the festival in relation to a specific performance.
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "performance_merchandise_items")
public class MerchandiseItem {

    /** Unique identifier of the merchandise item. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Name of the merchandise item. */
    @Column(nullable = false)
    private String name;

    /** Description of the merchandise item. */
    @Column(length = 500)
    private String description;

    /** Type of merchandise, e.g., "T-shirt", "CD", "Poster". */
    @Column(nullable = false)
    private String type;

    /** Price of the merchandise item. */
    @Column(nullable = false)
    private Double price;

    /** Performance to which this merchandise item belongs. */
    @ManyToOne
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;
}
