package com.housi.backend.infrastructure.web.mapper;

import org.mapstruct.Mapper;

import com.housi.backend.domain.model.Audit;
import com.housi.backend.infrastructure.web.response.v1.AuditResponse;

@Mapper(componentModel = "spring")
public interface AuditMapper {

    AuditResponse toResponse(Audit audit);
}
