package com.housi.backend.usecase.user;

import org.springframework.stereotype.Service;

import com.housi.backend.domain.model.User;
import com.housi.backend.domain.port.out.UserCommandPort;
import com.housi.backend.infrastructure.security.FindAuthenticatedUser;

@Service
public class DeleteUserUseCase {

    private final UserCommandPort userCommandPort;
    private final FindAuthenticatedUser findAuthenticatedUser;
    private final LastAdminGuard lastAdminGuard;

    public DeleteUserUseCase(
            UserCommandPort userCommandPort,
            FindAuthenticatedUser findAuthenticatedUser,
            LastAdminGuard lastAdminGuard) {
        this.userCommandPort = userCommandPort;
        this.findAuthenticatedUser = findAuthenticatedUser;
        this.lastAdminGuard = lastAdminGuard;
    }

    public void deleteUser() {
        User user = findAuthenticatedUser.getAuthenticatedUser();
        lastAdminGuard.assertCanDelete(user);
        userCommandPort.delete(user);
    }
}
