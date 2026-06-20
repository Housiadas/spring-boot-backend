package com.housi.backend.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.housi.backend.domain.enums.RoleEnum;
import com.housi.backend.infrastructure.persistence.entity.UserEntity;

@Repository
public interface UserJpaRepository extends CrudRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);

    @Query(
            "SELECT DISTINCT u FROM UserEntity u"
                    + " LEFT JOIN FETCH u.roles r"
                    + " LEFT JOIN FETCH r.permissions"
                    + " WHERE u.email = :email")
    Optional<UserEntity> findByEmailWithAuthorities(String email);

    @Query(
            "SELECT DISTINCT u FROM UserEntity u"
                    + " LEFT JOIN FETCH u.roles r"
                    + " LEFT JOIN FETCH r.permissions"
                    + " WHERE u.id = :id")
    Optional<UserEntity> findByIdWithRolesAndPermissions(UUID id);

    @Query(
            "SELECT DISTINCT u FROM UserEntity u"
                    + " LEFT JOIN FETCH u.roles r"
                    + " LEFT JOIN FETCH r.permissions")
    List<UserEntity> findAllWithRolesAndPermissions();

    boolean existsByEmail(String email);

    @Query(
            "SELECT COUNT(u) FROM UserEntity u JOIN u.roles r WHERE r.name = '"
                    + RoleEnum.ADMIN_NAME
                    + "'")
    long countAdminUsers();
}
