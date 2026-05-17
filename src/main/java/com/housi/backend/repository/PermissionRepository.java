package com.housi.backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.housi.backend.entity.Permission;

@Repository
public interface PermissionRepository extends CrudRepository<Permission, UUID> {
    Optional<Permission> findByName(String name);
}
