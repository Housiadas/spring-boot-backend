package com.housi.backend.request.v1.admin;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequest {

    @NotEmpty(message = "Role name is mandatory")
    @Size(max = 50, message = "Role name must be at most 50 characters")
    private String name;

    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;

    private Set<String> permissions;
}
