package com.housi.backend.service.user;

import org.springframework.stereotype.Service;

import com.housi.backend.entity.User;
import com.housi.backend.repository.UserRepository;
import com.housi.backend.service.auth.FindAuthenticatedUser;

@Service
public class DeleteUserService {

    private final UserRepository userRepository;
    private final FindAuthenticatedUser findAuthenticatedUser;
    private final LastAdminGuard lastAdminGuard;

    public DeleteUserService(
            UserRepository userRepository,
            FindAuthenticatedUser findAuthenticatedUser,
            LastAdminGuard lastAdminGuard) {
        this.userRepository = userRepository;
        this.findAuthenticatedUser = findAuthenticatedUser;
        this.lastAdminGuard = lastAdminGuard;
    }

    public void deleteUser() {
        User user = findAuthenticatedUser.getAuthenticatedUser();
        lastAdminGuard.assertCanDelete(user);
        userRepository.delete(user);
    }
}
