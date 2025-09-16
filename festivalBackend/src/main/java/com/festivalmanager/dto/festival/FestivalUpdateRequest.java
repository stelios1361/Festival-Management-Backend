package com.festivalmanager.dto.festival;

import java.time.LocalDate;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FestivalUpdateRequest {

    private String requesterUsername; // Who is making the request
    private String token;             // Their token

    private Long festivalId;          // Festival to update

    // Updatable main fields
    private String name;
    private String description;
    private Set<LocalDate> dates;

    // Nested DTOs for better structure
    private VenueLayoutDTO venueLayout;
    private BudgetDTO budget;
    private VendorManagementDTO vendorManagement;

    private Set<String> organizers; // usernames
    private Set<String> staff;      // usernames

    @Getter
    @Setter
    public static class VenueLayoutDTO {

        private Set<String> stages;
        private Set<String> vendorAreas;
        private Set<String> facilities; // New field
    }

    @Getter
    @Setter
    public static class BudgetDTO {

        private Double tracking;
        private Double costs;
        private Double logistics;
        private Double expectedRevenue;
    }

    @Getter
    @Setter
    public static class VendorManagementDTO {

        private Set<String> foodStalls;
        private Set<String> merchandiseBooths;
    }
}




