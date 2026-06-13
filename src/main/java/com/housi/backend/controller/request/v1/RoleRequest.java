package com.housi.backend.controller.request.v1;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record RoleRequest(
        @NotEmpty(message = "Role name is mandatory")
                @Size(max = 50, message = "Role name must be at most 50 characters")
                String name,
        @Size(max = 255, message = "Description must be at most 255 characters") String description,
        Set<String> permissions) {}
