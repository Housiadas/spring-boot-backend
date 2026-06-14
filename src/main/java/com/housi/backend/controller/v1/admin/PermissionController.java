package com.housi.backend.controller.v1.admin;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.housi.backend.constant.AppUrls;
import com.housi.backend.controller.mappers.PermissionMapper;
import com.housi.backend.controller.request.v1.PermissionRequest;
import com.housi.backend.controller.response.v1.PermissionResponse;
import com.housi.backend.service.permission.PermissionAdminService;

import com.housi.backend.controller.response.shared.ApiProblemDetail;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin Permissions", description = "Manage RBAC permissions")
@RestController
@RequestMapping(PermissionController.BASE_URL)
public class PermissionController {
    public static final String BASE_URL = AppUrls.V1_ADMIN + "/permissions";

    private final PermissionAdminService permissionAdminService;
    private final PermissionMapper permissionMapper;

    public PermissionController(
            PermissionAdminService permissionAdminService, PermissionMapper permissionMapper) {
        this.permissionAdminService = permissionAdminService;
        this.permissionMapper = permissionMapper;
    }

    @Operation(summary = "List all permissions")
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping
    public List<PermissionResponse> getAll() {
        return permissionMapper.toResponseList(permissionAdminService.getAll());
    }

    @Operation(summary = "Get permission by id")
    @ApiResponse(
            responseCode = "404",
            description = "Permission not found",
            content =
                    @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiProblemDetail.class)))
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping("/{id}")
    public PermissionResponse getById(@PathVariable UUID id) {
        return permissionMapper.toResponse(permissionAdminService.getById(id));
    }

    @Operation(summary = "Create a permission")
    @ApiResponse(responseCode = "201", description = "Permission created successfully")
    @ApiResponse(
            responseCode = "400",
            description = "Validation failed",
            content =
                    @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiProblemDetail.class)))
    @PreAuthorize("hasAuthority('admin:write')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PermissionResponse create(@Valid @RequestBody PermissionRequest request) {
        return permissionMapper.toResponse(
                permissionAdminService.create(request.name(), request.description()));
    }

    @Operation(summary = "Update a permission")
    @ApiResponse(
            responseCode = "400",
            description = "Validation failed",
            content =
                    @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiProblemDetail.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Permission not found",
            content =
                    @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiProblemDetail.class)))
    @PreAuthorize("hasAuthority('admin:write')")
    @PutMapping("/{id}")
    public PermissionResponse update(
            @PathVariable UUID id, @Valid @RequestBody PermissionRequest request) {
        return permissionMapper.toResponse(
                permissionAdminService.update(id, request.name(), request.description()));
    }

    @Operation(summary = "Delete a permission")
    @ApiResponse(
            responseCode = "404",
            description = "Permission not found",
            content =
                    @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiProblemDetail.class)))
    @PreAuthorize("hasAuthority('admin:write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        permissionAdminService.delete(id);
    }
}
