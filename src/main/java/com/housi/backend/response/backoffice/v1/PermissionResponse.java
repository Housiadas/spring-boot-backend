package com.housi.backend.response.backoffice.v1;

import java.util.UUID;

public record PermissionResponse(UUID id, String name, String description) {}
