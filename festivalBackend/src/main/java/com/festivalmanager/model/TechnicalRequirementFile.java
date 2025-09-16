package com.festivalmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "performance_technical_requirement_files")
public class TechnicalRequirementFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** File path or link (PDF or TXT) */
    @Column(nullable = false)
    private String filePath;

    @OneToOne
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;
}
