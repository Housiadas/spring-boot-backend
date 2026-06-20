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
import com.housi.backend.domain.port.out.RolePort;
import com.housi.backend.domain.port.out.UserPort;

@Service
public class RegisterUseCase {

    private final UserPort userPort;
    private final RolePort rolePort;
    private final PasswordEncoder passwordEncoder;

    public RegisterUseCase(UserPort userPort, RolePort rolePort, PasswordEncoder passwordEncoder) {
        this.userPort = userPort;
        this.rolePort = rolePort;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(String firstName, String lastName, String email, String password) {
        if (userPort.findByEmail(email).isPresent()) {
            throw new ConflictException(ProblemType.DUPLICATE_EMAIL, "Email already taken.");
        }
        userPort.save(
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
        if (userPort.count() == 0) {
            roles.add(requireRole(RoleEnum.ADMIN.getName()));
        }
        return roles;
    }

    private Role requireRole(String name) {
        return rolePort.findByName(name)
                .orElseThrow(
                        () -> new InternalServerErrorException("Required role missing: " + name));
    }
}
