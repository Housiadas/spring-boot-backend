package com.housi.backend.service.permission;

import java.util.List;
import java.util.UUID;

import com.housi.backend.controller.request.v1.PermissionRequest;
import com.housi.backend.controller.response.v1.PermissionResponse;

public interface PermissionAdminService {
    List<PermissionResponse> getAll();

    PermissionResponse getById(UUID id);

    PermissionResponse create(PermissionRequest request);

    PermissionResponse update(UUID id, PermissionRequest request);

    void delete(UUID id);
}
