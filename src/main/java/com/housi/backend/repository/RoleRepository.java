package com.housi.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.housi.backend.entity.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, UUID> {
    Optional<Role> findByName(String name);

    boolean existsByName(String name);

    @EntityGraph(attributePaths = "permissions")
    @Query("SELECT r FROM Role r")
    List<Role> findAllWithPermissions();

    @EntityGraph(attributePaths = "permissions")
    Optional<Role> findWithPermissionsById(UUID id);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.id = :roleId")
    long countUsersWithRole(UUID roleId);
}
