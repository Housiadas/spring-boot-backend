package com.housi.backend.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.housi.backend.infrastructure.persistence.entity.PermissionEntity;

@Repository
public interface PermissionJpaRepository extends CrudRepository<PermissionEntity, UUID> {

    Optional<PermissionEntity> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT COUNT(r) FROM RoleEntity r JOIN r.permissions p WHERE p.id = :permissionId")
    long countRolesWithPermission(UUID permissionId);
}
