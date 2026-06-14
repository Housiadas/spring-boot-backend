package com.housi.backend.controller.v1.admin;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.housi.backend.constant.AppUrls;
import com.housi.backend.controller.mappers.UserMapper;
import com.housi.backend.controller.request.v1.AssignRolesRequest;
import com.housi.backend.controller.request.v1.UserRequest;
import com.housi.backend.controller.response.v1.UserResponse;
import com.housi.backend.service.user.UserAdminService;
import com.housi.backend.service.userrole.UserRoleAdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin Users", description = "Manage users")
@RestController
@RequestMapping(UserAdminController.BASE_URL)
public class UserAdminController {
    public static final String BASE_URL = AppUrls.V1_ADMIN + "/users";

    private final UserAdminService userAdminService;
    private final UserRoleAdminService userRoleAdminService;
    private final UserMapper userMapper;

    public UserAdminController(
            UserAdminService userAdminService,
            UserRoleAdminService userRoleAdminService,
            UserMapper userMapper) {
        this.userAdminService = userAdminService;
        this.userRoleAdminService = userRoleAdminService;
        this.userMapper = userMapper;
    }

    @Operation(summary = "List all users")
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping
    public List<UserResponse> getAll() {
        return userMapper.toResponseList(userAdminService.getAll());
    }

    @Operation(summary = "Get user by id")
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable UUID id) {
        return userMapper.toResponse(userAdminService.getById(id));
    }

    @Operation(summary = "Create a user")
    @PreAuthorize("hasAuthority('admin:write')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserResponse create(@Valid @RequestBody UserRequest request) {
        return userMapper.toResponse(
                userAdminService.create(
                        request.firstName(),
                        request.lastName(),
                        request.email(),
                        request.password()));
    }

    @Operation(summary = "Update a user")
    @PreAuthorize("hasAuthority('admin:write')")
    @PutMapping("/{id}")
    public UserResponse update(@PathVariable UUID id, @Valid @RequestBody UserRequest request) {
        return userMapper.toResponse(
                userAdminService.update(
                        id, request.firstName(), request.lastName(), request.email()));
    }

    @Operation(summary = "Delete a user")
    @PreAuthorize("hasAuthority('admin:write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        userAdminService.delete(id);
    }

    @Operation(summary = "Replace the roles of a user")
    @PreAuthorize("hasAuthority('admin:write')")
    @PutMapping("/{id}/roles")
    public UserResponse replaceRoles(
            @PathVariable UUID id, @Valid @RequestBody AssignRolesRequest request) {
        return userMapper.toResponse(userRoleAdminService.replaceUserRoles(id, request.roles()));
    }
}
