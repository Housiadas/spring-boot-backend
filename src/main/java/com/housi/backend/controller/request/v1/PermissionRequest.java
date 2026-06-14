package com.housi.backend.controller.request.v1;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record PermissionRequest(
        @NotEmpty(message = "Permission name is mandatory")
                @Size(max = 100, message = "Permission name must be at most 100 characters")
                String name,
        @Size(max = 255, message = "Description must be at most 255 characters")
                String description) {}
