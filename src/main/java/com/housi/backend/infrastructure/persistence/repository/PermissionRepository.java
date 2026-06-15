package com.housi.backend.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.housi.backend.domain.model.Permission;
import com.housi.backend.domain.port.out.PermissionPort;

@Repository
public interface PermissionRepository extends CrudRepository<Permission, UUID>, PermissionPort {

    @Override
    Optional<Permission> findByName(String name);

    @Override
    boolean existsByName(String name);

    @Override
    @Query("SELECT COUNT(r) FROM Role r JOIN r.permissions p WHERE p.id = :permissionId")
    long countRolesWithPermission(UUID permissionId);
}
