package com.housi.backend.service.auth;

import com.housi.backend.request.AuthenticationRequest;
import com.housi.backend.request.RegisterRequest;
import com.housi.backend.response.AuthenticationResponse;

public interface AuthenticationService {
    void register(RegisterRequest input) throws Exception;

    AuthenticationResponse login(AuthenticationRequest request);
}
