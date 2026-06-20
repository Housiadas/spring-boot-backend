package com.housi.backend.usecase.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.housi.backend.domain.exception.BadRequestException;
import com.housi.backend.domain.exception.ProblemType;
import com.housi.backend.domain.model.User;
import com.housi.backend.domain.port.out.UserPort;
import com.housi.backend.infrastructure.security.FindAuthenticatedUser;

@Service
public class ChangePasswordUseCase {

    private final FindAuthenticatedUser findAuthenticatedUser;
    private final PasswordEncoder passwordEncoder;
    private final UserPort userPort;

    public ChangePasswordUseCase(
            FindAuthenticatedUser findAuthenticatedUser,
            PasswordEncoder passwordEncoder,
            UserPort userPort) {
        this.findAuthenticatedUser = findAuthenticatedUser;
        this.passwordEncoder = passwordEncoder;
        this.userPort = userPort;
    }

    @Transactional
    public void updatePassword(String oldPassword, String newPassword, String confirmPassword) {
        User user = findAuthenticatedUser.getAuthenticatedUser();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException(
                    ProblemType.INVALID_PASSWORD, "Current password is incorrect.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new BadRequestException(
                    ProblemType.PASSWORD_MISMATCH, "New passwords do not match.");
        }

        if (oldPassword.equals(newPassword)) {
            throw new BadRequestException(
                    ProblemType.PASSWORD_SAME_AS_CURRENT,
                    "Old and new passwords must be different.");
        }

        userPort.save(user.toBuilder().password(passwordEncoder.encode(newPassword)).build());
    }
}
