package com.housi.backend.request.api.v1;

import jakarta.validation.constraints.Email;

public record UserRequest(
        String firstName,
        String lastName,
        @Email(message = "The input must be an valid email") String email,
        String password) {}
