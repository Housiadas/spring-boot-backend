package com.housi.backend.service.permission;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.StreamSupport;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.housi.backend.entity.Permission;
import com.housi.backend.repository.PermissionRepository;
import com.housi.backend.request.backoffice.v1.PermissionRequest;
import com.housi.backend.response.backoffice.v1.PermissionResponse;
import com.housi.backend.service.audit.AuditLogger;

@Service
public class PermissionAdminServiceImpl implements PermissionAdminService {

    static final Set<String> PROTECTED_PERMISSIONS =
            Set.of("user:read", "user:write", "user:delete", "admin:read", "admin:write");

    private final PermissionRepository permissionRepository;
    private final AuditLogger auditLogger;

    public PermissionAdminServiceImpl(
            PermissionRepository permissionRepository, AuditLogger auditLogger) {
        this.permissionRepository = permissionRepository;
        this.auditLogger = auditLogger;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAll() {
        return StreamSupport.stream(permissionRepository.findAll().spliterator(), false)
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse getById(UUID id) {
        return toResponse(require(id));
    }

    @Override
    @Transactional
    public PermissionResponse create(PermissionRequest request) {
        if (permissionRepository.existsByName(request.name())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Permission already exists: " + request.name());
        }
        Permission saved =
                permissionRepository.save(
                        new Permission(request.name(), request.description()));
        auditLogger.permissionCreated(saved.getName());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public PermissionResponse update(UUID id, PermissionRequest request) {
        Permission permission = require(id);
        if (PROTECTED_PERMISSIONS.contains(permission.getName())
                && !permission.getName().equals(request.name())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot rename seeded permission: " + permission.getName());
        }
        if (!permission.getName().equals(request.name())
                && permissionRepository.existsByName(request.name())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Permission already exists: " + request.name());
        }
        permission.setName(request.name());
        permission.setDescription(request.description());
        Permission saved = permissionRepository.save(permission);
        auditLogger.permissionUpdated(saved.getName());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Permission permission = require(id);
        if (PROTECTED_PERMISSIONS.contains(permission.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete seeded permission: " + permission.getName());
        }
        if (permissionRepository.countRolesWithPermission(id) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Permission is still attached to a role");
        }
        auditLogger.permissionDeleted(permission.getName());
        permissionRepository.delete(permission);
    }

    private Permission require(UUID id) {
        return permissionRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND, "Permission not found"));
    }

    private PermissionResponse toResponse(Permission p) {
        return new PermissionResponse(p.getId(), p.getName(), p.getDescription());
    }
}
