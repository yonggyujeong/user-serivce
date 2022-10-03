package com.example.userserivce.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class RequestUser {

    @NotNull(message = "email cannot be null")
    @Size(min=2, message = "email cannot be less than 2 chars")
    @Email
    private String email;

    @NotNull(message = "name cannot be null")
    @Size(min=2, message = "name cannot be less than 2 chars")
    private String name;

    @NotNull(message = "pw cannot be null")
    @Size(min=8, message = "pw must be equal and greater than 8 chars")
    private String pw;

}
