package com.festivalmanager.dto.festival;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleMakingRequest {

    private String requesterUsername; // Who wants to start schedule making
    private String token;             // Their token
    private Long festivalId;          // Festival to update
}
