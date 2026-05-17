package com.housi.backend.service.user;

import com.housi.backend.request.v1.PasswordUpdateRequest;

public interface ChangePasswordService {
    void updatePassword(PasswordUpdateRequest passwordUpdateRequest);
}
