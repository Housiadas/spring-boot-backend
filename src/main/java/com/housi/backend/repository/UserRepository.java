package com.housi.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.housi.backend.entity.User;
import com.housi.backend.enums.RoleEnum;

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

    @Query(
            "SELECT DISTINCT u FROM User u"
                    + " LEFT JOIN FETCH u.roles r"
                    + " LEFT JOIN FETCH r.permissions"
                    + " WHERE u.id = :id")
    Optional<User> findByIdWithRolesAndPermissions(UUID id);

    @Query(
            "SELECT DISTINCT u FROM User u"
                    + " LEFT JOIN FETCH u.roles r"
                    + " LEFT JOIN FETCH r.permissions")
    List<User> findAllWithRolesAndPermissions();

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = '" + RoleEnum.ADMIN_NAME + "'")
    long countAdminUsers();
}
