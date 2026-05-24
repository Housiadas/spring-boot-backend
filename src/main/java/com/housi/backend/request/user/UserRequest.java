package com.housi.backend.request.user;

import jakarta.validation.constraints.Email;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRequest {
    private String firstName;
    private String lastName;

    @Email(message = "The input must be an valid email")
    private String email;

    private String password;
}
