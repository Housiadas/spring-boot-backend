package com.housi.backend.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jspecify.annotations.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogSlowResponseTimeInterceptor implements HandlerInterceptor {

    private static final String EXEC_TIME = "execTime";
    private final int maxResponseTimeToLogInMs;

    public LogSlowResponseTimeInterceptor(final int maxResponseTimeToLogInMs) {
        this.maxResponseTimeToLogInMs = maxResponseTimeToLogInMs;
    }

    @Override
    public boolean preHandle(
            final HttpServletRequest request,
            final @NonNull HttpServletResponse response,
            final @NonNull Object handler) {
        request.setAttribute(EXEC_TIME, System.nanoTime());
        return true;
    }

    @Override
    public void postHandle(
            final HttpServletRequest request,
            final @NonNull HttpServletResponse response,
            final @NonNull Object handler,
            final ModelAndView modelAndView) {
        final Long startTime = (Long) request.getAttribute(EXEC_TIME);
        if (startTime == null) {
            return;
        }

        final long elapsedInNanoS = System.nanoTime() - startTime;
        final long responseTimeInMs = elapsedInNanoS / 1_000_000;
        if (responseTimeInMs < maxResponseTimeToLogInMs) {
            return;
        }

        log.atWarn()
                .setMessage("[SLOW_REQUEST]")
                .addKeyValue(EXEC_TIME, responseTimeInMs)
                .addKeyValue("request.method", request.getMethod())
                .addKeyValue("request.path", request.getRequestURI())
                .addKeyValue("request.query", request.getQueryString())
                .addKeyValue("response.status", response.getStatus())
                .addKeyValue("remote.addr", request.getRemoteAddr())
                .addKeyValue("user.agent", request.getHeader("User-Agent"))
                .log();
    }
}
