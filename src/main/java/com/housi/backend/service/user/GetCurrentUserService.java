package com.housi.backend.service.user;

import com.housi.backend.response.v1.UserResponse;

public interface GetCurrentUserService {
    UserResponse getUserInfo();
}
