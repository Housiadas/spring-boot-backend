package com.housi.backend.response.api.v1;

import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id, String fullName, String email, Set<String> roles, Set<String> permissions) {}
