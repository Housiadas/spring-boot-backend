package com.housi.backend.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.housi.backend.domain.model.User;
import com.housi.backend.domain.port.out.UserPort;
import com.housi.backend.domain.enums.RoleEnum;

@Repository
public interface UserRepository extends CrudRepository<User, UUID>, UserPort {

    @Override
    Optional<User> findByEmail(String email);

    @Query(
            "SELECT DISTINCT u FROM User u"
                    + " LEFT JOIN FETCH u.roles r"
                    + " LEFT JOIN FETCH r.permissions"
                    + " WHERE u.email = :email")
    Optional<User> findByEmailWithAuthorities(String email);

    @Query("SELECT DISTINCT u FROM User u" + " LEFT JOIN FETCH u.roles" + " WHERE u.id = :id")
    Optional<User> findByIdWithRoles(UUID id);

    @Override
    @Query(
            "SELECT DISTINCT u FROM User u"
                    + " LEFT JOIN FETCH u.roles r"
                    + " LEFT JOIN FETCH r.permissions"
                    + " WHERE u.id = :id")
    Optional<User> findByIdWithRolesAndPermissions(UUID id);

    @Override
    @Query(
            "SELECT DISTINCT u FROM User u"
                    + " LEFT JOIN FETCH u.roles r"
                    + " LEFT JOIN FETCH r.permissions")
    List<User> findAllWithRolesAndPermissions();

    @Override
    boolean existsByEmail(String email);

    @Override
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = '" + RoleEnum.ADMIN_NAME + "'")
    long countAdminUsers();
}
