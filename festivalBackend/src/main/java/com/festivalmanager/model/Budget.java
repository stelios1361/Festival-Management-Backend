package com.festivalmanager.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

    @Getter
    @Setter
    @Entity
    @Table(name = "festival_budgets")
    public class Budget {

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