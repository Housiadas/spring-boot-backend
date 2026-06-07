package com.housi.backend.response.backoffice.v1;

import java.util.Set;
import java.util.UUID;

public record RoleResponse(UUID id, String name, String description, Set<String> permissions) {}
