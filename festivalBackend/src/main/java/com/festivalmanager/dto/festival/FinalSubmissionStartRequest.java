package com.festivalmanager.dto.festival;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinalSubmissionStartRequest {

    private String requesterUsername; // Who wants to start final submission
    private String token;             // Their token
    private Long festivalId;          // Festival to update
}
