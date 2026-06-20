package com.housi.backend.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.housi.backend.infrastructure.persistence.entity.RoleEntity;

@Repository
public interface RoleJpaRepository extends CrudRepository<RoleEntity, UUID> {

    Optional<RoleEntity> findByName(String name);

    boolean existsByName(String name);

    @EntityGraph(attributePaths = "permissions")
    @Query("SELECT r FROM RoleEntity r")
    List<RoleEntity> findAllWithPermissions();

    @EntityGraph(attributePaths = "permissions")
    Optional<RoleEntity> findWithPermissionsById(UUID id);

    @Query("SELECT COUNT(u) FROM UserEntity u JOIN u.roles r WHERE r.id = :roleId")
    long countUsersWithRole(UUID roleId);
}
