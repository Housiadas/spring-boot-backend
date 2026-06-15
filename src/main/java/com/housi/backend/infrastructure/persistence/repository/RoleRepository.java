package com.housi.backend.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.housi.backend.domain.model.Role;
import com.housi.backend.domain.port.out.RolePort;

@Repository
public interface RoleRepository extends CrudRepository<Role, UUID>, RolePort {

    @Override
    Optional<Role> findByName(String name);

    @Override
    boolean existsByName(String name);

    @Override
    @EntityGraph(attributePaths = "permissions")
    @Query("SELECT r FROM Role r")
    List<Role> findAllWithPermissions();

    @Override
    @EntityGraph(attributePaths = "permissions")
    Optional<Role> findWithPermissionsById(UUID id);

    @Override
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.id = :roleId")
    long countUsersWithRole(UUID roleId);
}
