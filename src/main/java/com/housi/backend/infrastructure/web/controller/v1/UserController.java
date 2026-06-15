package com.housi.backend.infrastructure.web.controller.v1;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.housi.backend.constant.AppUrls;
import com.housi.backend.infrastructure.web.mapper.UserMapper;
import com.housi.backend.infrastructure.web.request.v1.PasswordUpdateRequest;
import com.housi.backend.infrastructure.web.response.shared.ApiProblemDetail;
import com.housi.backend.infrastructure.web.response.v1.UserResponse;
import com.housi.backend.usecase.user.ChangePasswordUseCase;
import com.housi.backend.usecase.user.DeleteUserUseCase;
import com.housi.backend.usecase.user.GetCurrentUserUseCase;

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

    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final UserMapper userMapper;

    public UserController(
            GetCurrentUserUseCase getCurrentUserUseCase,
            DeleteUserUseCase deleteUserUseCase,
            ChangePasswordUseCase changePasswordUseCase,
            UserMapper userMapper) {
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.userMapper = userMapper;
    }

    @Operation(summary = "Current user information", description = "Get current user details")
    @PreAuthorize("hasAuthority('user:read')")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/current")
    public UserResponse getUserInfo() {
        return userMapper.toResponse(getCurrentUserUseCase.getCurrentUser());
    }

    @Operation(summary = "Delete user", description = "Delete current user account")
    @PreAuthorize("hasAuthority('user:delete')")
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    public void deleteUser() {
        this.deleteUserUseCase.deleteUser();
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
        this.changePasswordUseCase.updatePassword(
                request.oldPassword(), request.newPassword(), request.newPassword2());
    }
}
