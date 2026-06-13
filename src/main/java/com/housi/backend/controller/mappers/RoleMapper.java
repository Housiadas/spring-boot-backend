package com.housi.backend.controller.mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.housi.backend.controller.response.v1.RoleResponse;
import com.housi.backend.entity.Permission;
import com.housi.backend.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(source = "permissions", target = "permissions", qualifiedByName = "permissionsToNames")
    RoleResponse toResponse(Role role);

    List<RoleResponse> toResponseList(List<Role> roles);

    @Named("permissionsToNames")
    default Set<String> permissionsToNames(Set<Permission> permissions) {
        if (permissions == null) return Set.of();
        return permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toUnmodifiableSet());
    }
}
