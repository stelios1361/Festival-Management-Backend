package com.festivalmanager.dto;

public class RegisterRequest {

    private String username;
    private String fullname;
    private String password1;
    private String password2;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password) {
        this.password1 = password;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password) {
        this.password2 = password;
    }

}
