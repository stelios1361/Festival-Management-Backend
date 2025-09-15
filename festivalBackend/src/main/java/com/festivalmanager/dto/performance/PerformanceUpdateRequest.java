package com.festivalmanager.dto.performance;

import com.festivalmanager.model.Performance;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PerformanceUpdateRequest {

    private String requesterUsername; // The artist requesting the update
    private String token;             // Auth token
    private Long performanceId;       // Which performance to update

    private String name;
    private String description;
    private String genre;
    private Integer duration;

    private List<Long> bandMemberIds; // New band members (User IDs)

    private Performance.TechnicalRequirements technicalRequirements;
    private List<String> setlist;
    private List<Performance.MerchandiseItem> merchandiseItems;
    private List<String> preferredRehearsalTimes;
    private List<String> preferredPerformanceSlots;
}
