package com.housi.backend.controller.v1;

import com.housi.backend.constant.AppUrls;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.housi.backend.controller.request.v1.LoginRequest;
import com.housi.backend.controller.request.v1.RegisterRequest;
import com.housi.backend.controller.response.v1.LoginResponse;
import com.housi.backend.service.auth.LoginService;
import com.housi.backend.service.auth.RegisterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping(AuthController.BASE_URL)
@Tag(name = "Authentication Endpoints", description = "Operations related to register & login")
public class AuthController {
    public static final String BASE_URL = AppUrls.V1_AUTH;

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
    public LoginResponse login(@Valid @RequestBody LoginRequest authRequest) {
        return this.loginService.login(authRequest);
    }
}
