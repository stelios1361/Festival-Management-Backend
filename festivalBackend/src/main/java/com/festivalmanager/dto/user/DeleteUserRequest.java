package com.festivalmanager.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteUserRequest {
    private String requesterUsername; // who is making the request
    private String token;             // their token
    private String targetUsername;    // the user to delete 
    
}
