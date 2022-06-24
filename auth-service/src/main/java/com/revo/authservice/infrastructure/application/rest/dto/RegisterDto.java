package com.revo.authservice.infrastructure.application.rest.dto;

public class RegisterDto {

    private String username;
    private String password;
    private String email;

    public RegisterDto(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
