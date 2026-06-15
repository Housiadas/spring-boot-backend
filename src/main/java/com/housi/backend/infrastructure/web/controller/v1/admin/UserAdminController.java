package com.housi.backend.infrastructure.web.controller.v1.admin;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.housi.backend.constant.AppUrls;
import com.housi.backend.infrastructure.web.mapper.UserMapper;
import com.housi.backend.infrastructure.web.request.v1.AssignRolesRequest;
import com.housi.backend.infrastructure.web.request.v1.UserRequest;
import com.housi.backend.infrastructure.web.response.shared.ApiProblemDetail;
import com.housi.backend.infrastructure.web.response.v1.UserResponse;
import com.housi.backend.usecase.user.UserAdminUseCase;
import com.housi.backend.usecase.userrole.UserRoleAdminUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Admin Users", description = "Manage users")
@RestController
@RequestMapping(UserAdminController.BASE_URL)
public class UserAdminController {
    public static final String BASE_URL = AppUrls.V1_ADMIN + "/users";

    private final UserAdminUseCase userAdminUseCase;
    private final UserRoleAdminUseCase userRoleAdminUseCase;
    private final UserMapper userMapper;

    public UserAdminController(
            UserAdminUseCase userAdminUseCase,
            UserRoleAdminUseCase userRoleAdminUseCase,
            UserMapper userMapper) {
        this.userAdminUseCase = userAdminUseCase;
        this.userRoleAdminUseCase = userRoleAdminUseCase;
        this.userMapper = userMapper;
    }

    @Operation(summary = "List all users")
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping
    public List<UserResponse> getAll() {
        return userMapper.toResponseList(userAdminUseCase.getAll());
    }

    @Operation(summary = "Get user by id")
    @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content =
                    @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiProblemDetail.class)))
    @PreAuthorize("hasAuthority('admin:read')")
    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable UUID id) {
        return userMapper.toResponse(userAdminUseCase.getById(id));
    }

    @Operation(summary = "Create a user")
    @ApiResponse(responseCode = "201", description = "User created successfully")
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
    public UserResponse create(@Valid @RequestBody UserRequest request) {
        return userMapper.toResponse(
                userAdminUseCase.create(
                        request.firstName(),
                        request.lastName(),
                        request.email(),
                        request.password()));
    }

    @Operation(summary = "Update a user")
    @ApiResponse(
            responseCode = "400",
            description = "Validation failed",
            content =
                    @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiProblemDetail.class)))
    @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content =
                    @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiProblemDetail.class)))
    @PreAuthorize("hasAuthority('admin:write')")
    @PutMapping("/{id}")
    public UserResponse update(@PathVariable UUID id, @Valid @RequestBody UserRequest request) {
        return userMapper.toResponse(
                userAdminUseCase.update(
                        id, request.firstName(), request.lastName(), request.email()));
    }

    @Operation(summary = "Delete a user")
    @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content =
                    @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiProblemDetail.class)))
    @PreAuthorize("hasAuthority('admin:write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        userAdminUseCase.delete(id);
    }

    @Operation(summary = "Replace the roles of a user")
    @ApiResponse(
            responseCode = "400",
            description = "Validation failed",
            content =
                    @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiProblemDetail.class)))
    @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content =
                    @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiProblemDetail.class)))
    @PreAuthorize("hasAuthority('admin:write')")
    @PutMapping("/{id}/roles")
    public UserResponse replaceRoles(
            @PathVariable UUID id, @Valid @RequestBody AssignRolesRequest request) {
        return userMapper.toResponse(userRoleAdminUseCase.replaceUserRoles(id, request.roles()));
    }
}
