package com.housi.backend.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.housi.backend.entity.User;
import com.housi.backend.service.auth.FindAuthenticatedUser;

@Service
public class GetCurrentUserService {

    private final FindAuthenticatedUser findAuthenticatedUser;

    public GetCurrentUserService(FindAuthenticatedUser findAuthenticatedUser) {
        this.findAuthenticatedUser = findAuthenticatedUser;
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        return findAuthenticatedUser.getAuthenticatedUser();
    }
}
