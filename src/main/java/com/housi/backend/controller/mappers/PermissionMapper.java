package com.housi.backend.controller.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.housi.backend.controller.response.v1.PermissionResponse;
import com.housi.backend.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionResponse toResponse(Permission permission);

    List<PermissionResponse> toResponseList(List<Permission> permissions);
}
