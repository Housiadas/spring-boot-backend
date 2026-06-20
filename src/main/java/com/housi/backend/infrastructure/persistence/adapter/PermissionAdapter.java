package com.housi.backend.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Component;

import com.housi.backend.domain.model.Permission;
import com.housi.backend.domain.port.out.PermissionPort;
import com.housi.backend.infrastructure.persistence.mapper.PermissionPersistenceMapper;
import com.housi.backend.infrastructure.persistence.repository.PermissionJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PermissionAdapter implements PermissionPort {

    private final PermissionJpaRepository repository;
    private final PermissionPersistenceMapper mapper;

    @Override
    public Optional<Permission> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Iterable<Permission> findAll() {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Permission> findByName(String name) {
        return repository.findByName(name).map(mapper::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    @Override
    public long countRolesWithPermission(UUID permissionId) {
        return repository.countRolesWithPermission(permissionId);
    }

    @Override
    public Permission save(Permission permission) {
        return mapper.toDomain(repository.save(mapper.toEntity(permission)));
    }

    @Override
    public void delete(Permission permission) {
        repository.delete(mapper.toEntity(permission));
    }
}
