package com.housi.backend.service.role;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.housi.backend.entity.Permission;
import com.housi.backend.entity.Role;
import com.housi.backend.repository.PermissionRepository;
import com.housi.backend.repository.RoleRepository;
import com.housi.backend.controller.request.v1.RoleRequest;
import com.housi.backend.controller.response.v1.RoleResponse;
import com.housi.backend.service.audit.AuditLogger;

@Service
public class RoleAdminServiceImpl implements RoleAdminService {

    static final Set<String> PROTECTED_ROLES = Set.of("ROLE_ADMIN", "ROLE_USER");

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final AuditLogger auditLogger;

    public RoleAdminServiceImpl(
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            AuditLogger auditLogger) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.auditLogger = auditLogger;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAll() {
        return roleRepository.findAllWithPermissions().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getById(UUID id) {
        return toResponse(requireRole(id));
    }

    @Override
    @Transactional
    public RoleResponse create(RoleRequest request) {
        if (roleRepository.existsByName(request.name())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Role already exists: " + request.name());
        }
        Role role = new Role(request.name(), request.description());
        if (request.permissions() != null && !request.permissions().isEmpty()) {
            role.setPermissions(resolvePermissions(request.permissions()));
        }
        Role saved = roleRepository.save(role);
        auditLogger.roleCreated(saved.getName());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public RoleResponse update(UUID id, RoleRequest request) {
        Role role = requireRole(id);
        if (PROTECTED_ROLES.contains(role.getName()) && !role.getName().equals(request.name())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Cannot rename seeded role: " + role.getName());
        }
        if (!role.getName().equals(request.name())
                && roleRepository.existsByName(request.name())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Role already exists: " + request.name());
        }
        role.setName(request.name());
        role.setDescription(request.description());
        if (request.permissions() != null) {
            role.setPermissions(resolvePermissions(request.permissions()));
        }
        Role saved = roleRepository.save(role);
        auditLogger.roleUpdated(saved.getName());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Role role = requireRole(id);
        if (PROTECTED_ROLES.contains(role.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Cannot delete seeded role: " + role.getName());
        }
        if (roleRepository.countUsersWithRole(id) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Role is still assigned to users");
        }
        auditLogger.roleDeleted(role.getName());
        roleRepository.delete(role);
    }

    @Override
    @Transactional
    public RoleResponse replacePermissions(UUID id, Set<String> permissionNames) {
        Role role = requireRole(id);
        role.setPermissions(
                permissionNames == null ? new HashSet<>() : resolvePermissions(permissionNames));
        Role saved = roleRepository.save(role);
        auditLogger.rolePermissionsChanged(saved.getName());
        return toResponse(saved);
    }

    private Role requireRole(UUID id) {
        return roleRepository
                .findWithPermissionsById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));
    }

    private Set<Permission> resolvePermissions(Set<String> names) {
        Set<Permission> resolved = new HashSet<>();
        for (String name : names) {
            Permission p =
                    permissionRepository
                            .findByName(name)
                            .orElseThrow(
                                    () ->
                                            new ResponseStatusException(
                                                    HttpStatus.BAD_REQUEST,
                                                    "Unknown permission: " + name));
            resolved.add(p);
        }
        return resolved;
    }

    private RoleResponse toResponse(Role role) {
        Set<String> perms =
                role.getPermissions().stream().map(Permission::getName).collect(Collectors.toSet());
        return new RoleResponse(role.getId(), role.getName(), role.getDescription(), perms);
    }
}
