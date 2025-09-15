package com.festivalmanager.dto.festival;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmissionStartRequest {

    private String requesterUsername; // Who wants to start submission
    private String token;             // Their token
    private Long festivalId;          // Festival to start submission
}
