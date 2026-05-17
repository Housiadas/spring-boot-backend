package com.housi.backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.housi.backend.entity.Permission;

@Repository
public interface PermissionRepository extends CrudRepository<Permission, UUID> {
    Optional<Permission> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT COUNT(r) FROM Role r JOIN r.permissions p WHERE p.id = :permissionId")
    long countRolesWithPermission(UUID permissionId);
}
