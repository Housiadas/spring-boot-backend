package com.housi.backend.service.auth;

import com.housi.backend.request.v1.RegisterRequest;

public interface RegisterService {
    void register(RegisterRequest input) throws Exception;
}
