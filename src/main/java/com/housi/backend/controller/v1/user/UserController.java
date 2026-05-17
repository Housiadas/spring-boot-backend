package com.housi.backend.controller.v1.user;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.housi.backend.request.v1.PasswordUpdateRequest;
import com.housi.backend.response.v1.UserResponse;
import com.housi.backend.service.user.ChangePasswordService;
import com.housi.backend.service.user.DeleteUserService;
import com.housi.backend.service.user.GetCurrentUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "User REST API Endpoints",
        description = "Operations related to info about current user")
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
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/current")
    public UserResponse getUserInfo() {
        return this.getCurrentUserService.getUserInfo();
    }

    @Operation(summary = "Delete user", description = "Delete current user account")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    public void deleteUser() {
        this.deleteUserService.deleteUser();
    }

    @Operation(summary = "Password update", description = "Change user password after verification")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/change/password")
    public void passwordUpdate(@Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest)
            throws Exception {
        this.changePasswordService.updatePassword(passwordUpdateRequest);
    }
}
