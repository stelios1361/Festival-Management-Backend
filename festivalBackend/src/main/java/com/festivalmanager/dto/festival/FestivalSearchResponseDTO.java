package com.festivalmanager.dto.festival;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing the response for a festival search.
 * 
 * <p>This object contains the basic details of a festival. Additional details
 * such as organizers, venue layout, budget, vendor management, and staff are
 * included only if the requester is an organizer of the festival.</p>
 */
@Getter
@Setter
public class FestivalSearchResponseDTO {

    /** The unique identifier of the festival. */
    private Long id;

    /** The name of the festival. */
    private String name;

    /** The description of the festival. */
    private String description;

    /** The venue where the festival takes place. */
    private String venue;

    /** The set of dates when the festival occurs. */
    private Set<LocalDate> dates;

    /** The list of usernames of organizers; optional, included only if the requester is an organizer. */
    private List<String> organizers;

    /** The layout details of the festival venue; optional. */
    private VenueLayoutDTO venueLayout;

    /** The budget details of the festival; optional. */
    private BudgetDTO budget;

    /** The vendor management details of the festival; optional. */
    private VendorManagementDTO vendorManagement;

    /** The list of usernames of staff members; optional. */
    private List<String> staff;
}
