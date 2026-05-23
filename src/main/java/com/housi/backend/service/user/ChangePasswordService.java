package com.housi.backend.service.user;

import com.housi.backend.request.v1.user.PasswordUpdateRequest;

public interface ChangePasswordService {
    void updatePassword(PasswordUpdateRequest passwordUpdateRequest);
}
