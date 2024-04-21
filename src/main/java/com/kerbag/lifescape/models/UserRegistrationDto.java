package com.kerbag.lifescape.models;

import lombok.Data;

@Data
public class UserRegistrationDto {

    private String email;
    private String password;
    private String firstName;
    private String lastName;

}