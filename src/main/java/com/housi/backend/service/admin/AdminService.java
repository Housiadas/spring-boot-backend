package com.housi.backend.service.admin;

import java.util.List;

import com.housi.backend.response.UserResponse;

public interface AdminService {
    List<UserResponse> getAllUsers();

    UserResponse promoteToAdmin(long userId);

    void deleteNonAdminUser(long userId);
}
