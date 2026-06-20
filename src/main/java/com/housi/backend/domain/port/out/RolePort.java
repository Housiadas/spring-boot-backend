package com.housi.backend.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.housi.backend.domain.model.Role;

public interface RolePort {
    Optional<Role> findByName(String name);

    boolean existsByName(String name);

    List<Role> findAllWithPermissions();

    Optional<Role> findWithPermissionsById(UUID id);

    long countUsersWithRole(UUID roleId);

    Role save(Role role);

    void delete(Role role);
}
