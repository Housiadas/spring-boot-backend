package com.housi.backend.service.security;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private static final String KEY_PREFIX = "loginAttempts:";

    private final StringRedisTemplate redis;
    private final int maxAttempts;
    private final Duration window;

    public LoginAttemptServiceImpl(
            StringRedisTemplate redis,
            @Value("${security.login-lockout.max-attempts:5}") int maxAttempts,
            @Value("${security.login-lockout.window-minutes:15}") int windowMinutes) {
        this.redis = redis;
        this.maxAttempts = maxAttempts;
        this.window = Duration.ofMinutes(windowMinutes);
    }

    @Override
    public void recordFailure(String email) {
        String key = key(email);
        Long count = redis.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redis.expire(key, window);
        }
    }

    @Override
    public boolean isBlocked(String email) {
        return currentAttempts(email) >= maxAttempts;
    }

    @Override
    public void reset(String email) {
        redis.delete(key(email));
    }

    @Override
    public long currentAttempts(String email) {
        String value = redis.opsForValue().get(key(email));
        return value == null ? 0L : Long.parseLong(value);
    }

    private String key(String email) {
        return KEY_PREFIX + email.toLowerCase();
    }
}
