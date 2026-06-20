package com.housi.backend.usecase.auth;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.housi.backend.domain.enums.RoleEnum;
import com.housi.backend.domain.exception.ConflictException;
import com.housi.backend.domain.exception.InternalServerErrorException;
import com.housi.backend.domain.exception.ProblemType;
import com.housi.backend.domain.model.Role;
import com.housi.backend.domain.model.User;
import com.housi.backend.domain.port.out.RoleQueryPort;
import com.housi.backend.domain.port.out.UserCommandPort;
import com.housi.backend.domain.port.out.UserQueryPort;

@Service
public class RegisterUseCase {

    private final UserQueryPort userQueryPort;
    private final UserCommandPort userCommandPort;
    private final RoleQueryPort roleQueryPort;
    private final PasswordEncoder passwordEncoder;

    public RegisterUseCase(
            UserQueryPort userQueryPort,
            UserCommandPort userCommandPort,
            RoleQueryPort roleQueryPort,
            PasswordEncoder passwordEncoder) {
        this.userQueryPort = userQueryPort;
        this.userCommandPort = userCommandPort;
        this.roleQueryPort = roleQueryPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(String firstName, String lastName, String email, String password) {
        if (userQueryPort.findByEmail(email).isPresent()) {
            throw new ConflictException(ProblemType.DUPLICATE_EMAIL, "Email already taken.");
        }
        userCommandPort.save(
                User.builder()
                        .firstName(firstName)
                        .lastName(lastName)
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .roles(initialRoles())
                        .build());
    }

    private Set<Role> initialRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(requireRole(RoleEnum.USER.getName()));
        if (userQueryPort.count() == 0) {
            roles.add(requireRole(RoleEnum.ADMIN.getName()));
        }
        return roles;
    }

    private Role requireRole(String name) {
        return roleQueryPort.findByName(name)
                .orElseThrow(
                        () -> new InternalServerErrorException("Required role missing: " + name));
    }
}
