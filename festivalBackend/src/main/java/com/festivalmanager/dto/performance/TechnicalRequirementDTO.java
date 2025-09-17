package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing the technical requirements
 * for a performance.
 * <p>
 * Typically used to provide the filename of a document containing
 * technical specifications such as stage setup, audio/visual needs,
 * or other performance requirements.
 * </p>
 */
@Getter
@Setter
public class TechnicalRequirementDTO {

    /**
     * The filename of the technical requirements document.
     * Example: "perf123_requirements.pdf"
     */
    private String fileName;
}
