package com.housi.backend.service.auth;

import com.housi.backend.controller.request.v1.LoginRequest;
import com.housi.backend.controller.response.v1.LoginResponse;

public interface LoginService {
    LoginResponse login(LoginRequest request);
}
