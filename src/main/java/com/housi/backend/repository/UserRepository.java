package com.housi.backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.housi.backend.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    @Query(
            "SELECT DISTINCT u FROM User u"
                    + " LEFT JOIN FETCH u.roles r"
                    + " LEFT JOIN FETCH r.permissions"
                    + " WHERE u.email = :email")
    Optional<User> findByEmailWithAuthorities(String email);

    @Query("SELECT DISTINCT u FROM User u" + " LEFT JOIN FETCH u.roles" + " WHERE u.id = :id")
    Optional<User> findByIdWithRoles(UUID id);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = 'ROLE_ADMIN'")
    long countAdminUsers();
}
