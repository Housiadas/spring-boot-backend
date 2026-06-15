package com.housi.backend.infrastructure.web.controller.v1;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.housi.backend.constant.AppUrls;
import com.housi.backend.infrastructure.web.request.v1.LoginRequest;
import com.housi.backend.infrastructure.web.request.v1.RegisterRequest;
import com.housi.backend.infrastructure.web.response.v1.LoginResponse;
import com.housi.backend.infrastructure.web.response.shared.ApiProblemDetail;
import com.housi.backend.usecase.auth.LoginUseCase;
import com.housi.backend.usecase.auth.RegisterUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(AppUrls.V1_AUTH)
@Tag(name = "Authentication Endpoints", description = "Operations related to register & login")
public class AuthController {
    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;

    public AuthController(LoginUseCase loginUseCase, RegisterUseCase registerUseCase) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
    }

    @Operation(summary = "Register a user", description = "Create new user in database")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(
            responseCode = "400",
            description = "Validation failed",
            content =
                    @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiProblemDetail.class)))
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterRequest request) {
        this.registerUseCase.register(
                request.firstName(), request.lastName(), request.email(), request.password());
    }

    @Operation(
            summary = "Login a user",
            description = "submit email & password to authenticate user")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(
            responseCode = "400",
            description = "Validation failed or invalid credentials",
            content =
                    @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ApiProblemDetail.class)))
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return new LoginResponse(this.loginUseCase.login(request.email(), request.password()));
    }
}
