package com.festivalmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing a technical requirement file associated with a performance.
 * <p>
 * Typically stores a file path or link to a PDF or TXT document containing
 * the technical requirements for a performance, such as stage setup,
 * audio/visual needs, or other specifications.
 * </p>
 */
@Getter
@Setter
@Entity
@Table(name = "performance_technical_requirement_files")
public class TechnicalRequirementFile {

    /**
     * Unique identifier of the technical requirement file.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * File path or link to the technical requirement document.
     * Example: "/files/perf123_requirements.pdf"
     */
    @Column(nullable = false)
    private String filePath;

    /**
     * The performance this file is associated with.
     */
    @OneToOne
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;
}
