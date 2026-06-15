package com.housi.backend.infrastructure.web.response.v1;

import java.util.Set;
import java.util.UUID;

public record RoleResponse(UUID id, String name, String description, Set<String> permissions) {}
