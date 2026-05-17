package com.housi.backend.service.user;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.housi.backend.entity.User;
import com.housi.backend.repository.UserRepository;
import com.housi.backend.service.auth.FindAuthenticatedUser;

@Service
public class DeleteUserServiceImpl implements DeleteUserService {

    private final UserRepository userRepository;
    private final FindAuthenticatedUser findAuthenticatedUser;

    public DeleteUserServiceImpl(
            UserRepository userRepository, FindAuthenticatedUser findAuthenticatedUser) {
        this.userRepository = userRepository;
        this.findAuthenticatedUser = findAuthenticatedUser;
    }

    @Override
    public void deleteUser() {
        User user = findAuthenticatedUser.getAuthenticatedUser();

        if (isLastAdmin(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin cannot delete itself");
        }

        userRepository.delete(user);
    }

    private boolean isLastAdmin(User user) {
        boolean isAdmin =
                user.getAuthorities().stream()
                        .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

        if (isAdmin) {
            long adminCount = userRepository.countAdminUsers();
            return adminCount <= 1;
        }

        return false;
    }
}
