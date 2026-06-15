package com.housi.backend.usecase.user;

import org.springframework.stereotype.Service;

import com.housi.backend.domain.model.User;
import com.housi.backend.domain.port.out.UserPort;
import com.housi.backend.infrastructure.security.FindAuthenticatedUser;

@Service
public class DeleteUserUseCase {

    private final UserPort userPort;
    private final FindAuthenticatedUser findAuthenticatedUser;
    private final LastAdminGuard lastAdminGuard;

    public DeleteUserUseCase(
            UserPort userPort,
            FindAuthenticatedUser findAuthenticatedUser,
            LastAdminGuard lastAdminGuard) {
        this.userPort = userPort;
        this.findAuthenticatedUser = findAuthenticatedUser;
        this.lastAdminGuard = lastAdminGuard;
    }

    public void deleteUser() {
        User user = findAuthenticatedUser.getAuthenticatedUser();
        lastAdminGuard.assertCanDelete(user);
        userPort.delete(user);
    }
}
