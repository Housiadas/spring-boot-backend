package com.housi.backend.domain.port.out;

import com.housi.backend.domain.model.Role;

public interface RoleCommandPort {
    Role save(Role role);

    void delete(Role role);
}
