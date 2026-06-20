package com.housi.backend.infrastructure.web.request.v1;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;

public record AssignRolesRequest(
        @NotEmpty(message = "At least one role is required") Set<String> roles) {}
