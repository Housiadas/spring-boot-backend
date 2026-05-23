package com.housi.backend.service.auth;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.housi.backend.entity.Role;
import com.housi.backend.entity.User;
import com.housi.backend.repository.RoleRepository;
import com.housi.backend.repository.UserRepository;
import com.housi.backend.request.v1.auth.RegisterRequest;

@Service
public class RegisterServiceImpl implements RegisterService {

    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void register(RegisterRequest input) throws Exception {
        if (isEmailTaken(input.getEmail())) {
            throw new Exception("Email already taken");
        }
        userRepository.save(buildNewUser(input));
    }

    private boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private Set<Role> initialRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(requireRole(ROLE_USER));
        if (userRepository.count() == 0) {
            roles.add(requireRole(ROLE_ADMIN));
        }
        return roles;
    }

    private Role requireRole(String name) {
        return roleRepository
                .findByName(name)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.INTERNAL_SERVER_ERROR,
                                        "Required role missing: " + name));
    }

    private User buildNewUser(RegisterRequest input) {
        return new User(
                input.getFirstName(),
                input.getLastName(),
                input.getEmail(),
                passwordEncoder.encode(input.getPassword()),
                initialRoles());
    }
}
