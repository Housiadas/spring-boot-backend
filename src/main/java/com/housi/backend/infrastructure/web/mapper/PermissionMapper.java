package com.housi.backend.infrastructure.web.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.housi.backend.domain.model.Permission;
import com.housi.backend.infrastructure.web.response.v1.PermissionResponse;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionResponse toResponse(Permission permission);

    List<PermissionResponse> toResponseList(List<Permission> permissions);
}
