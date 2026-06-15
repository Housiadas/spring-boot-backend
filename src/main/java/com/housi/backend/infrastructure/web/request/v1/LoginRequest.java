package com.housi.backend.infrastructure.web.request.v1;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotEmpty(message = "Email is mandatory") @Email(message = "Invalid email format")
                String email,
        @NotEmpty(message = "Password is mandatory")
                @Size(min = 5, max = 30, message = "Password must be at least 5 characters long")
                String password) {}
