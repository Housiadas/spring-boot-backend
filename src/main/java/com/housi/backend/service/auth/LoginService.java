package com.housi.backend.service.auth;

import com.housi.backend.request.v1.AuthenticationRequest;
import com.housi.backend.response.v1.AuthenticationResponse;

public interface LoginService {
    AuthenticationResponse login(AuthenticationRequest request);
}
