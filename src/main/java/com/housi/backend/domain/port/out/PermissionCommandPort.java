package com.housi.backend.domain.port.out;

import com.housi.backend.domain.model.Permission;

public interface PermissionCommandPort {
    Permission save(Permission permission);

    void delete(Permission permission);
}
