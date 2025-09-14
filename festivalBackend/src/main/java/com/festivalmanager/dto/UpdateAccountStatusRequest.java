package com.festivalmanager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateAccountStatusRequest {
    private String requesterUsername;  // who is executing the request
    private String token;              // their token
    private String targetUsername;     // the user whose account status is being changed
    private Boolean newActive;         // true = activate, false = deactivate

}
