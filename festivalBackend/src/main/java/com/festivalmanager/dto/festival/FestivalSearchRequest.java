package com.festivalmanager.dto.festival;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FestivalSearchRequest {

    private String requesterUsername; // optional, null for VISITOR
    private String token;             // optional, null for VISITOR

    // Search criteria
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String venue;
}
