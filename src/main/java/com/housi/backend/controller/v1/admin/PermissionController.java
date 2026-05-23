package com.housi.backend.controller.v1.admin;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.housi.backend.request.v1.admin.PermissionRequest;
import com.housi.backend.response.v1.permission.PermissionResponse;
import com.housi.backend.service.permission.PermissionAdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin Permissions", description = "Manage RBAC permissions")
@RestController
@RequestMapping("/api/v1/admin/permissions")
public class PermissionController {

    private final PermissionAdminService permissionAdminService;

    public PermissionController(PermissionAdminService permissionAdminService) {
        this.permissionAdminService = permissionAdminService;
    }

    @Operation(summary = "List all permissions")
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping
    public List<PermissionResponse> getAll() {
        return permissionAdminService.getAll();
    }

    @Operation(summary = "Get permission by id")
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping("/{id}")
    public PermissionResponse getById(@PathVariable UUID id) {
        return permissionAdminService.getById(id);
    }

    @Operation(summary = "Create a permission")
    @PreAuthorize("hasAuthority('admin:write')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PermissionResponse create(@Valid @RequestBody PermissionRequest request) {
        return permissionAdminService.create(request);
    }

    @Operation(summary = "Update a permission")
    @PreAuthorize("hasAuthority('admin:write')")
    @PutMapping("/{id}")
    public PermissionResponse update(
            @PathVariable UUID id, @Valid @RequestBody PermissionRequest request) {
        return permissionAdminService.update(id, request);
    }

    @Operation(summary = "Delete a permission")
    @PreAuthorize("hasAuthority('admin:write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        permissionAdminService.delete(id);
    }
}
