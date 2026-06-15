package com.housi.backend.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.housi.backend.domain.model.User;

public interface UserPort {
    Optional<User> findByEmail(String email);

    Optional<User> findByIdWithRolesAndPermissions(UUID id);

    List<User> findAllWithRolesAndPermissions();

    boolean existsByEmail(String email);

    long countAdminUsers();

    long count();

    <S extends User> S save(S user);

    void delete(User user);
}
