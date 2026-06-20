package com.housi.backend.infrastructure.web.request.v1;

import java.util.Set;

public record AssignPermissionsRequest(Set<String> permissions) {}
