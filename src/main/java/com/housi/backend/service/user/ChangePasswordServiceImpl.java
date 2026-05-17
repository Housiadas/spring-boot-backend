package com.housi.backend.service.user;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.housi.backend.entity.User;
import com.housi.backend.repository.UserRepository;
import com.housi.backend.request.v1.PasswordUpdateRequest;
import com.housi.backend.service.auth.FindAuthenticatedUser;

@Service
public class ChangePasswordServiceImpl implements ChangePasswordService {

    private final FindAuthenticatedUser findAuthenticatedUser;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public ChangePasswordServiceImpl(
            FindAuthenticatedUser findAuthenticatedUser,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository) {
        this.findAuthenticatedUser = findAuthenticatedUser;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void updatePassword(PasswordUpdateRequest passwordUpdateRequest) {
        User user = findAuthenticatedUser.getAuthenticatedUser();

        if (!isOldPasswordCorrect(user.getPassword(), passwordUpdateRequest.getOldPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }

        if (!isNewPasswordConfirmed(
                passwordUpdateRequest.getNewPassword(), passwordUpdateRequest.getNewPassword2())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New passwords do not match");
        }

        if (!isNewPasswordDifferent(
                passwordUpdateRequest.getOldPassword(), passwordUpdateRequest.getNewPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Old and new passwords must be different");
        }

        user.setPassword(passwordEncoder.encode(passwordUpdateRequest.getNewPassword()));
        userRepository.save(user);
    }

    private boolean isOldPasswordCorrect(String currentPassword, String oldPassword) {
        return passwordEncoder.matches(oldPassword, currentPassword);
    }

    private boolean isNewPasswordConfirmed(String newPassword, String newPasswordConfirmation) {
        return newPassword.equals(newPasswordConfirmation);
    }

    private boolean isNewPasswordDifferent(String oldPassword, String newPassword) {
        return !oldPassword.equals(newPassword);
    }
}
