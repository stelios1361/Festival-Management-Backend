package com.festivalmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "performance_merchandise_items")
public class MerchandiseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String type;   // e.g. "T-shirt", "CD"
    private Double price;

    @ManyToOne
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;
}
