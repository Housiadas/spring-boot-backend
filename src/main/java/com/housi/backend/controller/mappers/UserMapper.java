package com.housi.backend.controller.mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.housi.backend.controller.response.v1.UserResponse;
import com.housi.backend.entity.Permission;
import com.housi.backend.entity.Role;
import com.housi.backend.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(
            target = "fullName",
            expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToNames")
    @Mapping(source = "roles", target = "permissions", qualifiedByName = "permissionsFromRoles")
    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);

    @Named("rolesToNames")
    default Set<String> rolesToNames(Set<Role> roles) {
        if (roles == null) return Set.of();
        return roles.stream().map(Role::getName).collect(Collectors.toUnmodifiableSet());
    }

    @Named("permissionsFromRoles")
    default Set<String> permissionsFromRoles(Set<Role> roles) {
        if (roles == null) return Set.of();
        return roles.stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toUnmodifiableSet());
    }
}
