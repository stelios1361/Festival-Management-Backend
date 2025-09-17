package com.festivalmanager.dto.festival;

import java.time.LocalDate;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for creating a new festival.
 * 
 * <p>This request object contains all necessary information to create a new
 * festival, including authentication details of the requester and festival
 * details such as name, description, dates, and venue.</p>
 */
@Getter
@Setter
public class FestivalCreateRequest {

    /** The username of the user making the festival creation request. */
    private String requesterUsername;

    /** The authentication token of the requester. */
    private String token;

    /** The name of the festival. */
    private String name;

    /** A description of the festival. */
    private String description;

    /** The set of dates for the festival. At least one date must be provided. */
    private Set<LocalDate> dates;

    /** The venue where the festival will take place. */
    private String venue;
}
