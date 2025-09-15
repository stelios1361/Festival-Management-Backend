package com.festivalmanager.dto.festival;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FestivalCreateRequest {

    private String requesterUsername; // who is making the request
    private String token;             // their token
    private String name;
    private String description;
    private List<LocalDate> dates;       // At least one date
    private String venue;
}
