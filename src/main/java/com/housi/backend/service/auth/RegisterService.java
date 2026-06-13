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
import com.housi.backend.enums.RoleEnum;
import com.housi.backend.repository.RoleRepository;
import com.housi.backend.repository.UserRepository;

@Service
public class RegisterService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(String firstName, String lastName, String email, String password)
            throws Exception {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new Exception("Email already taken");
        }
        userRepository.save(
                new User(
                        firstName,
                        lastName,
                        email,
                        passwordEncoder.encode(password),
                        initialRoles()));
    }

    private Set<Role> initialRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(requireRole(RoleEnum.USER.getName()));
        if (userRepository.count() == 0) {
            roles.add(requireRole(RoleEnum.ADMIN.getName()));
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
}
