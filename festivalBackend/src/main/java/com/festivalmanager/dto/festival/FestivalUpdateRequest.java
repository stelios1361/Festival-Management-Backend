package com.festivalmanager.dto.festival;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FestivalUpdateRequest {

    private String requesterUsername; // Who is making the request
    private String token;             // Their token

    private Long festivalId;          // Festival to update

    // Updatable fields
    private String name;
    private String description;
    private List<LocalDate> dates;

    // Venue layout
    private Set<String> stages;
    private Set<String> vendorAreas;

    // Budget
    private Double tracking;
    private Double costs;
    private Double logistics;
    private Double expectedRevenue;

    // Vendor management
    private Set<String> foodStalls;
    private Set<String> merchandiseBooths;

    // Staff and organizers (usernames)
    private Set<String> organizers;
    private Set<String> staff;
}
