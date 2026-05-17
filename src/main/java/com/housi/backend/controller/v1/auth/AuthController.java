package com.housi.backend.controller.v1.auth;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.housi.backend.request.v1.AuthenticationRequest;
import com.housi.backend.request.v1.RegisterRequest;
import com.housi.backend.response.v1.AuthenticationResponse;
import com.housi.backend.service.auth.LoginService;
import com.housi.backend.service.auth.RegisterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(
        name = "Authentication Endpoints",
        description = "Operations related to register & login")
public class AuthController {

    private final LoginService loginService;
    private final RegisterService registerService;

    public AuthController(LoginService loginService, RegisterService registerService) {
        this.loginService = loginService;
        this.registerService = registerService;
    }

    @Operation(summary = "Register a user", description = "Create new user in database")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterRequest registerRequest) throws Exception {
        this.registerService.register(registerRequest);
    }

    @Operation(
            summary = "Login a user",
            description = "submit email & password to authenticate user")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public AuthenticationResponse login(@Valid @RequestBody AuthenticationRequest authRequest) {
        return this.loginService.login(authRequest);
    }
}
