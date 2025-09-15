package com.festivalmanager.dto.festival;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FestivalViewRequest {

    private String requesterUsername; // optional for VISITOR
    private String token;             // optional for VISITOR
    private Long festivalId;          // the festival to view
}
