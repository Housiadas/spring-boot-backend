package com.housi.backend.infrastructure.web.response.v1;

import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id, String fullName, String email, Set<String> roles, Set<String> permissions) {}
