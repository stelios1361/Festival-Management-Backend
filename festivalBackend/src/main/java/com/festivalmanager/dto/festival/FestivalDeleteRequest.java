package com.festivalmanager.dto.festival;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FestivalDeleteRequest {

    private String requesterUsername; // Who wants to delete
    private String token;             // Their token
    private Long festivalId;          // Festival to delete
}
