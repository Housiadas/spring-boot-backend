package com.housi.backend.service.userrole;

import java.util.Set;
import java.util.UUID;

import com.housi.backend.response.user.UserResponse;

public interface UserRoleAdminService {
    UserResponse replaceUserRoles(UUID userId, Set<String> roleNames);
}
