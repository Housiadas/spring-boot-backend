package com.housi.backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.housi.backend.entity.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, UUID> {
    Optional<Role> findByName(String name);
}
