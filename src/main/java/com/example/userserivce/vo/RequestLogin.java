package com.example.userserivce.vo;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestLogin {

    @NotNull(message = "Email cannot be null")
    @Size(min=2, message = "Email cannot be less than two char")
    @Email
    private String email;

    @NotNull(message = "password cannot be null")
    @Size(min=8, message = "password must be equals or greater than two char")
    private String password;

}
