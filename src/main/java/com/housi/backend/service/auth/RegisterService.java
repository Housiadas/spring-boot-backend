package com.housi.backend.service.auth;

import com.housi.backend.request.auth.RegisterRequest;

public interface RegisterService {
    void register(RegisterRequest input) throws Exception;
}
