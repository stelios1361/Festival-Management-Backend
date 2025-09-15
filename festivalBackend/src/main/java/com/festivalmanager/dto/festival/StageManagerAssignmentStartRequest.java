package com.festivalmanager.dto.festival;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StageManagerAssignmentStartRequest {

    private String requesterUsername; // Who wants to start stage manager assignment
    private String token;             // Their token
    private Long festivalId;          // Festival to update
}
