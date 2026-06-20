package com.housi.backend.infrastructure.web.response.v1;

import java.util.UUID;

public record PermissionResponse(UUID id, String name, String description) {}
