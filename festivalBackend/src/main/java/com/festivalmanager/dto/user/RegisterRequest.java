package com.festivalmanager.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    private String username;
    private String fullname;
    private String password1;
    private String password2;
}
