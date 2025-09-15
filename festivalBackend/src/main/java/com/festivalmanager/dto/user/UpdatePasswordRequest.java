package com.festivalmanager.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordRequest {

    private String requesterUsername;  
    private String token;              
    private String oldPassword;
    private String newPassword1;
    private String newPassword2;


}
