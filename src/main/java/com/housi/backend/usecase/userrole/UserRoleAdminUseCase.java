package com.housi.backend.usecase.userrole;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.housi.backend.domain.enums.EntityTransactionAuditEnum;
import com.housi.backend.domain.event.EntityAuditEvent;
import com.housi.backend.domain.exception.BadRequestException;
import com.housi.backend.domain.exception.ProblemType;
import com.housi.backend.domain.exception.ResourceNotFoundException;
import com.housi.backend.domain.model.Role;
import com.housi.backend.domain.model.User;
import com.housi.backend.domain.port.out.RolePort;
import com.housi.backend.domain.port.out.UserPort;
import com.housi.backend.infrastructure.audit.AuditLogger;
import com.housi.backend.usecase.user.LastAdminGuard;

@Service
public class UserRoleAdminUseCase {

    private final UserPort userPort;
    private final RolePort rolePort;
    private final AuditLogger auditLogger;
    private final ApplicationEventPublisher eventPublisher;
    private final LastAdminGuard lastAdminGuard;

    public UserRoleAdminUseCase(
            UserPort userPort,
            RolePort rolePort,
            AuditLogger auditLogger,
            ApplicationEventPublisher eventPublisher,
            LastAdminGuard lastAdminGuard) {
        this.userPort = userPort;
        this.rolePort = rolePort;
        this.auditLogger = auditLogger;
        this.eventPublisher = eventPublisher;
        this.lastAdminGuard = lastAdminGuard;
    }

    @Transactional
    public User replaceUserRoles(UUID userId, Set<String> roleNames) {
        User user =
                userPort.findByIdWithRolesAndPermissions(userId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                ProblemType.USER_NOT_FOUND,
                                                "User with id '" + userId + "' not found."));

        Set<String> before =
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

        Set<Role> resolved = new HashSet<>();
        for (String name : roleNames) {
            Role r =
                    rolePort.findByName(name)
                            .orElseThrow(
                                    () ->
                                            new BadRequestException(
                                                    ProblemType.UNKNOWN_ROLE,
                                                    "Unknown role: " + name));
            resolved.add(r);
        }

        lastAdminGuard.assertAdminRoleRemovalAllowed(user, roleNames);

        user.setRoles(resolved);
        userPort.save(user);
        auditLogger.userRolesChanged(String.join(",", before), String.join(",", roleNames));
        eventPublisher.publishEvent(
                new EntityAuditEvent(
                        user.getId(),
                        "User",
                        user.getEmail(),
                        EntityTransactionAuditEnum.UPDATE,
                        "roles changed"));
        return userPort.findByIdWithRolesAndPermissions(userId).orElseThrow();
    }
}
