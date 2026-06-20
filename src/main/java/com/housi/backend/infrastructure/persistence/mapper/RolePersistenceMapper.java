package com.housi.backend.infrastructure.persistence.mapper;

import java.util.Set;

import org.mapstruct.Mapper;

import com.housi.backend.domain.model.Role;
import com.housi.backend.infrastructure.persistence.entity.RoleEntity;

@Mapper(
        componentModel = "spring",
        uses = {PermissionPersistenceMapper.class})
public interface RolePersistenceMapper {
    Role toDomain(RoleEntity entity);

    RoleEntity toEntity(Role domain);

    Set<Role> toDomainSet(Set<RoleEntity> entities);

    Set<RoleEntity> toEntitySet(Set<Role> domains);
}
