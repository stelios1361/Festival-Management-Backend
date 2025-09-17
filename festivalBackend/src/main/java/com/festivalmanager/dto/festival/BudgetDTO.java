package com.festivalmanager.dto.festival;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing budget information for a festival.
 * 
 * <p>This object contains various financial metrics for tracking festival
 * expenses and expected revenue.</p>
 */
@Getter
@Setter
public class BudgetDTO {

    /** The amount allocated for general tracking or miscellaneous expenses. */
    private Double tracking;

    /** The total costs incurred for the festival. */
    private Double costs;

    /** The amount allocated for logistics, such as venue, transport, and equipment. */
    private Double logistics;

    /** The expected revenue to be generated from the festival. */
    private Double expectedRevenue;
}
