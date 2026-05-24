package com.housi.backend.service.auth;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.housi.backend.entity.User;
import com.housi.backend.repository.UserRepository;
import com.housi.backend.request.auth.LoginRequest;
import com.housi.backend.response.auth.LoginResponse;
import com.housi.backend.service.audit.AuditLogger;
import com.housi.backend.service.security.JwtService;
import com.housi.backend.service.security.LoginAttemptService;

@Service
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final LoginAttemptService loginAttemptService;
    private final AuditLogger auditLogger;

    public LoginServiceImpl(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            LoginAttemptService loginAttemptService,
            AuditLogger auditLogger) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.loginAttemptService = loginAttemptService;
        this.auditLogger = auditLogger;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail();

        if (loginAttemptService.isBlocked(email)) {
            auditLogger.loginBlocked(email);
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Too many failed login attempts. Try again later.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword()));
        } catch (BadCredentialsException ex) {
            loginAttemptService.recordFailure(email);
            auditLogger.loginFailure(email, "bad_credentials");
            throw ex;
        } catch (AuthenticationException ex) {
            auditLogger.loginFailure(email, ex.getClass().getSimpleName());
            throw ex;
        }

        loginAttemptService.reset(email);

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () -> new IllegalArgumentException("Invalid email or password"));

        String jwtToken = jwtService.generateToken(new HashMap<>(), user);
        auditLogger.loginSuccess(email);
        return new LoginResponse(jwtToken);
    }
}
