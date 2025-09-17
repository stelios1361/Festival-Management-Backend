package com.festivalmanager.dto.performance;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Set;

/**
 * Request DTO used by an artist to update an existing performance.
 * <p>
 * This object allows updating the details of a performance, including
 * optional technical requirements, setlist, merchandise items, and
 * preferred rehearsal and performance times.
 * </p>
 */
@Getter
@Setter
public class PerformanceUpdateRequest {

    /**
     * The username of the artist requesting the update.
     * Typically the creator of the performance or an authorized contributor.
     */
    private String requesterUsername;

    /**
     * Authentication token of the requester used to validate the operation.
     */
    private String token;

    /**
     * The ID of the performance to update.
     */
    private Long performanceId;

    /**
     * Updated name of the performance.
     */
    private String name;

    /**
     * Updated description of the performance.
     */
    private String description;

    /**
     * Updated genre of the performance (e.g., Rock, Jazz, Classical).
     */
    private String genre;

    /**
     * Updated duration of the performance in minutes.
     */
    private Integer duration;

    /**
     * Updated list of band member IDs (user IDs) for this performance.
     */
    private List<Long> bandMemberIds;

    /**
     * Updated technical requirements for the performance.
     */
    private TechnicalRequirementDTO technicalRequirements;

    /**
     * Updated setlist of songs for the performance.
     */
    private List<String> setlist;

    /**
     * Updated merchandise items associated with the performance.
     */
    private Set<MerchandiseItemDTO> merchandiseItems;

    /**
     * Updated preferred rehearsal times for the performance.
     */
    private Set<LocalTime> preferredRehearsalTimes;

    /**
     * Updated preferred performance time slots for the performance.
     */
    private Set<LocalTime> preferredPerformanceSlots;
}
