package com.housi.backend.service.permission;

import java.util.List;
import java.util.UUID;

import com.housi.backend.request.backoffice.v1.PermissionRequest;
import com.housi.backend.response.backoffice.v1.PermissionResponse;

public interface PermissionAdminService {
    List<PermissionResponse> getAll();

    PermissionResponse getById(UUID id);

    PermissionResponse create(PermissionRequest request);

    PermissionResponse update(UUID id, PermissionRequest request);

    void delete(UUID id);
}
