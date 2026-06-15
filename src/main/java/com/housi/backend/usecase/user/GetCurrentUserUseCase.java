package com.housi.backend.usecase.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.housi.backend.domain.model.User;
import com.housi.backend.infrastructure.security.FindAuthenticatedUser;

@Service
public class GetCurrentUserUseCase {

    private final FindAuthenticatedUser findAuthenticatedUser;

    public GetCurrentUserUseCase(FindAuthenticatedUser findAuthenticatedUser) {
        this.findAuthenticatedUser = findAuthenticatedUser;
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        return findAuthenticatedUser.getAuthenticatedUser();
    }
}
