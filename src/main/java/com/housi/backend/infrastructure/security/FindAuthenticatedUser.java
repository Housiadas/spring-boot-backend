package com.housi.backend.infrastructure.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.housi.backend.domain.model.User;
import com.housi.backend.infrastructure.persistence.entity.UserEntity;
import com.housi.backend.infrastructure.persistence.mapper.UserPersistenceMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FindAuthenticatedUser {

    private final UserPersistenceMapper mapper;

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("Authentication required");
        }

        return mapper.toDomain((UserEntity) authentication.getPrincipal());
    }
}
