package com.festivalmanager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequest {
    private String requesterUsername;
    private String token; // current session token to invalidate
}
