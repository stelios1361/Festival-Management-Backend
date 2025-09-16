package com.festivalmanager.dto.festival;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FestivalSearchResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String venue;
    private Set<LocalDate> dates;

    // Optional, only included if requester is an organizer for this festival
    private List<String> organizers;
    private VenueLayoutDTO venueLayout;
    private BudgetDTO budget;
    private VendorManagementDTO vendorManagement;
    private List<String> staff;
}
