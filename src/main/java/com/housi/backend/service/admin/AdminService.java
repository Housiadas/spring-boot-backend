package com.housi.backend.service.admin;

import java.util.List;
import java.util.UUID;

import com.housi.backend.response.v1.UserResponse;

public interface AdminService {
    List<UserResponse> getAllUsers();

    UserResponse promoteToAdmin(UUID userId);

    void deleteNonAdminUser(UUID userId);
}
