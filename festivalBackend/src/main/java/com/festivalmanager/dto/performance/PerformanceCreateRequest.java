package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class PerformanceCreateRequest {

    // ------------------ REQUIRED ------------------
    private String requesterUsername;  // Creator of the performance
    private String token;              // Authentication token
    private Long festivalId;           // Festival to which this performance belongs

    private String name;               // Unique performance name within the festival
    private String description;        // Performance description
    private String genre;              // Genre of the performance
    private Integer duration;          // Duration in minutes
    private Set<Long> bandMemberIds;   // IDs of band members (users)

    // ------------------ OPTIONAL ------------------
    private TechnicalRequirementsDTO technicalRequirements;
    private Set<String> setlist;
    private Set<MerchandiseItemDTO> merchandiseItems;
    private Set<String> preferredRehearsalTimes;
    private Set<String> preferredPerformanceSlots;

    // ------------------ NESTED DTO CLASSES ------------------
    @Getter
    @Setter
    public static class TechnicalRequirementsDTO {
        private String equipment;       // e.g., "Guitar, Drums"
        private String stageSetup;      // e.g., "Center Stage"
        private String soundLighting;   // e.g., "Basic sound/light"
    }

    @Getter
    @Setter
    public static class MerchandiseItemDTO {
        private String name;            // Name of item
        private String description;     // Description
        private String type;            // e.g., "T-shirt", "CD"
        private Double price;           // Price of the item
    }
}
