package com.housi.backend.controller.v1.admin;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.housi.backend.request.v1.admin.AssignRolesRequest;
import com.housi.backend.response.user.UserResponse;
import com.housi.backend.service.userrole.UserRoleAdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin User Roles", description = "Assign roles to users")
@RestController
@RequestMapping("/api/v1/admin/users")
public class UserRoleController {

    private final UserRoleAdminService userRoleAdminService;

    public UserRoleController(UserRoleAdminService userRoleAdminService) {
        this.userRoleAdminService = userRoleAdminService;
    }

    @Operation(summary = "Replace the roles of a user")
    @PreAuthorize("hasAuthority('admin:write')")
    @PutMapping("/{userId}/roles")
    public UserResponse replaceRoles(
            @PathVariable UUID userId, @Valid @RequestBody AssignRolesRequest request) {
        return userRoleAdminService.replaceUserRoles(userId, request.getRoles());
    }
}
