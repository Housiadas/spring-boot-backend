package com.housi.backend.service.auth;

import com.housi.backend.entity.User;

public interface FindAuthenticatedUser {
    User getAuthenticatedUser();
}
