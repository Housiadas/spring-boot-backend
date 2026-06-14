package com.housi.backend.service.permission;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.StreamSupport;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.housi.backend.entity.Permission;
import com.housi.backend.enums.EntityTransactionAuditEnum;
import com.housi.backend.event.EntityAuditEvent;
import com.housi.backend.exception.ConflictException;
import com.housi.backend.exception.ProblemType;
import com.housi.backend.exception.ResourceNotFoundException;
import com.housi.backend.repository.PermissionRepository;
import com.housi.backend.service.audit.AuditLogger;

@Service
public class PermissionAdminService {

    static final Set<String> PROTECTED_PERMISSIONS =
            Set.of("user:read", "user:write", "user:delete", "admin:read", "admin:write");

    private final PermissionRepository permissionRepository;
    private final AuditLogger auditLogger;
    private final ApplicationEventPublisher eventPublisher;

    public PermissionAdminService(
            PermissionRepository permissionRepository,
            AuditLogger auditLogger,
            ApplicationEventPublisher eventPublisher) {
        this.permissionRepository = permissionRepository;
        this.auditLogger = auditLogger;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<Permission> getAll() {
        return StreamSupport.stream(permissionRepository.findAll().spliterator(), false).toList();
    }

    @Transactional(readOnly = true)
    public Permission getById(UUID id) {
        return require(id);
    }

    @Transactional
    public Permission create(String name, String description) {
        if (permissionRepository.existsByName(name)) {
            throw new ConflictException(ProblemType.DUPLICATE_PERMISSION, "Permission already exists: " + name);
        }
        Permission saved = permissionRepository.save(new Permission(name, description));
        auditLogger.permissionCreated(saved.getName());
        eventPublisher.publishEvent(
                new EntityAuditEvent(
                        saved.getId(), "Permission", saved.getName(), EntityTransactionAuditEnum.CREATE));
        return saved;
    }

    @Transactional
    public Permission update(UUID id, String name, String description) {
        Permission permission = require(id);
        if (PROTECTED_PERMISSIONS.contains(permission.getName()) && !permission.getName().equals(name)) {
            throw new ConflictException(ProblemType.OPERATION_NOT_ALLOWED, "Cannot rename seeded permission: " + permission.getName());
        }
        if (!permission.getName().equals(name) && permissionRepository.existsByName(name)) {
            throw new ConflictException(ProblemType.DUPLICATE_PERMISSION, "Permission already exists: " + name);
        }
        permission.setName(name);
        permission.setDescription(description);
        Permission saved = permissionRepository.save(permission);
        auditLogger.permissionUpdated(saved.getName());
        eventPublisher.publishEvent(
                new EntityAuditEvent(
                        saved.getId(), "Permission", saved.getName(), EntityTransactionAuditEnum.UPDATE));
        return saved;
    }

    @Transactional
    public void delete(UUID id) {
        Permission permission = require(id);
        if (PROTECTED_PERMISSIONS.contains(permission.getName())) {
            throw new ConflictException(ProblemType.OPERATION_NOT_ALLOWED, "Cannot delete seeded permission: " + permission.getName());
        }
        if (permissionRepository.countRolesWithPermission(id) > 0) {
            throw new ConflictException(ProblemType.OPERATION_NOT_ALLOWED, "Permission is still attached to a role.");
        }
        auditLogger.permissionDeleted(permission.getName());
        eventPublisher.publishEvent(
                new EntityAuditEvent(
                        permission.getId(), "Permission", permission.getName(), EntityTransactionAuditEnum.DELETE));
        permissionRepository.delete(permission);
    }

    private Permission require(UUID id) {
        return permissionRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ProblemType.PERMISSION_NOT_FOUND, "Permission with id '" + id + "' not found."));
    }
}
