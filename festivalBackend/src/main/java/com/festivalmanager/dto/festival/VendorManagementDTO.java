package com.festivalmanager.dto.festival;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing vendor management information for a festival.
 * 
 * <p>This object contains the lists of vendors participating in the festival,
 * including food stalls and merchandise booths.</p>
 */
@Getter
@Setter
public class VendorManagementDTO {

    /** The set of food stall vendor names participating in the festival. */
    private Set<String> foodStalls;

    /** The set of merchandise booth vendor names participating in the festival. */
    private Set<String> merchandiseBooths;
}
