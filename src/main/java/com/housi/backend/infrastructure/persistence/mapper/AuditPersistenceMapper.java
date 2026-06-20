package com.housi.backend.infrastructure.persistence.mapper;

import org.mapstruct.Mapper;

import com.housi.backend.domain.model.Audit;
import com.housi.backend.infrastructure.persistence.entity.AuditEntity;

@Mapper(componentModel = "spring")
public interface AuditPersistenceMapper {
    Audit toDomain(AuditEntity entity);

    AuditEntity toEntity(Audit domain);
}
