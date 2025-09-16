package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MerchandiseItemDTO {

    private String name;            // Name of item
    private String description;     // Description
    private String type;            // e.g., "T-shirt", "CD"
    private Double price;           // Price of the item
}
