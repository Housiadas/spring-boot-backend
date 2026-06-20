package com.housi.backend.usecase.user;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.housi.backend.domain.enums.RoleEnum;
import com.housi.backend.domain.exception.ConflictException;
import com.housi.backend.domain.exception.ProblemType;
import com.housi.backend.domain.model.User;
import com.housi.backend.domain.port.out.UserPort;

@Component
public class LastAdminGuard {

    private final UserPort userPort;

    public LastAdminGuard(UserPort userPort) {
        this.userPort = userPort;
    }

    public void assertCanDelete(User user) {
        boolean isAdmin =
                user.getRoles().stream()
                        .anyMatch(r -> RoleEnum.ADMIN.getName().equals(r.getName()));
        if (isAdmin && userPort.countAdminUsers() <= 1) {
            throw new ConflictException(
                    ProblemType.OPERATION_NOT_ALLOWED, "Cannot delete the last admin account.");
        }
    }

    public void assertAdminRoleRemovalAllowed(User user, Set<String> newRoleNames) {
        boolean wasAdmin =
                user.getRoles().stream()
                        .anyMatch(r -> RoleEnum.ADMIN.getName().equals(r.getName()));
        boolean willBeAdmin = newRoleNames.contains(RoleEnum.ADMIN.getName());
        if (wasAdmin && !willBeAdmin && userPort.countAdminUsers() <= 1) {
            throw new ConflictException(
                    ProblemType.OPERATION_NOT_ALLOWED,
                    "Cannot remove ADMIN role from the last admin user.");
        }
    }
}
