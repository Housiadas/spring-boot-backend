package com.housi.backend.service.user;

import com.housi.backend.request.PasswordUpdateRequest;
import com.housi.backend.response.UserResponse;

public interface UserService {
    UserResponse getUserInfo();

    void deleteUser();

    void updatePassword(PasswordUpdateRequest passwordUpdateRequest);
}
