package com.housi.backend.service.auth;

import com.luv2code.springboot.todos.entity.User;

public interface FindAuthenticatedUser {
    User getAuthenticatedUser();
}
