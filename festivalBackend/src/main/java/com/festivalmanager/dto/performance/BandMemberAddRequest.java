package com.festivalmanager.dto.performance;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BandMemberAddRequest {
    private String requesterUsername; // main artist performing the addition
    private String token;
    private Long performanceId;
    private String newMemberUsername; // the user to be added
}
