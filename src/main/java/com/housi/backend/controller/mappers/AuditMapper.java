package com.housi.backend.controller.mappers;

import org.mapstruct.Mapper;

import com.housi.backend.controller.response.v1.AuditResponse;
import com.housi.backend.entity.Audit;

@Mapper(componentModel = "spring")
public interface AuditMapper {

    AuditResponse toResponse(Audit audit);
}
