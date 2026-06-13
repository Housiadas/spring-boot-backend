package com.housi.backend.service.auth;

import com.housi.backend.controller.request.v1.RegisterRequest;

public interface RegisterService {
    void register(RegisterRequest input) throws Exception;
}
