package com.housi.backend.controller.request.v1;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;

public record AssignRolesRequest(
        @NotEmpty(message = "At least one role is required") Set<String> roles) {}
