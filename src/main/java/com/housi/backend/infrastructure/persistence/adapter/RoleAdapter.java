package com.housi.backend.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.housi.backend.domain.model.Role;
import com.housi.backend.domain.port.out.RolePort;
import com.housi.backend.infrastructure.persistence.mapper.RolePersistenceMapper;
import com.housi.backend.infrastructure.persistence.repository.RoleJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoleAdapter implements RolePort {

    private final RoleJpaRepository repository;
    private final RolePersistenceMapper mapper;

    @Override
    public Optional<Role> findByName(String name) {
        return repository.findByName(name).map(mapper::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    @Override
    public List<Role> findAllWithPermissions() {
        return repository.findAllWithPermissions().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Role> findWithPermissionsById(UUID id) {
        return repository.findWithPermissionsById(id).map(mapper::toDomain);
    }

    @Override
    public long countUsersWithRole(UUID roleId) {
        return repository.countUsersWithRole(roleId);
    }

    @Override
    public Role save(Role role) {
        return mapper.toDomain(repository.save(mapper.toEntity(role)));
    }

    @Override
    public void delete(Role role) {
        repository.delete(mapper.toEntity(role));
    }
}
