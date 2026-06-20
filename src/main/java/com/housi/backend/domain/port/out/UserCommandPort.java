package com.housi.backend.domain.port.out;

import com.housi.backend.domain.model.User;

public interface UserCommandPort {
    User save(User user);

    void delete(User user);
}
