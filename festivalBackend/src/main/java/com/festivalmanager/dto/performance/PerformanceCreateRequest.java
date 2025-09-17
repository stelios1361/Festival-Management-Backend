package com.festivalmanager.dto.performance;

import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

/**
 * Request DTO used to create a new performance for a festival.
 * <p>
 * This object contains all necessary information for a creator (main artist)
 * to submit a new performance, including optional technical and merchandising details.
 * </p>
 */
@Getter
@Setter
public class PerformanceCreateRequest {

    // ------------------ REQUIRED ------------------

    /**
     * The username of the requester creating the performance.
     * Typically the main artist or an authorized user.
     */
    private String requesterUsername;

    /**
     * Authentication token of the requester to validate the operation.
     */
    private String token;

    /**
     * The ID of the festival to which this performance will be added.
     */
    private Long festivalId;

    /**
     * Unique name of the performance within the festival.
     */
    private String name;

    /**
     * Description of the performance.
     */
    private String description;

    /**
     * Genre of the performance (e.g., Rock, Jazz, Classical).
     */
    private String genre;

    /**
     * Duration of the performance in minutes.
     */
    private Integer duration;

    /**
     * IDs of users to be added as band members for this performance.
     */
    private Set<Long> bandMemberIds;

    // ------------------ OPTIONAL ------------------

    /**
     * Technical requirements for the performance, such as stage or equipment needs.
     */
    private TechnicalRequirementDTO technicalRequirements;

    /**
     * Optional setlist of songs for the performance.
     */
    private Set<String> setlist;

    /**
     * Optional merchandise items associated with the performance.
     */
    private Set<MerchandiseItemDTO> merchandiseItems;

    /**
     * Optional preferred rehearsal times for the performance.
     */
    private Set<LocalTime> preferredRehearsalTimes;

    /**
     * Optional preferred performance time slots.
     */
    private Set<LocalTime> preferredPerformanceSlots;
}
