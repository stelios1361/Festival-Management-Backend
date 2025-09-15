package com.festivalmanager.dto.festival;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewStartRequest {

    private String requesterUsername; // Who wants to start review
    private String token;             // Their token
    private Long festivalId;          // Festival to update
}
