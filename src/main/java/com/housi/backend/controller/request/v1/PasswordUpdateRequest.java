package com.housi.backend.controller.request.v1;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record PasswordUpdateRequest(
        @NotEmpty(message = "Old password is mandatory")
                @Size(
                        min = 5,
                        max = 30,
                        message = "Old password must be at least 5 characters long")
                String oldPassword,
        @NotEmpty(message = "New password is mandatory")
                @Size(
                        min = 5,
                        max = 30,
                        message = "New password must be at least 5 characters long")
                String newPassword,
        @NotEmpty(message = "Confirmed password is mandatory")
                @Size(
                        min = 5,
                        max = 30,
                        message = "Confirmed password must be at least 5 characters long")
                String newPassword2) {}
