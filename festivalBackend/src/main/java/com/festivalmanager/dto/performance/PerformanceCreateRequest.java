package com.festivalmanager.dto.performance;

import java.time.LocalTime;
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
    private TechnicalRequirementDTO technicalRequirements;
    private Set<String> setlist;
    private Set<MerchandiseItemDTO> merchandiseItems;
    private Set<LocalTime> preferredRehearsalTimes;
    private Set<LocalTime> preferredPerformanceSlots;

}
