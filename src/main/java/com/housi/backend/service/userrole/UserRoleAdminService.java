package com.housi.backend.service.userrole;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.housi.backend.entity.Role;
import com.housi.backend.entity.User;
import com.housi.backend.enums.EntityTransactionAuditEnum;
import com.housi.backend.enums.RoleEnum;
import com.housi.backend.event.EntityAuditEvent;
import com.housi.backend.exception.BadRequestException;
import com.housi.backend.exception.ConflictException;
import com.housi.backend.exception.ProblemType;
import com.housi.backend.exception.ResourceNotFoundException;
import com.housi.backend.repository.RoleRepository;
import com.housi.backend.repository.UserRepository;
import com.housi.backend.service.audit.AuditLogger;

@Service
public class UserRoleAdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuditLogger auditLogger;
    private final ApplicationEventPublisher eventPublisher;

    public UserRoleAdminService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            AuditLogger auditLogger,
            ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.auditLogger = auditLogger;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public User replaceUserRoles(UUID userId, Set<String> roleNames) {
        User user =
                userRepository
                        .findByIdWithRolesAndPermissions(userId)
                        .orElseThrow(() -> new ResourceNotFoundException(ProblemType.USER_NOT_FOUND, "User with id '" + userId + "' not found."));

        Set<String> before =
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

        Set<Role> resolved = new HashSet<>();
        for (String name : roleNames) {
            Role r =
                    roleRepository
                            .findByName(name)
                            .orElseThrow(() -> new BadRequestException(ProblemType.UNKNOWN_ROLE, "Unknown role: " + name));
            resolved.add(r);
        }

        boolean wasAdmin = before.contains(RoleEnum.ADMIN.getName());
        boolean willBeAdmin = roleNames.contains(RoleEnum.ADMIN.getName());
        if (wasAdmin && !willBeAdmin && userRepository.countAdminUsers() <= 1) {
            throw new ConflictException(ProblemType.OPERATION_NOT_ALLOWED, "Cannot remove ADMIN role from the last admin user.");
        }

        user.setRoles(resolved);
        userRepository.save(user);
        auditLogger.userRolesChanged(String.join(",", before), String.join(",", roleNames));
        eventPublisher.publishEvent(
                new EntityAuditEvent(
                        user.getId(),
                        "User",
                        user.getEmail(),
                        EntityTransactionAuditEnum.UPDATE,
                        "roles changed"));
        return userRepository.findByIdWithRolesAndPermissions(userId).orElseThrow();
    }
}
