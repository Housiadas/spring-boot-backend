package com.housi.backend.service.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuditLogger {

    private static final Logger log = LoggerFactory.getLogger("AUDIT");

    public void loginSuccess(String email) {
        log.info("event=auth.login.success email={}", email);
    }

    public void loginFailure(String email, String reason) {
        log.warn("event=auth.login.failure email={} reason={}", email, reason);
    }

    public void loginBlocked(String email) {
        log.warn("event=auth.login.blocked email={}", email);
    }

    public void accessDenied(String email, String method, String path) {
        log.warn("event=auth.access.denied email={} method={} path={}", email, method, path);
    }

    public void roleCreated(String roleName) {
        log.info("event=rbac.role.created actor={} role={}", actor(), roleName);
    }

    public void roleUpdated(String roleName) {
        log.info("event=rbac.role.updated actor={} role={}", actor(), roleName);
    }

    public void roleDeleted(String roleName) {
        log.info("event=rbac.role.deleted actor={} role={}", actor(), roleName);
    }

    public void permissionCreated(String permissionName) {
        log.info("event=rbac.permission.created actor={} permission={}", actor(), permissionName);
    }

    public void permissionUpdated(String permissionName) {
        log.info("event=rbac.permission.updated actor={} permission={}", actor(), permissionName);
    }

    public void permissionDeleted(String permissionName) {
        log.info("event=rbac.permission.deleted actor={} permission={}", actor(), permissionName);
    }

    public void rolePermissionsChanged(String roleName) {
        log.info("event=rbac.role.permissions.changed actor={} role={}", actor(), roleName);
    }

    public void userRolesChanged(String userId, String before, String after) {
        log.info(
                "event=rbac.user.roles.changed actor={} user={} before={} after={}",
                actor(),
                userId,
                before,
                after);
    }

    private String actor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? "anonymous" : auth.getName();
    }
}
