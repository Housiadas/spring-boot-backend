package com.housi.backend.request.v1.admin;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionRequest {

    @NotEmpty(message = "Permission name is mandatory")
    @Size(max = 100, message = "Permission name must be at most 100 characters")
    private String name;

    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;
}
