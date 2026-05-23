package com.housi.backend.service.permission;

import java.util.List;
import java.util.UUID;

import com.housi.backend.request.v1.admin.PermissionRequest;
import com.housi.backend.response.v1.permission.PermissionResponse;

public interface PermissionAdminService {
    List<PermissionResponse> getAll();

    PermissionResponse getById(UUID id);

    PermissionResponse create(PermissionRequest request);

    PermissionResponse update(UUID id, PermissionRequest request);

    void delete(UUID id);
}
