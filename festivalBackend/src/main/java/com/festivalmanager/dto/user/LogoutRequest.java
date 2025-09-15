package com.festivalmanager.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequest {
    private String requesterUsername;
    private String token; // current session token to invalidate
}
