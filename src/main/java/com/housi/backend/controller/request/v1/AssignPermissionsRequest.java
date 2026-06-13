package com.housi.backend.controller.request.v1;

import java.util.Set;

public record AssignPermissionsRequest(Set<String> permissions) {}
