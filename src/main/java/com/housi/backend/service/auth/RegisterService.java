package com.housi.backend.service.auth;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.housi.backend.entity.Role;
import com.housi.backend.entity.User;
import com.housi.backend.enums.RoleEnum;
import com.housi.backend.exception.ConflictException;
import com.housi.backend.exception.InternalServerErrorException;
import com.housi.backend.exception.ProblemType;
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
    public void register(String firstName, String lastName, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ConflictException(ProblemType.DUPLICATE_EMAIL, "Email already taken.");
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
                .orElseThrow(() -> new InternalServerErrorException("Required role missing: " + name));
    }
}
