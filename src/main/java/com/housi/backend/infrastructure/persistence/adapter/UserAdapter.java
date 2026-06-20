package com.housi.backend.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.housi.backend.domain.model.User;
import com.housi.backend.domain.port.out.UserCommandPort;
import com.housi.backend.domain.port.out.UserQueryPort;
import com.housi.backend.infrastructure.persistence.entity.UserEntity;
import com.housi.backend.infrastructure.persistence.mapper.UserPersistenceMapper;
import com.housi.backend.infrastructure.persistence.repository.UserJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserAdapter implements UserQueryPort, UserCommandPort {

    private final UserJpaRepository repository;
    private final UserPersistenceMapper mapper;

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByIdWithRolesAndPermissions(UUID id) {
        return repository.findByIdWithRolesAndPermissions(id).map(mapper::toDomain);
    }

    @Override
    public List<User> findAllWithRolesAndPermissions() {
        return mapper.toDomainList(repository.findAllWithRolesAndPermissions());
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public long countAdminUsers() {
        return repository.countAdminUsers();
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public void delete(User user) {
        repository.delete(mapper.toEntity(user));
    }
}
