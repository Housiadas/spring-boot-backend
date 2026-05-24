package com.housi.backend.service.userrole;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.housi.backend.entity.Permission;
import com.housi.backend.entity.Role;
import com.housi.backend.entity.User;
import com.housi.backend.repository.RoleRepository;
import com.housi.backend.repository.UserRepository;
import com.housi.backend.response.user.UserResponse;
import com.housi.backend.service.audit.AuditLogger;

@Service
public class UserRoleAdminServiceImpl implements UserRoleAdminService {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuditLogger auditLogger;

    public UserRoleAdminServiceImpl(
            UserRepository userRepository, RoleRepository roleRepository, AuditLogger auditLogger) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.auditLogger = auditLogger;
    }

    @Override
    @Transactional
    public UserResponse replaceUserRoles(UUID userId, Set<String> roleNames) {
        User user =
                userRepository
                        .findByIdWithRoles(userId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "User not found"));

        Set<String> before =
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

        Set<Role> resolved = new HashSet<>();
        for (String name : roleNames) {
            Role r =
                    roleRepository
                            .findByName(name)
                            .orElseThrow(
                                    () ->
                                            new ResponseStatusException(
                                                    HttpStatus.BAD_REQUEST,
                                                    "Unknown role: " + name));
            resolved.add(r);
        }

        boolean wasAdmin = before.contains(ROLE_ADMIN);
        boolean willBeAdmin = roleNames.contains(ROLE_ADMIN);
        if (wasAdmin && !willBeAdmin && userRepository.countAdminUsers() <= 1) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Cannot remove ROLE_ADMIN from the last admin");
        }

        user.setRoles(resolved);
        User saved = userRepository.save(user);

        auditLogger.userRolesChanged(String.join(",", before), String.join(",", roleNames));

        return toResponse(saved);
    }

    private UserResponse toResponse(User user) {
        Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        Set<String> permissions =
                user.getRoles().stream()
                        .flatMap(r -> r.getPermissions().stream())
                        .map(Permission::getName)
                        .collect(Collectors.toSet());
        return new UserResponse(
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                roles,
                permissions);
    }
}
