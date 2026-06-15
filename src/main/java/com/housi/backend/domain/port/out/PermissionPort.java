package com.housi.backend.domain.port.out;

import java.util.Optional;
import java.util.UUID;

import com.housi.backend.domain.model.Permission;

public interface PermissionPort {
    Optional<Permission> findById(UUID id);

    Iterable<Permission> findAll();

    Optional<Permission> findByName(String name);

    boolean existsByName(String name);

    long countRolesWithPermission(UUID permissionId);

    <S extends Permission> S save(S permission);

    void delete(Permission permission);
}
