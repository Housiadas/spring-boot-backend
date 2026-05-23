package com.housi.backend.service.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuditLogger {

    private static final Logger log = LoggerFactory.getLogger("AUDIT");

    public void loginSuccess(String email) {
        log.atInfo()
                .setMessage("auth.login.success")
                .addKeyValue("event", "auth.login.success")
                .addKeyValue("email", email)
                .log();
    }

    public void loginFailure(String email, String reason) {
        log.atWarn()
                .setMessage("auth.login.failure")
                .addKeyValue("event", "auth.login.failure")
                .addKeyValue("email", email)
                .addKeyValue("reason", reason)
                .log();
    }

    public void loginBlocked(String email) {
        log.atWarn()
                .setMessage("auth.login.blocked")
                .addKeyValue("event", "auth.login.blocked")
                .addKeyValue("email", email)
                .log();
    }

    public void accessDenied(String email, String method, String path) {
        log.atWarn()
                .setMessage("auth.access.denied")
                .addKeyValue("event", "auth.access.denied")
                .addKeyValue("email", email)
                .addKeyValue("method", method)
                .addKeyValue("path", path)
                .log();
    }

    public void roleCreated(String roleName) {
        log.atInfo()
                .setMessage("rbac.role.created")
                .addKeyValue("event", "rbac.role.created")
                .addKeyValue("role", roleName)
                .log();
    }

    public void roleUpdated(String roleName) {
        log.atInfo()
                .setMessage("rbac.role.updated")
                .addKeyValue("event", "rbac.role.updated")
                .addKeyValue("role", roleName)
                .log();
    }

    public void roleDeleted(String roleName) {
        log.atInfo()
                .setMessage("rbac.role.deleted")
                .addKeyValue("event", "rbac.role.deleted")
                .addKeyValue("role", roleName)
                .log();
    }

    public void permissionCreated(String permissionName) {
        log.atInfo()
                .setMessage("rbac.permission.created")
                .addKeyValue("event", "rbac.permission.created")
                .addKeyValue("permission", permissionName)
                .log();
    }

    public void permissionUpdated(String permissionName) {
        log.atInfo()
                .setMessage("rbac.permission.updated")
                .addKeyValue("event", "rbac.permission.updated")
                .addKeyValue("permission", permissionName)
                .log();
    }

    public void permissionDeleted(String permissionName) {
        log.atInfo()
                .setMessage("rbac.permission.deleted")
                .addKeyValue("event", "rbac.permission.deleted")
                .addKeyValue("permission", permissionName)
                .log();
    }

    public void rolePermissionsChanged(String roleName) {
        log.atInfo()
                .setMessage("rbac.role.permissions.changed")
                .addKeyValue("event", "rbac.role.permissions.changed")
                .addKeyValue("role", roleName)
                .log();
    }

    public void userRolesChanged(String before, String after) {
        log.atInfo()
                .setMessage("rbac.user.roles.changed")
                .addKeyValue("event", "rbac.user.roles.changed")
                .addKeyValue("before", before)
                .addKeyValue("after", after)
                .log();
    }
}
