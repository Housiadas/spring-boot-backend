package com.housi.backend.controller.v1.admin;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.housi.backend.constant.AppUrls;
import com.housi.backend.controller.mappers.RoleMapper;
import com.housi.backend.controller.request.v1.AssignPermissionsRequest;
import com.housi.backend.controller.request.v1.RoleRequest;
import com.housi.backend.controller.response.v1.RoleResponse;
import com.housi.backend.service.role.RoleAdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin Roles", description = "Manage RBAC roles")
@RestController
@RequestMapping(RoleController.BASE_URL)
public class RoleController {
    public static final String BASE_URL = AppUrls.V1_ADMIN + "/roles";

    private final RoleAdminService roleAdminService;
    private final RoleMapper roleMapper;

    public RoleController(RoleAdminService roleAdminService, RoleMapper roleMapper) {
        this.roleAdminService = roleAdminService;
        this.roleMapper = roleMapper;
    }

    @Operation(summary = "List all roles")
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping
    public List<RoleResponse> getAll() {
        return roleMapper.toResponseList(roleAdminService.getAll());
    }

    @Operation(summary = "Get role by id")
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping("/{id}")
    public RoleResponse getById(@PathVariable UUID id) {
        return roleMapper.toResponse(roleAdminService.getById(id));
    }

    @Operation(summary = "Create a role")
    @PreAuthorize("hasAuthority('admin:write')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public RoleResponse create(@Valid @RequestBody RoleRequest request) {
        return roleMapper.toResponse(
                roleAdminService.create(
                        request.name(), request.description(), request.permissions()));
    }

    @Operation(summary = "Update a role")
    @PreAuthorize("hasAuthority('admin:write')")
    @PutMapping("/{id}")
    public RoleResponse update(@PathVariable UUID id, @Valid @RequestBody RoleRequest request) {
        return roleMapper.toResponse(
                roleAdminService.update(
                        id, request.name(), request.description(), request.permissions()));
    }

    @Operation(summary = "Delete a role")
    @PreAuthorize("hasAuthority('admin:write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        roleAdminService.delete(id);
    }

    @Operation(summary = "Replace the permissions of a role")
    @PreAuthorize("hasAuthority('admin:write')")
    @PutMapping("/{id}/permissions")
    public RoleResponse replacePermissions(
            @PathVariable UUID id, @Valid @RequestBody AssignPermissionsRequest request) {
        return roleMapper.toResponse(
                roleAdminService.replacePermissions(id, request.permissions()));
    }
}
