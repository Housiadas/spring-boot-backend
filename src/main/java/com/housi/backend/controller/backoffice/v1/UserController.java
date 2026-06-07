package com.housi.backend.controller.backoffice.v1;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.housi.backend.constant.AppUrls;
import com.housi.backend.request.backoffice.v1.AssignRolesRequest;
import com.housi.backend.response.api.v1.UserResponse;
import com.housi.backend.service.userrole.UserRoleAdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin User Roles", description = "Assign roles to users")
@RestController
@RequestMapping(UserController.BASE_URL)
public class UserController {
    public static final String BASE_URL = AppUrls.V1_BACKOFFICE + "/users";

    private final UserRoleAdminService userRoleAdminService;

    public UserController(UserRoleAdminService userRoleAdminService) {
        this.userRoleAdminService = userRoleAdminService;
    }

    @Operation(summary = "Replace the roles of a user")
    @PreAuthorize("hasAuthority('admin:write')")
    @PutMapping("/{userId}/roles")
    public UserResponse replaceRoles(
            @PathVariable UUID userId, @Valid @RequestBody AssignRolesRequest request) {
        return userRoleAdminService.replaceUserRoles(userId, request.roles());
    }
}
