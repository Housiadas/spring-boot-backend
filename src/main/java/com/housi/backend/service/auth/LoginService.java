package com.housi.backend.service.auth;

import com.housi.backend.request.api.v1.LoginRequest;
import com.housi.backend.response.api.v1.LoginResponse;

public interface LoginService {
    LoginResponse login(LoginRequest request);
}
