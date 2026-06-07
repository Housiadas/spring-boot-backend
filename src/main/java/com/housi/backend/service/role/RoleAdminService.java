package com.housi.backend.service.role;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.housi.backend.request.backoffice.v1.RoleRequest;
import com.housi.backend.response.backoffice.v1.RoleResponse;

public interface RoleAdminService {
    List<RoleResponse> getAll();

    RoleResponse getById(UUID id);

    RoleResponse create(RoleRequest request);

    RoleResponse update(UUID id, RoleRequest request);

    void delete(UUID id);

    RoleResponse replacePermissions(UUID id, Set<String> permissionNames);
}
