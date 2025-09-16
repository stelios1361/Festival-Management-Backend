package com.festivalmanager.dto.festival;


import java.time.LocalDate;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FestivalSearchResultDTO {
    private Long id;
    private String name;
    private String description;
    private String venue;
    private Set<LocalDate> dates;
}
