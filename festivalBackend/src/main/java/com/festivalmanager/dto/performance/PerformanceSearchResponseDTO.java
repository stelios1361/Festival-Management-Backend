package com.festivalmanager.dto.performance;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing the response for a performance search.
 * 
 * <p>This object contains the basic details of a performance. Additional details
 * such as band members, technical requirements, setlist, merchandise, rehearsal
 * preferences, and reviewer information are included only if the requester has
 * appropriate permissions (e.g., organizer, staff, or creator).</p>
 */
@Getter
@Setter
public class PerformanceSearchResponseDTO {

    /** The unique identifier of the performance. */
    private Long id;


    /** The name of the performance. */
    private String name;

    /** A description of the performance. */
    private String description;

    /** The genre of the performance. */
    private String genre;

    /** The duration of the performance in minutes. */
    private Integer duration;

    /** The usernames of band members; optional. */
    private List<String> bandMembers;

    /** The technical requirement file; optional. */
    private TechnicalRequirementDTO technicalRequirement;

    /** The setlist of songs; optional. */
    private Set<String> setlist;

    /** The list of merchandise items; optional. */
    private List<MerchandiseItemDTO> merchandiseItems;

    /** The preferred rehearsal times; optional. */
    private Set<LocalTime> preferredRehearsalTimes;

    /** The preferred performance slots; optional. */
    private Set<LocalTime> preferredPerformanceSlots;

    /** The username of the stage manager; optional. */
    private String stageManager;

    /** The username of the creator / main artist. */
    private String creator;

    /** Reviewer comments; optional. */
    private String reviewerComments;

    /** Reviewer score; optional. */
    private Double score;

    /** The current state of the performance. */
    private String state;

    /** The id of the associated festival. */
    private Long festivalId;
}
