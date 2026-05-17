package com.housi.backend.service.security;

public interface LoginAttemptService {
    void recordFailure(String email);

    boolean isBlocked(String email);

    void reset(String email);

    long currentAttempts(String email);
}
