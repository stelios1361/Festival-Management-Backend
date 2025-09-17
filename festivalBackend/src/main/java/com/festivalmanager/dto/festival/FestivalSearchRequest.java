package com.festivalmanager.dto.festival;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for searching festivals.
 * 
 * <p>All fields in this request are optional. If {@code requesterUsername} and
 * {@code token} are null, the search is treated as being performed by a
 * visitor. The other fields can be used to filter festivals by name,
 * description, date range, or venue.</p>
 */
@Getter
@Setter
public class FestivalSearchRequest {

    /** The username of the user performing the search; null if the user is a visitor. */
    private String requesterUsername;

    /** The authentication token of the requester; null if the user is a visitor. */
    private String token;

    // Search criteria

    /** Filter by festival name; optional. */
    private String name;

    /** Filter by festival description; optional. */
    private String description;

    /** Filter by festival start date; optional. */
    private LocalDate startDate;

    /** Filter by festival end date; optional. */
    private LocalDate endDate;

    /** Filter by festival venue; optional. */
    private String venue;
}
