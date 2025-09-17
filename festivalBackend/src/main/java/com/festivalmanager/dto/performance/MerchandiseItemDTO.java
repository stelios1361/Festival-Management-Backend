package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing a merchandise item associated with a performance.
 * <p>
 * This object is used to transfer basic details about merchandise items such as T-shirts,
 * CDs, or other products available for a performance.
 * </p>
 */
@Getter
@Setter
public class MerchandiseItemDTO {

    /**
     * The name of the merchandise item.
     * Example: "Official Band T-shirt".
     */
    private String name;

    /**
     * A brief description of the merchandise item.
     * Example: "Black T-shirt with band logo printed on the front".
     */
    private String description;

    /**
     * The type or category of merchandise.
     * Example: "T-shirt", "CD", "Poster".
     */
    private String type;

    /**
     * The price of the merchandise item in the default currency.
     */
    private Double price;
}
