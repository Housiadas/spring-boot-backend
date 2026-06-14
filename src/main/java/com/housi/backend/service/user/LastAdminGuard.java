package com.housi.backend.service.user;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.housi.backend.entity.User;
import com.housi.backend.enums.RoleEnum;
import com.housi.backend.exception.ConflictException;
import com.housi.backend.exception.ProblemType;
import com.housi.backend.repository.UserRepository;

@Component
public class LastAdminGuard {

    private final UserRepository userRepository;

    public LastAdminGuard(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void assertCanDelete(User user) {
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(r -> RoleEnum.ADMIN.getName().equals(r.getName()));
        if (isAdmin && userRepository.countAdminUsers() <= 1) {
            throw new ConflictException(ProblemType.OPERATION_NOT_ALLOWED,
                    "Cannot delete the last admin account.");
        }
    }

    public void assertAdminRoleRemovalAllowed(User user, Set<String> newRoleNames) {
        boolean wasAdmin = user.getRoles().stream()
                .anyMatch(r -> RoleEnum.ADMIN.getName().equals(r.getName()));
        boolean willBeAdmin = newRoleNames.contains(RoleEnum.ADMIN.getName());
        if (wasAdmin && !willBeAdmin && userRepository.countAdminUsers() <= 1) {
            throw new ConflictException(ProblemType.OPERATION_NOT_ALLOWED,
                    "Cannot remove ADMIN role from the last admin user.");
        }
    }
}
