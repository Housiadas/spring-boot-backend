package com.housi.backend.controller.request.v1;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "First name is mandatory") @Size(max = 100) String firstName,
        @NotBlank(message = "Last name is mandatory") @Size(max = 100) String lastName,
        @NotBlank @Email(message = "Must be a valid email") @Size(max = 100) String email,
        String password) {}
