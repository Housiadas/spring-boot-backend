package com.housi.backend.controller.response.v1;

import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id, String fullName, String email, Set<String> roles, Set<String> permissions) {}
