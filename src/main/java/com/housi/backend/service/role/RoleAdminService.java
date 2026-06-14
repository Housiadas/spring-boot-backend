package com.housi.backend.service.role;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.housi.backend.entity.Permission;
import com.housi.backend.entity.Role;
import com.housi.backend.enums.EntityTransactionAuditEnum;
import com.housi.backend.enums.RoleEnum;
import com.housi.backend.event.EntityAuditEvent;
import com.housi.backend.exception.BadRequestException;
import com.housi.backend.exception.ConflictException;
import com.housi.backend.exception.ProblemType;
import com.housi.backend.exception.ResourceNotFoundException;
import com.housi.backend.repository.PermissionRepository;
import com.housi.backend.repository.RoleRepository;
import com.housi.backend.service.audit.AuditLogger;

@Service
public class RoleAdminService {

    static final Set<String> PROTECTED_ROLES =
            Set.of(RoleEnum.ADMIN.getName(), RoleEnum.USER.getName());

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final AuditLogger auditLogger;
    private final ApplicationEventPublisher eventPublisher;

    public RoleAdminService(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            AuditLogger auditLogger,
            ApplicationEventPublisher eventPublisher) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.auditLogger = auditLogger;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<Role> getAll() {
        return roleRepository.findAllWithPermissions();
    }

    @Transactional(readOnly = true)
    public Role getById(UUID id) {
        return requireRole(id);
    }

    @Transactional
    public Role create(String name, String description, Set<String> permissions) {
        if (roleRepository.existsByName(name)) {
            throw new ConflictException(ProblemType.DUPLICATE_ROLE, "Role already exists: " + name);
        }
        Role role = new Role(name, description);
        if (permissions != null && !permissions.isEmpty()) {
            role.setPermissions(resolvePermissions(permissions));
        }
        Role saved = roleRepository.save(role);
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
            throw new ConflictException(ProblemType.OPERATION_NOT_ALLOWED, "Cannot rename seeded role: " + role.getName());
        }
        if (!role.getName().equals(name) && roleRepository.existsByName(name)) {
            throw new ConflictException(ProblemType.DUPLICATE_ROLE, "Role already exists: " + name);
        }
        role.setName(name);
        role.setDescription(description);
        if (permissions != null) {
            role.setPermissions(resolvePermissions(permissions));
        }
        Role saved = roleRepository.save(role);
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
            throw new ConflictException(ProblemType.OPERATION_NOT_ALLOWED, "Cannot delete seeded role: " + role.getName());
        }
        if (roleRepository.countUsersWithRole(id) > 0) {
            throw new ConflictException(ProblemType.OPERATION_NOT_ALLOWED, "Role is still assigned to users.");
        }
        auditLogger.roleDeleted(role.getName());
        eventPublisher.publishEvent(
                new EntityAuditEvent(
                        role.getId(), "Role", role.getName(), EntityTransactionAuditEnum.DELETE));
        roleRepository.delete(role);
    }

    @Transactional
    public Role replacePermissions(UUID id, Set<String> permissionNames) {
        Role role = requireRole(id);
        role.setPermissions(
                permissionNames == null ? new HashSet<>() : resolvePermissions(permissionNames));
        Role saved = roleRepository.save(role);
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
        return roleRepository
                .findWithPermissionsById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ProblemType.ROLE_NOT_FOUND, "Role with id '" + id + "' not found."));
    }

    private Set<Permission> resolvePermissions(Set<String> names) {
        Set<Permission> resolved = new HashSet<>();
        for (String name : names) {
            Permission p =
                    permissionRepository
                            .findByName(name)
                            .orElseThrow(() -> new BadRequestException(ProblemType.UNKNOWN_PERMISSION, "Unknown permission: " + name));
            resolved.add(p);
        }
        return resolved;
    }
}
