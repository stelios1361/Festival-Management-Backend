package com.festivalmanager.dto.festival;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing the layout of a festival venue.
 * 
 * <p>This object contains details about the physical setup of the festival,
 * including stages, vendor areas, and other facilities.</p>
 */
@Getter
@Setter
public class VenueLayoutDTO {

    /** The set of stage names or identifiers within the festival venue. */
    private Set<String> stages;

    /** The set of vendor area names or identifiers within the festival venue. */
    private Set<String> vendorAreas;

    /** The set of additional facilities available at the festival venue, such as restrooms or info booths. */
    private Set<String> facilities;
}
