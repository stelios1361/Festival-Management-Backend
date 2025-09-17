package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO used to search for performances based on various criteria.
 * <p>
 * This object allows filtering performances by name, artist, or genre.
 * If the requesterUsername and token are provided, the response may include
 * additional details based on the requester's role and permissions.
 * </p>
 */
@Getter
@Setter
public class PerformanceSearchRequest {

    /**
     * The username of the user performing the search.
     * Optional; provides role-based access to additional performance details.
     */
    private String requesterUsername;

    /**
     * Authentication token of the requester used to validate the operation.
     * Optional; provides role-based access to additional performance details.
     */
    private String token;

    /**
     * Name of the performance to search for.
     * Matches performances containing this string.
     */
    private String name;

    /**
     * Artist to search for.
     * This searches both the performance creator and band members.
     */
    private String artist;

    /**
     * Genre of the performance to search for.
     */
    private String genre;
}
