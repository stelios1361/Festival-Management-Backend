package com.festivalmanager.dto.festival;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddStaffRequest {

    private String requesterUsername; // Who is making the request
    private String token;             // Their token
    private Long festivalId;          // Festival to update
    private Set<String> usernames;    // Usernames to add as staff
}
