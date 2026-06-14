package com.housi.backend.service.auth;

import java.util.HashMap;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.housi.backend.entity.User;
import com.housi.backend.exception.NotAuthorizedException;
import com.housi.backend.exception.ProblemType;
import com.housi.backend.exception.TooManyRequestsException;
import com.housi.backend.repository.UserRepository;
import com.housi.backend.service.audit.AuditLogger;
import com.housi.backend.service.security.JwtService;
import com.housi.backend.service.security.LoginAttemptService;

@Service
public class LoginService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final LoginAttemptService loginAttemptService;
    private final AuditLogger auditLogger;

    public LoginService(
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

    @Transactional(readOnly = true)
    public String login(String email, String password) {
        if (loginAttemptService.isBlocked(email)) {
            auditLogger.loginBlocked(email);
            throw new TooManyRequestsException("Too many failed login attempts. Try again later.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
        } catch (BadCredentialsException ex) {
            loginAttemptService.recordFailure(email);
            auditLogger.loginFailure(email, "bad_credentials");
            throw new NotAuthorizedException(ProblemType.INVALID_CREDENTIALS, "Invalid email or password.");
        } catch (AuthenticationException ex) {
            auditLogger.loginFailure(email, ex.getClass().getSimpleName());
            throw new NotAuthorizedException(ProblemType.INVALID_CREDENTIALS, "Authentication failed.");
        }

        loginAttemptService.reset(email);

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () -> new NotAuthorizedException(ProblemType.INVALID_CREDENTIALS, "Invalid email or password."));

        String jwtToken = jwtService.generateToken(new HashMap<>(), user);
        auditLogger.loginSuccess(email);
        return jwtToken;
    }
}
