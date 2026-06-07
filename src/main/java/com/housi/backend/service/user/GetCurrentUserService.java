package com.housi.backend.service.user;

import com.housi.backend.response.api.v1.UserResponse;

public interface GetCurrentUserService {
    UserResponse getUserInfo();
}
