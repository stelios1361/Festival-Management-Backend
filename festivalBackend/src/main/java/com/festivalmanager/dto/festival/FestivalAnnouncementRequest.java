package com.festivalmanager.dto.festival;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FestivalAnnouncementRequest {

    private String requesterUsername; // Who wants to announce the festival
    private String token;             // Their token
    private Long festivalId;          // Festival to update
}
