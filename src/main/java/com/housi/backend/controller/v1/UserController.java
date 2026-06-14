package com.housi.backend.controller.v1;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.housi.backend.constant.AppUrls;
import com.housi.backend.controller.mappers.UserMapper;
import com.housi.backend.controller.request.v1.PasswordUpdateRequest;
import com.housi.backend.controller.response.v1.UserResponse;
import com.housi.backend.service.user.ChangePasswordService;
import com.housi.backend.service.user.DeleteUserService;
import com.housi.backend.service.user.GetCurrentUserService;

import com.housi.backend.controller.response.shared.ApiProblemDetail;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User Endpoints", description = "Operations related to info about current user")
@RestController
@RequestMapping(UserController.BASE_URL)
public class UserController {
    public static final String BASE_URL = AppUrls.V1_USERS;

    private final GetCurrentUserService getCurrentUserService;
    private final DeleteUserService deleteUserService;
    private final ChangePasswordService changePasswordService;
    private final UserMapper userMapper;

    public UserController(
            GetCurrentUserService getCurrentUserService,
            DeleteUserService deleteUserService,
            ChangePasswordService changePasswordService,
            UserMapper userMapper) {
        this.getCurrentUserService = getCurrentUserService;
        this.deleteUserService = deleteUserService;
        this.changePasswordService = changePasswordService;
        this.userMapper = userMapper;
    }

    @Operation(summary = "Current user information", description = "Get current user details")
    @PreAuthorize("hasAuthority('user:read')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/current")
    public UserResponse getUserInfo() {
        return userMapper.toResponse(getCurrentUserService.getCurrentUser());
    }

    @Operation(summary = "Delete user", description = "Delete current user account")
    @PreAuthorize("hasAuthority('user:delete')")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    public void deleteUser() {
        this.deleteUserService.deleteUser();
    }

    @Operation(summary = "Password update", description = "Change user password after verification")
    @ApiResponse(
            responseCode = "400",
            description = "Validation failed or old password mismatch",
            content =
                    @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiProblemDetail.class)))
    @PreAuthorize("hasAuthority('user:write')")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/change/password")
    public void passwordUpdate(@Valid @RequestBody PasswordUpdateRequest request) {
        this.changePasswordService.updatePassword(
                request.oldPassword(), request.newPassword(), request.newPassword2());
    }
}
