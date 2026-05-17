package com.housi.backend.controller.v1.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.housi.backend.response.v1.UserResponse;
import com.housi.backend.service.admin.AdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin Endpoints", description = "Operations related to a admin")
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieve a list of all users in the system")
    @PreAuthorize("hasAuthority('admin:read')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return this.adminService.getAllUsers();
    }

    @Operation(summary = "Promote user to admin", description = "Promote user to admin role")
    @PreAuthorize("hasAuthority('admin:write')")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{userId}/role")
    public UserResponse promoteToAdmin(@PathVariable UUID userId) {
        return this.adminService.promoteToAdmin(userId);
    }

    @Operation(summary = "Delete user", description = "Delete a non-admin user from the system")
    @PreAuthorize("hasAuthority('admin:write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable UUID userId) {
        this.adminService.deleteNonAdminUser(userId);
    }
}
