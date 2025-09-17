package com.festivalmanager.dto.festival;

import java.time.LocalDate;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for updating an existing festival.
 * 
 * <p>This request object contains all fields that can be updated for a
 * festival, including basic details, nested DTOs for structured data, and
 * lists of organizers and staff. It also carries authentication details of
 * the requester.</p>
 */
@Getter
@Setter
public class FestivalUpdateRequest {

    /** The username of the user making the update request. */
    private String requesterUsername;

    /** The authentication token of the requester. */
    private String token;

    /** The ID of the festival to be updated. */
    private Long festivalId;

    // Updatable main fields

    /** The updated name of the festival; optional. */
    private String name;

    /** The updated description of the festival; optional. */
    private String description;

    /** The updated set of dates for the festival; optional. */
    private Set<LocalDate> dates;

    // Nested DTOs for better structure

    /** The updated venue layout; optional. */
    private VenueLayoutDTO venueLayout;

    /** The updated budget details; optional. */
    private BudgetDTO budget;

    /** The updated vendor management details; optional. */
    private VendorManagementDTO vendorManagement;

    /** The updated list of organizer usernames; optional. */
    private Set<String> organizers;

    /** The updated list of staff usernames; optional. */
    private Set<String> staff;
}
