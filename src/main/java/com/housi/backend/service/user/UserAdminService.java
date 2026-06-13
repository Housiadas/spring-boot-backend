package com.housi.backend.service.user;

import java.util.List;
import java.util.Set;
import java.util.UUID;

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
import com.housi.backend.service.audit.AuditLogger;

@Service
@Transactional(readOnly = true)
public class UserAdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogger auditLogger;

    public UserAdminService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuditLogger auditLogger) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogger = auditLogger;
    }

    public List<User> getAll() {
        return userRepository.findAllWithRolesAndPermissions();
    }

    public User getById(UUID id) {
        return requireUser(id);
    }

    @Transactional
    public User create(String firstName, String lastName, String email, String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already taken");
        }
        User user =
                new User(
                        firstName,
                        lastName,
                        email,
                        passwordEncoder.encode(rawPassword),
                        Set.of(requireRole(RoleEnum.USER.getName())));
        userRepository.save(user);
        auditLogger.userAdminCreated(user.getEmail());
        return requireUser(user.getId());
    }

    @Transactional
    public User update(UUID id, String firstName, String lastName, String email) {
        User user = requireUser(id);
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already taken");
        }
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        userRepository.save(user);
        auditLogger.userAdminUpdated(user.getEmail());
        return requireUser(id);
    }

    @Transactional
    public void delete(UUID id) {
        User user = requireUser(id);
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equals(RoleEnum.ADMIN.getName()));
        if (isAdmin && userRepository.countAdminUsers() <= 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Cannot delete the last admin user");
        }
        auditLogger.userAdminDeleted(user.getEmail());
        userRepository.delete(user);
    }

    private User requireUser(UUID id) {
        return userRepository
                .findByIdWithRolesAndPermissions(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
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
