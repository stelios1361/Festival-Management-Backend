package com.festivalmanager.dto;

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
