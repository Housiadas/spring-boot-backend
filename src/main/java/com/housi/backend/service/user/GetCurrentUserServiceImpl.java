package com.housi.backend.service.user;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.housi.backend.entity.Role;
import com.housi.backend.entity.User;
import com.housi.backend.controller.response.v1.UserResponse;
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
        Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        Set<String> permissions =
                user.getRoles().stream()
                        .flatMap(r -> r.getPermissions().stream())
                        .map(p -> p.getName())
                        .collect(Collectors.toSet());
        return new UserResponse(
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                roles,
                permissions);
    }
}
