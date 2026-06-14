package com.housi.backend.service.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.housi.backend.entity.User;
import com.housi.backend.exception.BadRequestException;
import com.housi.backend.exception.ProblemType;
import com.housi.backend.repository.UserRepository;
import com.housi.backend.service.auth.FindAuthenticatedUser;

@Service
public class ChangePasswordService {

    private final FindAuthenticatedUser findAuthenticatedUser;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public ChangePasswordService(
            FindAuthenticatedUser findAuthenticatedUser,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository) {
        this.findAuthenticatedUser = findAuthenticatedUser;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Transactional
    public void updatePassword(String oldPassword, String newPassword, String confirmPassword) {
        User user = findAuthenticatedUser.getAuthenticatedUser();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException(ProblemType.INVALID_PASSWORD, "Current password is incorrect.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new BadRequestException(ProblemType.PASSWORD_MISMATCH, "New passwords do not match.");
        }

        if (oldPassword.equals(newPassword)) {
            throw new BadRequestException(ProblemType.PASSWORD_SAME_AS_CURRENT, "Old and new passwords must be different.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
