package com.housi.backend.service.auth;

import com.housi.backend.request.v1.auth.LoginRequest;
import com.housi.backend.response.v1.auth.LoginResponse;

public interface LoginService {
    LoginResponse login(LoginRequest request);
}
