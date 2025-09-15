package com.festivalmanager.dto.festival;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DecisionMakingRequest {

    private String requesterUsername; // Who wants to make the decision
    private String token;             // Their token
    private Long festivalId;          // Festival to update
}
