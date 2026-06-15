package com.housi.backend.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoginAttemptServiceTest {

    @Mock StringRedisTemplate redis;
    @Mock ValueOperations<String, String> valueOps;

    LoginAttemptService service;

    @BeforeEach
    void setUp() {
        when(redis.opsForValue()).thenReturn(valueOps);
        service = new LoginAttemptService(redis, 5, 15);
    }

    @Test
    void firstFailureSetsExpiry() {
        when(valueOps.increment("loginAttempts:user@example.com")).thenReturn(1L);

        service.recordFailure("user@example.com");

        verify(redis).expire("loginAttempts:user@example.com", Duration.ofMinutes(15));
    }

    @Test
    void subsequentFailuresDoNotResetExpiry() {
        when(valueOps.increment(anyString())).thenReturn(3L);

        service.recordFailure("user@example.com");

        verify(redis, org.mockito.Mockito.never()).expire(anyString(), any(Duration.class));
    }

    @Test
    void isBlockedWhenAttemptsReachMax() {
        when(valueOps.get("loginAttempts:user@example.com")).thenReturn("5");
        assertThat(service.isBlocked("user@example.com")).isTrue();
    }

    @Test
    void isNotBlockedBelowMax() {
        when(valueOps.get("loginAttempts:user@example.com")).thenReturn("4");
        assertThat(service.isBlocked("user@example.com")).isFalse();
    }

    @Test
    void resetDeletesKey() {
        service.reset("user@example.com");
        verify(redis).delete("loginAttempts:user@example.com");
    }

    @Test
    void keyIsLowercased() {
        when(valueOps.get("loginAttempts:user@example.com")).thenReturn("5");
        assertThat(service.isBlocked("USER@Example.COM")).isTrue();
    }
}
