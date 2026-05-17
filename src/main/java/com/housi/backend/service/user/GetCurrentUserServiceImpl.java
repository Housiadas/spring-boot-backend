package com.housi.backend.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.housi.backend.entity.Authority;
import com.housi.backend.entity.User;
import com.housi.backend.response.v1.UserResponse;
import com.housi.backend.service.auth.FindAuthenticatedUser;

@Service
public class GetCurrentUserServiceImpl implements GetCurrentUserService {
    private final FindAuthenticatedUser findAuthenticatedUser;

    public GetCurrentUserServiceImpl(FindAuthenticatedUser findAuthenticatedUser) {
        this.findAuthenticatedUser = findAuthenticatedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserInfo() {
        User user = this.findAuthenticatedUser.getAuthenticatedUser();
        return new UserResponse(
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                user.getAuthorities().stream().map(auth -> (Authority) auth).toList());
    }
}
