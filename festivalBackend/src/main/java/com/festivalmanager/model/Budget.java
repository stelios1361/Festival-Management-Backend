package com.festivalmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing the budget details of a festival.
 * <p>
 * Includes tracking of overall funds, costs, logistics expenses, and expected revenue.
 * Linked one-to-one with a specific festival.
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "festival_budgets")
public class Budget {

    /** Unique identifier of the budget record. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Festival associated with this budget. */
    @OneToOne
    @JoinColumn(name = "festival_id", nullable = false)
    private Festival festival;

    /** Total tracking amount (e.g., funds already allocated or tracked). */
    @Column(nullable = false)
    private Double tracking = 0.0;

    /** Total costs incurred or projected. */
    @Column(nullable = false)
    private Double costs = 0.0;

    /** Expenses related to logistics (setup, transport, equipment, etc.). */
    @Column(nullable = false)
    private Double logistics = 0.0;

    /** Expected revenue from ticket sales, sponsorships, or merchandise. */
    @Column(nullable = false)
    private Double expectedRevenue = 0.0;
}
