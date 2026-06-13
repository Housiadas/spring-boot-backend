package com.housi.backend.controller.v1;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.housi.backend.constant.AppUrls;
import com.housi.backend.controller.request.v1.LoginRequest;
import com.housi.backend.controller.request.v1.RegisterRequest;
import com.housi.backend.controller.response.v1.LoginResponse;
import com.housi.backend.service.auth.LoginService;
import com.housi.backend.service.auth.RegisterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(AppUrls.V1_AUTH)
@Tag(name = "Authentication Endpoints", description = "Operations related to register & login")
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
    public void register(@Valid @RequestBody RegisterRequest request) throws Exception {
        this.registerService.register(
                request.firstName(), request.lastName(), request.email(), request.password());
    }

    @Operation(
            summary = "Login a user",
            description = "submit email & password to authenticate user")
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return new LoginResponse(this.loginService.login(request.email(), request.password()));
    }
}
