package com.festivalmanager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateInfoRequest {

    private String requesterUsername;  // who is logged in
    private String token;              // their token
    private String targetUsername;     // user to update (optional, admin only)
    private String newUsername;        // optional
    private String newFullName;        // optional

}
