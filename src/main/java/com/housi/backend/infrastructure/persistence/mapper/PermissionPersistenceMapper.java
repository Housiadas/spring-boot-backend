package com.housi.backend.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;

import com.housi.backend.domain.model.Permission;
import com.housi.backend.infrastructure.persistence.entity.PermissionEntity;

@Mapper(componentModel = "spring")
public interface PermissionPersistenceMapper {
    Permission toDomain(PermissionEntity entity);

    PermissionEntity toEntity(Permission domain);
}
