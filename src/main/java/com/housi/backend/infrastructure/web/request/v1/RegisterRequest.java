package com.housi.backend.infrastructure.web.request.v1;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotEmpty(message = "First name is mandatory")
                @Size(min = 3, max = 30, message = "First name must be at least 3 characters long")
                String firstName,
        @NotEmpty(message = "Last name is mandatory")
                @Size(min = 3, max = 30, message = "Last name must be at least 3 characters long")
                String lastName,
        @NotEmpty(message = "Email is mandatory") @Email(message = "Invalid email format")
                String email,
        @NotEmpty(message = "Password is mandatory")
                @Size(min = 5, max = 30, message = "Password must be at least 5 characters long")
                String password) {}
