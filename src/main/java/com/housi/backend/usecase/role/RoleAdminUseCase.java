package com.housi.backend.usecase.role;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.housi.backend.domain.enums.EntityTransactionAuditEnum;
import com.housi.backend.domain.enums.RoleEnum;
import com.housi.backend.domain.event.EntityAuditEvent;
import com.housi.backend.domain.exception.BadRequestException;
import com.housi.backend.domain.exception.ConflictException;
import com.housi.backend.domain.exception.ProblemType;
import com.housi.backend.domain.exception.ResourceNotFoundException;
import com.housi.backend.domain.model.Permission;
import com.housi.backend.domain.model.Role;
import com.housi.backend.domain.port.out.PermissionQueryPort;
import com.housi.backend.domain.port.out.RoleCommandPort;
import com.housi.backend.domain.port.out.RoleQueryPort;
import com.housi.backend.infrastructure.audit.AuditLogger;

@Service
public class RoleAdminUseCase {

    static final Set<String> PROTECTED_ROLES =
            Set.of(RoleEnum.ADMIN.getName(), RoleEnum.USER.getName());

    private final RoleQueryPort roleQueryPort;
    private final RoleCommandPort roleCommandPort;
    private final PermissionQueryPort permissionQueryPort;
    private final AuditLogger auditLogger;
    private final ApplicationEventPublisher eventPublisher;

    public RoleAdminUseCase(
            RoleQueryPort roleQueryPort,
            RoleCommandPort roleCommandPort,
            PermissionQueryPort permissionQueryPort,
            AuditLogger auditLogger,
            ApplicationEventPublisher eventPublisher) {
        this.roleQueryPort = roleQueryPort;
        this.roleCommandPort = roleCommandPort;
        this.permissionQueryPort = permissionQueryPort;
        this.auditLogger = auditLogger;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<Role> getAll() {
        return roleQueryPort.findAllWithPermissions();
    }

    @Transactional(readOnly = true)
    public Role getById(UUID id) {
        return requireRole(id);
    }

    @Transactional
    public Role create(String name, String description, Set<String> permissions) {
        if (roleQueryPort.existsByName(name)) {
            throw new ConflictException(ProblemType.DUPLICATE_ROLE, "Role already exists: " + name);
        }
        Role role = Role.builder()
                .name(name)
                .description(description)
                .permissions(permissions != null && !permissions.isEmpty() ? resolvePermissions(permissions) : new HashSet<>())
                .build();
        Role saved = roleCommandPort.save(role);
        auditLogger.roleCreated(saved.getName());
        eventPublisher.publishEvent(
                new EntityAuditEvent(
                        saved.getId(), "Role", saved.getName(), EntityTransactionAuditEnum.CREATE));
        return saved;
    }

    @Transactional
    public Role update(UUID id, String name, String description, Set<String> permissions) {
        Role role = requireRole(id);
        if (PROTECTED_ROLES.contains(role.getName()) && !role.getName().equals(name)) {
            throw new ConflictException(
                    ProblemType.OPERATION_NOT_ALLOWED,
                    "Cannot rename seeded role: " + role.getName());
        }
        if (!role.getName().equals(name) && roleQueryPort.existsByName(name)) {
            throw new ConflictException(ProblemType.DUPLICATE_ROLE, "Role already exists: " + name);
        }
        Role updated = role.toBuilder()
                .name(name)
                .description(description)
                .permissions(permissions != null ? resolvePermissions(permissions) : role.getPermissions())
                .build();
        Role saved = roleCommandPort.save(updated);
        auditLogger.roleUpdated(saved.getName());
        eventPublisher.publishEvent(
                new EntityAuditEvent(
                        saved.getId(), "Role", saved.getName(), EntityTransactionAuditEnum.UPDATE));
        return saved;
    }

    @Transactional
    public void delete(UUID id) {
        Role role = requireRole(id);
        if (PROTECTED_ROLES.contains(role.getName())) {
            throw new ConflictException(
                    ProblemType.OPERATION_NOT_ALLOWED,
                    "Cannot delete seeded role: " + role.getName());
        }
        if (roleQueryPort.countUsersWithRole(id) > 0) {
            throw new ConflictException(
                    ProblemType.OPERATION_NOT_ALLOWED, "Role is still assigned to users.");
        }
        auditLogger.roleDeleted(role.getName());
        eventPublisher.publishEvent(
                new EntityAuditEvent(
                        role.getId(), "Role", role.getName(), EntityTransactionAuditEnum.DELETE));
        roleCommandPort.delete(role);
    }

    @Transactional
    public Role replacePermissions(UUID id, Set<String> permissionNames) {
        Role role = requireRole(id);
        Role updated = role.toBuilder()
                .permissions(permissionNames == null ? new HashSet<>() : resolvePermissions(permissionNames))
                .build();
        Role saved = roleCommandPort.save(updated);
        auditLogger.rolePermissionsChanged(saved.getName());
        eventPublisher.publishEvent(
                new EntityAuditEvent(
                        saved.getId(),
                        "Role",
                        saved.getName(),
                        EntityTransactionAuditEnum.UPDATE,
                        "permissions changed"));
        return saved;
    }

    private Role requireRole(UUID id) {
        return roleQueryPort.findWithPermissionsById(id)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        ProblemType.ROLE_NOT_FOUND,
                                        "Role with id '" + id + "' not found."));
    }

    private Set<Permission> resolvePermissions(Set<String> names) {
        Set<Permission> resolved = new HashSet<>();
        for (String name : names) {
            Permission p =
                    permissionQueryPort
                            .findByName(name)
                            .orElseThrow(
                                    () ->
                                            new BadRequestException(
                                                    ProblemType.UNKNOWN_PERMISSION,
                                                    "Unknown permission: " + name));
            resolved.add(p);
        }
        return resolved;
    }
}
