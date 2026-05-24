package com.housi.backend.controller.v1.user;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.housi.backend.request.v1.user.PasswordUpdateRequest;
import com.housi.backend.response.user.UserResponse;
import com.housi.backend.service.user.ChangePasswordService;
import com.housi.backend.service.user.DeleteUserService;
import com.housi.backend.service.user.GetCurrentUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User Endpoints", description = "Operations related to info about current user")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final GetCurrentUserService getCurrentUserService;
    private final DeleteUserService deleteUserService;
    private final ChangePasswordService changePasswordService;

    public UserController(
            GetCurrentUserService getCurrentUserService,
            DeleteUserService deleteUserService,
            ChangePasswordService changePasswordService) {
        this.getCurrentUserService = getCurrentUserService;
        this.deleteUserService = deleteUserService;
        this.changePasswordService = changePasswordService;
    }

    @Operation(summary = "Current user information", description = "Get current user details")
    @PreAuthorize("hasAuthority('user:read')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/current")
    public UserResponse getUserInfo() {
        return this.getCurrentUserService.getUserInfo();
    }

    @Operation(summary = "Delete user", description = "Delete current user account")
    @PreAuthorize("hasAuthority('user:delete')")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    public void deleteUser() {
        this.deleteUserService.deleteUser();
    }

    @Operation(summary = "Password update", description = "Change user password after verification")
    @PreAuthorize("hasAuthority('user:write')")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/change/password")
    public void passwordUpdate(@Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest)
            throws Exception {
        this.changePasswordService.updatePassword(passwordUpdateRequest);
    }
}
