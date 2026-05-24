package com.housi.backend.request.admin;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignRolesRequest {

    @NotEmpty(message = "At least one role is required")
    private Set<String> roles;
}
